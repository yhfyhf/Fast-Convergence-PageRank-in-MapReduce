package BlockPageRank;

import Conf.Conf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    Map<String, Node> nodesMap = new HashMap<>();
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
        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            String[] temp = valueIn.toString().split(";");
            String nodeId = temp[1];
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
                    node.setDesNodeInBlock(temp[2]);
                    break;
            }
        }

        Text keyOut = new Text("");
        Text valueOut;
        for (String nodeId : nodesMap.keySet()) {
            Node node = nodesMap.get(nodeId);
            valueOut = new Text(nodeId + ";" + node.getDesNodeId() + ";" + node.getNewPageRank() + ";");
            context.write(keyOut, valueOut);
            System.out.println("[ PRReducer ] key: " + keyOut + "value: " + valueOut);
        }
    }

    /**
     * newPageRank = nextPageRank from inBlock node + nextPageRank from out Block node
     * The nextPageRank from inBlock node is constant, so reset the newPageRank = nextPageRank from outBlock
     * then add the nextPageRank from inBlock
     * */
    private void iterateBlockOnce() {
        //set nextPageRank and newPageRank for each node
        for (Node node : nodesMap.values()) {
            node.setNextPageRank(node.getNewPageRank() / node.getDegree());
            node.setNewPageRank(node.getPageRankFromOutBlock());
        }

        //get the updated newPageRank
        for (Node srcNode : nodesMap.values()) {
            String[] desNodeIds = srcNode.getDesNodeInBlock().split(",");
            float nextPageRank = srcNode.getNextPageRank();
            for (String desNodeId : desNodeIds) {
                Node desNode = nodesMap.get(desNodeId);
                desNode.addNewPageRank(nextPageRank);
            }
        }

        //damping factor
        for (Node node : nodesMap.values()) {
            float updatedPageRank = node.getNextPageRank() * Conf.DAMPING_FACTOR + Conf.RANDOM_JUMP_FACTOR;
            node.setNewPageRank(updatedPageRank);
        }
    }
}
