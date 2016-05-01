package BlockPageRank;

import Conf.Conf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Christina on 4/30/16.
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    int blockId;
    /**
     * KeyIn: blockId
     * valueIn: BE;vId;uId;
     *
     * KeyIn: blockId
     * valueIn: BC;vId;NextPageRank
     *
     * KeyIn: blockId
     * valueIn: NODEINFO;uId;vId1, vId2, ...;pageRank;
     *
     * KeyOur:
     * ValueOut: uId;vId1, vId2, ...;pageRank;
     *
     */
    protected void reduce(Text keyIn, Iterable<Text> valuesIn, Context context)
            throws IOException, InterruptedException {
        Map<Integer, Node> nodesMap = new HashMap<>();
        blockId = Integer.parseInt(keyIn.toString());

        while (valuesIn.iterator().hasNext()) {

            Text valueIn = valuesIn.iterator().next();
            String[] tokens = valueIn.toString().trim().split(";");
            int nodeId = Integer.parseInt(tokens[1].trim());
            if (!nodesMap.containsKey(nodeId)) {
                nodesMap.put(nodeId, new Node(nodeId));
            }
            Node node = nodesMap.get(nodeId);

            switch (Integer.parseInt(tokens[0])) {
                case Conf.NODEINFO:
                    node.setDesNodeIds(tokens[2].trim());
                    node.setOldPageRank(Float.valueOf(tokens[3].trim()));
                    node.setNewPageRank(Float.valueOf(tokens[3].trim()));
                    break;
                case Conf.BE:
                    node.addBE(Integer.parseInt(tokens[2].trim()));
                    break;
                case Conf.BC:
                    node.addBC(Float.parseFloat(tokens[2].trim()));
                    break;
            }

        }

        int iterNum = 0;
        float residual = Float.MAX_VALUE;
        while (iterNum < Conf.INBLOCK_ITERRATION && residual > Conf.RESIDUAL_ERROR) {
            iterNum++;
            residual = iterateBlockOnce(nodesMap);
            context.getCounter(Counter.INBLOCK_INTER_COUNTER).increment(1);
            context.getCounter(Counter.values()[blockId]).increment(1);
        }

        float residualAll = 0.0f;

        Integer lowestNodeId1 = null, lowestNodeId2 = null;

        for (Node node : nodesMap.values()) {
            Text valuesOut = new Text(node.getId() + ";" + node.getDesNodeId() + ";" + node.getNewPageRank());
            context.write(new Text(""), valuesOut);

            residualAll += Math.abs(node.getOldPageRank() - node.getNewPageRank()) / node.getNewPageRank();

            if (lowestNodeId1 == null) {
                lowestNodeId1 = node.getId();
            } else if (lowestNodeId1 > node.getId()) {
                lowestNodeId2 = lowestNodeId1;
                lowestNodeId1 = node.getId();
            } else if (lowestNodeId2 == null || lowestNodeId2 > node.getId()) {
                lowestNodeId2 = node.getId();
            }

        }
        context.getCounter(Counter.RESIDUAL_COUNTER).increment((long) residualAll * Conf.MULTIPLE);
        System.out.println("blockId:" + blockId + "  nodeId:" + lowestNodeId1 + ", pr:" + nodesMap.get(lowestNodeId1).getNewPageRank());
        System.out.println("blockId:" + blockId + "  nodeId:" + lowestNodeId2 + ", pr:" + nodesMap.get(lowestNodeId2).getNewPageRank());
    }

    protected float iterateBlockOnce(Map<Integer, Node> nodesMap) {
        Map<Integer, Float> startPageRankMap = new HashMap<>();
        for (Node node : nodesMap.values()) {
            startPageRankMap.put(node.getId(), node.getNewPageRank());
            if (node.getDegree() != 0) {
                node.setNextPageRank(node.getNewPageRank() / node.getDegree());
            }
        }

        for (Node v : nodesMap.values()) {
            //BC
            v.setNewPageRank(v.getBC());

            //BE
            for (int u : v.getBE()) {
                v.addNewPageRank(nodesMap.get(u).getNextPageRank());
            }
        }

        //damping factor and residual
        float residuals = 0.0f;
        for (Node v : nodesMap.values()) {
            v.setNewPageRank(v.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR);
            float startPageRank = startPageRankMap.get(v.getId());
            float endPageRank = v.getNewPageRank();
            residuals += Math.abs(startPageRank - endPageRank) / endPageRank;
        }
        return residuals / nodesMap.size();
    }
}
