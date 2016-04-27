package GaussPageRank;

import Conf.Conf;
import Conf.LoggerConf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    private Logger log = LoggerConf.getWarningLogger();


    /**
     * keyIn: blockId
     * valueIn: NEXTPAGERANK_FROM_INBLOCK;desNodeId;nextPageRank;
     * Or
     * keyIn: blockId
     * valueIn: NEXTPAGERANK_FROM_OUTBLOCK;desNodeId;nextPageRank;
     * Or
     * keyIn: blockId
     * valueIn: NODEINFO;srcNodeId;desNodeId1,desNodeId2...;srcOldNodePageRank;
     * Or
     * keyIn:blockId
     * valueIn: EDGE_INCBLOCK;srcNodeId;desNodeIdInBlock1,desNodeIdInBlock2...;
     *
     * keyIn:
     * valueIn: srcNodeId;desNodeId1,desNodeId2...;srcNodeDegree;srcNodePageRank;
     */
    protected void reduce(Text keyIn, Iterable<Text> valuesIn, Context context)
            throws IOException, InterruptedException {
        Map<Integer, Node> nodesMap = new HashMap<>();
        System.out.println("!!This reducer blockId: " + keyIn);
        long blockId = Long.parseLong(keyIn.toString());
        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            System.out.println("!!keyIn: " + keyIn.toString() + " valueIn: " + valueIn.toString());
            String[] temp = valueIn.toString().split(";");
            int nodeId = Integer.parseInt(temp[1].trim());
            nodesMap.putIfAbsent(nodeId, new Node(nodeId));
            Node node = nodesMap.get(nodeId);
            switch (Integer.parseInt(temp[0])) {
                case Conf.NODEINFO:
                    node.setDesNodeIds(temp[2].trim());
                    node.setOldPageRank(Float.parseFloat(temp[3].trim()));
                    break;
                case Conf.NEXTPAGERANK_FROM_INBLOCK:
                    node.addNewPageRank(Float.parseFloat(temp[2].trim()));
                    break;
                case Conf.NEXTPAGERANK_FROM_OUTBLOCK:
                    node.addNewPageRank(Float.parseFloat(temp[2].trim()));
                    node.addPageRankFromOutBlock(Float.parseFloat(temp[2].trim()));
                    break;
                case Conf.EDGE_INCBLOCK:
                    //check if it has edge inBlock
                    if (temp.length > 2) {
                        node.setDesNodeInBlock(temp[2]);
                    }
                    break;
            }
        }

        int iterNum = 0;
        float residualErr = Float.MAX_VALUE;
        while (iterNum++ < Conf.INBLOCK_ITERRATION && residualErr > Conf.RESIDUAL_ERROR) {
            residualErr = iterateBlockOnce(nodesMap);
            System.out.println("!!!residual: " + residualErr);
        }

        Queue<Node> heap = new PriorityQueue<>(2, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int)(o1.getNewPageRank() - o2.getNewPageRank());
            }
        });

        Text keyOut = new Text("");
        Text valueOut;
        for (int nodeId : nodesMap.keySet()) {
            Node node = nodesMap.get(nodeId);
            heap.add(node);

            valueOut = new Text(nodeId + ";" + node.getDesNodeId() + ";" + node.getNewPageRank() + ";");
            context.write(keyOut, valueOut);

            long residual = (long) (Math.abs(node.getOldPageRank() - node.getNewPageRank()) * Conf.MULTIPLE / node.getNewPageRank());
            context.getCounter(Counter.RESIDUAL_COUNTER).increment(residual);
            log.severe("valueOut: " + valueOut + ", residual = " + residual + ", prevPR = " + node.getOldPageRank() + ", newPR = " + node.getNewPageRank());
            log.info("[ Reducer ] key: " + keyOut + "value: " + valueOut);

            context.getCounter(Counter.RESIDUAL_COUNTER).increment(
                    (long) (Math.abs(node.getNewPageRank() - node.getOldPageRank()) * Conf.MULTIPLE));

            System.out.println("[ PRReducer ] key: " + keyOut + "value: " + valueOut);
        }

        // TODO : it should be written into text
        //get two lowest pagerank nodes
        System.out.println("Lowest 1 node:  blockid:" + blockId + "  nodeid:" + heap.poll().getId());
        System.out.println("Lowest 2 node:  blockid:" + blockId + "  nodeid:" + heap.poll().getId());
    }

    /**
     * newPageRank = nextPageRank from inBlock nodes + nextPageRank from outBlock nodes
     * The nextPageRank from outBlock nodes is constant, so reset the newPageRank = nextPageRank from outBlock
     * then add the nextPageRank from inBlock
     * */
    protected float iterateBlockOnce(Map<Integer, Node> nodesMap) {
        float residuals = 0;

        //set nextPageRank and newPageRank for each node
        for (Node node : nodesMap.values()) {
            //check if the node has desNode
            if (!node.getDesNodeId().isEmpty()) {
                node.setNextPageRank(node.getNewPageRank() / node.getDegree());
                node.setNewPageRank(node.getPageRankFromOutBlock());
            }
        }

        //get the updated newPageRank considering the nextPageRank from inBlock nodes
        for (Node srcNode : nodesMap.values()) {
            //check if the node has desNodeInBlock
            if (srcNode.getDesNodeInBlock().isEmpty()) {
                continue;
            }
            String[] desNodeIds = srcNode.getDesNodeInBlock().split(",");
            float nextPageRank = srcNode.getNextPageRank();

            for (String desNodeIdString : desNodeIds) {
                int desNodeId = Integer.valueOf(desNodeIdString);
                Node desNode = nodesMap.get(desNodeId);
                desNode.addNewPageRank(nextPageRank);
            }
        }

        //update newPageRank considering the damping factor and calculate the residual
        for (Node node : nodesMap.values()) {
            float updatedPageRank = node.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR;
            node.setNewPageRank(updatedPageRank);
            float oldPageRank = nodesMap.get(node.getId()).getOldPageRank();
            float newPageRank = node.getNewPageRank();
            residuals += Math.abs(oldPageRank - newPageRank) / newPageRank;
        }

        //return the avg of residuals
        return residuals / nodesMap.size();
    }

}




