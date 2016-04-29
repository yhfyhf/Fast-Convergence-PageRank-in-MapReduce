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



    long blockId;

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
        log.info("!!This reducer blockId: " + keyIn);
        blockId = Long.parseLong(keyIn.toString());
        Map<Integer, Node> nodesMap = new HashMap<>();
        Map<Integer, List<Node>> srcNodesInBlockMap = new HashMap<>(); //key is the desNodeId, value is the list of desNode

        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            log.info("!!keyIn: " + keyIn.toString() + " valueIn: " + valueIn.toString());
            String[] tokens = valueIn.toString().split(";");
            int nodeId = Integer.parseInt(tokens[1].trim());
            if (!nodesMap.containsKey(nodeId)) {
                nodesMap.put(nodeId, new Node(nodeId));
            }
            Node node = nodesMap.get(nodeId);
            switch (Integer.parseInt(tokens[0])) {
                case Conf.NODEINFO:
                    node.setDesNodeIds(tokens[2].trim());
                    node.setOldPageRank(Float.parseFloat(tokens[3].trim()));
                    break;
                case Conf.NEXTPAGERANK_FROM_INBLOCK:
                    node.addNewPageRank(Float.parseFloat(tokens[2].trim()));
                    break;
                case Conf.NEXTPAGERANK_FROM_OUTBLOCK:
                    node.addNewPageRank(Float.parseFloat(tokens[2].trim()));
                    node.addPageRankFromOutBlock(Float.parseFloat(tokens[2].trim()));
                    break;
                case Conf.EDGE_INCBLOCK:
                    // check if it has edge inBlock and if so store the source node inBlock
                    if (tokens.length > 2) {
                        node.setDesNodeInBlock(tokens[2]);

                        String[] desNodeIdsStr = tokens[2].split(",");
                        for (String desNodeIdStr : desNodeIdsStr) {
                            int desNodeId = Integer.valueOf(desNodeIdStr);
                            if (!srcNodesInBlockMap.containsKey(desNodeId)) {
                                srcNodesInBlockMap.put(desNodeId, new ArrayList<Node>());
                            }
                            srcNodesInBlockMap.get(desNodeId).add(node);
                        }

                    }
                    break;
            }
        }

        for (Node node : nodesMap.values()) {
            node.setNewPageRank(node.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR);
        }

        int iterNum = 0;
        float residualErr = Float.MAX_VALUE;
        while (iterNum++ < Conf.INBLOCK_ITERRATION && residualErr > Conf.RESIDUAL_ERROR) {
            context.getCounter(Counter.INBLOCK_INTER_COUNTER).increment(1);
            residualErr = iterateBlockOnce(nodesMap, srcNodesInBlockMap);

//            System.out.println("blockId: " + keyIn + ", iterNum: " + iterNum + ", residual: " + residualErr);
        }

        Text keyOut = new Text("");
        Text valueOut;

        Queue<Node> heap = new PriorityQueue<>(2, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int)(o1.getNewPageRank() - o2.getNewPageRank());
            }
        });

        residualErr = 0;
        for (Node node: nodesMap.values()) {
            heap.add(node);

            valueOut = new Text(node.getId() + ";" + node.getDesNodeId() + ";" + node.getNewPageRank() + ";");
            context.write(keyOut, valueOut);

            long residual = (long) (Math.abs(node.getOldPageRank() - node.getNewPageRank()) * Conf.MULTIPLE / node.getNewPageRank());
            context.getCounter(Counter.RESIDUAL_COUNTER).increment(residual);
            log.info("valueOut: " + valueOut + ", residual = " + residual + ", prevPR = " + node.getOldPageRank() + ", newPR = " + node.getNewPageRank());
            log.info("[ Reducer ] key: " + keyOut + "value: " + valueOut);
            residualErr += residual;

        }

//        System.out.println("!! blockId:" + keyIn + " residual:" + residualErr);

        // TODO : it should be written into text
        // get two lowest pagerank nodesSystem.
//        System.out.println("Lowest 1 node:  blockid:" + blockId + "  nodeid:" + heap.poll().getId());
//        System.out.println("Lowest 2 node:  blockid:" + blockId + "  nodeid:" + heap.poll().getId());

    }

    /**
     * newPageRank = nextPageRank from inBlock nodes + nextPageRank from outBlock nodes
     * The nextPageRank from outBlock nodes is constant, so reset the newPageRank = nextPageRank from outBlock
     * then add the nextPageRank from inBlock
     * */
    protected float iterateBlockOnce(Map<Integer, Node> nodesMap, Map<Integer, List<Node>> srcNodesInBlockMap) {

        Map<Integer, Float> startPageRankMap = new HashMap<>();
        float residuals = 0;

        for (Node node : nodesMap.values()) {
            startPageRankMap.put(node.getId(), node.getNewPageRank());
        }

        //store the startPageRank and set newPageRank
        for (Node node : nodesMap.values()) {
            node.setNewPageRank(node.getPageRankFromOutBlock());
            if (srcNodesInBlockMap.containsKey(node.getId())) {
                List<Node> srcNodeInBlock = srcNodesInBlockMap.get(node.getId());

                for (Node srcNode : srcNodeInBlock) {
                    float nextPageRank = srcNode.getNewPageRank() / srcNode.getDegree();
                    node.addNewPageRank(nextPageRank);
                }
            }

            float updatedPageRank = node.getNewPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR;
            node.setNewPageRank(updatedPageRank);

            //calculate the residual
            float startPageRank = startPageRankMap.get(node.getId());
            float endPageRank = node.getNewPageRank();
            residuals += Math.abs(startPageRank - endPageRank) / endPageRank;
//
//            if (node.getId() > 10350 && node.getId() < 10360) {
//                System.out.println("!!nodeId:" + node.getId() + " before:" + startPageRank + " after:" + endPageRank + " residual:" + (Math.abs(startPageRank - endPageRank) / endPageRank));
//            }

        }

        // return the avg of residuals
        return residuals / nodesMap.size();
    }
}
