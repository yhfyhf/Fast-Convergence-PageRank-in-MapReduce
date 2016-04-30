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
     * valueIn:NODEINFO;uId;vId1, vId2, ...;pageRank;
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

        Queue<Node> heap = new PriorityQueue<>(2, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int)(o1.getNewPageRank() - o2.getNewPageRank());
            }
        });


        for (Node node : nodesMap.values()) {
            Text valuesOut = new Text(node.getId() + ";" + node.getDesNodeId() + ";" + node.getNewPageRank());
            context.write(new Text(""), valuesOut);

            residualAll += Math.abs(node.getOldPageRank() - node.getNewPageRank()) / node.getNewPageRank();
        }
        context.getCounter(Counter.RESIDUAL_COUNTER).increment((long) residualAll * Conf.MULTIPLE);


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

//            if (v.getId() < 3) {
//                System.out.println("!!before" + v.getId()+": "+v.getNewPageRank());
//            }
        }

        //damping factor and residual
        float residuals = 0.0f;
        for (Node v : nodesMap.values()) {
            v.setNewPageRank(v.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR);
            float startPageRank = startPageRankMap.get(v.getId());
            float endPageRank = v.getNewPageRank();
            residuals += Math.abs(startPageRank - endPageRank) / endPageRank;

//            if (v.getId() < 3) {
//                System.out.println("!!after" + v.getId()+": "+v.getNewPageRank());
//            }

        }

        return residuals / nodesMap.size();

    }




}



