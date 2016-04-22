package BlockPageRank;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
    /**
     * keyIn:
     * valueIn: srcNodeId;desNodeId1,desNodeId2...;srcNodePageRank;
     *
     * keyOut: blockId
     * valueOut: NEXTPAGERANK_FROM_INBLOCK;desNodeId;nextPageRank;
     * Or
     * keyOut: blockId
     * valueOut: NEXTPAGERANK_FROM_OUTBLOCK;desNodeId;nextPageRank;
     * Or
     * keyOut: blockId
     * valueOut: NODEINFO;srcNodeId;desNodeId1,desNodeId2...;srcOldNodePageRank;
     * Or
     * keyOut:blockId
     * valueOut: EDGE_INCBLOCK;srcNodeId;desNodeIdInBlock1,desNodeIdInBlock2...;
     */
    public void map(LongWritable keyIn, Text valueIn, Mapper.Context context)
            throws IOException, InterruptedException {
        System.out.println("!!valueIn: " + valueIn.toString());
        String[] temp = valueIn.toString().trim().split(";");
        String srcNodeId = temp[0].trim();
        String[] desNodeIds = temp[1].trim().split(",");
        float srcNodePageRank = Float.parseFloat(temp[2].trim());
        int srcNodeDegree = desNodeIds.length;
        float nextPageRank = srcNodePageRank / srcNodeDegree;
        String srcBlockId = Conf.Conf.getBlockId(srcNodeId);
        String edgeInBlock = "";

        //emit the node info
        Text keyOut = new Text(srcBlockId);
        Text valueOut = new Text(Conf.Conf.NODEINFO + ";" + temp[0] + ";" + temp[1] + ";" + temp[2] + ";");
        context.write(keyOut, valueOut);
        System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);

        //check if it has desNode
        //if not, emit srcNode and its pageRank
        if (desNodeIds.length == 0 || desNodeIds[0].isEmpty()) {
            keyOut = new Text(srcBlockId);
            valueOut = new Text(Conf.Conf.NEXTPAGERANK_FROM_INBLOCK + ";" + srcNodeId + ";" + nextPageRank + ";");
            context.write(keyOut, valueOut);
            System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
        } else {
            for (String desNodeId : desNodeIds) {
                String desBlockId = Conf.Conf.getBlockId(desNodeId);
                keyOut = new Text(desBlockId);
                if (srcBlockId.equals(desBlockId)) {
                    edgeInBlock += desNodeId + ",";
                    valueOut = new Text(Conf.Conf.NEXTPAGERANK_FROM_INBLOCK +";" + desNodeId + ";" + nextPageRank + ";");
                } else {
                    valueOut = new Text(Conf.Conf.NEXTPAGERANK_FROM_OUTBLOCK +";" + desNodeId + ";" + nextPageRank + ";");
                }
                context.write(keyOut, valueOut);
                System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);

            }
        }

        //emit the edge in block
        keyOut = new Text(srcBlockId);
        valueOut = new Text(Conf.Conf.EDGE_INCBLOCK + ";" + srcNodeId + ";" + edgeInBlock + ";");
        context.write(keyOut, valueOut);
        System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
    }
}
