package BlockPageRank;

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
     * valueIn: NEXTPAGERANK;desNodeId;nextPageRank;
     * Or
     * keyIn: blockId
     * valueIn: NODEINFO;srcNodeId;desNodeId1,desNodeId2...;srcOldNodePageRank;
     * Or
     * keyIn:blockId
     * valueIn: EDGE_INCBLOCK;srcNodeId;desNodeIdInBlock1,desNodeIdInBlock2...;
     * Or
     * keyIn:blockId
     * valueIn: EDGE_OUTCBLOCK;srcNodeId;desNodeIdOutBlock1,desNodeIdOutBlock2...;
     *
     * keyOut:
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
            switch (temp[0]) {
                case Conf.Conf.NODEINFO:
                    node.setDesNodeIds(temp[2].trim());
                    node.setOldPageRank(Float.parseFloat(temp[3].trim()));
                    break;
                case Conf.Conf.NEXTPAGERANK:
                    node.addNewPageRank(Float.parseFloat(temp[2].trim()));
                    break;
                case Conf.Conf.EDGE_INCBLOCK:
                    node.setDesNodeInBlock(temp[2]);
                    break;
                case Conf.Conf.EDGE_OUTCBLOCK:
                    node.setDesNodeOutBlock(temp[2]);
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
}
