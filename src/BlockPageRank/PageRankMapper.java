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
     * valueOut: NEXTPAGERANK;desNodeId;nextPageRank;
     * Or
     * keyOut: blockId
     * valueOut: NODEINFO;srcNodeId;desNodeId1,desNodeId2...;srcOldNodePageRank;
     * Or
     * keyOut:blockId
     * valueOut: EDGE_INCBLOCK;srcNodeId;desNodeIdInBlock1,desNodeIdInBlock2...;
     * Or
     * keyOut:blockId
     * valueOut: EDGE_OUTCBLOCK;srcNodeId;desNodeIdOutBlock1,desNodeIdOutBlock2...;
     */
    public void map(LongWritable keyIn, Text valueIn, Mapper.Context context)
            throws IOException, InterruptedException {
        System.out.println("!!valueIn: " + valueIn.toString());
        String[] temp = valueIn.toString().split(";");
        String srcNodeId = temp[0].trim();
        String[] desNodeIds = temp[1].trim().split(",");
        float srcNodePageRank = Float.parseFloat(temp[2].trim());
        int srcNodeDegree = desNodeIds.length;
        float nextPageRank = srcNodePageRank / srcNodeDegree;
        String srcBlockId = Conf.Conf.getBlockId(srcNodeId);
        String edgeInBlock = "";
        String edgeOutBlock = "";

        //emit the node info
        Text keyOut = new Text(srcNodeId);
        Text valueOut = new Text(Conf.Conf.NODEINFO + ";" + temp[1] + ";" + temp[2] + ";");
        context.write(keyOut, valueOut);
        System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);

        //emit the next pageRank

        //check if it has desNode
        //if not, emit srcNode and its pageRank
        if (desNodeIds.length == 0 || desNodeIds[0].isEmpty()) {
            valueOut = new Text(Conf.Conf.NEXTPAGERANK + ";" + nextPageRank);
            context.write(keyOut, valueOut);
            System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
        } else {
            for (String desNodeId : desNodeIds) {
                String desBlockId = Conf.Conf.getBlockId(desNodeId);
                if (srcBlockId.equals(desBlockId)) {
                    edgeInBlock += desNodeId;
                } else {
                    edgeOutBlock += desNodeId;
                }
                valueOut = new Text(Conf.Conf.NEXTPAGERANK +";" + nextPageRank);
                context.write(keyOut, valueOut);
                System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
            }
        }

        //emit the edge in block
        valueOut = new Text(Conf.Conf.EDGE_INCBLOCK + ";" + srcNodeId + ":" + edgeInBlock);
        context.write(keyOut, valueOut);
        System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);

        //emit the edge out block
        valueOut = new Text(Conf.Conf.EDGE_OUTCBLOCK + ";" + srcNodeId + ":" + edgeOutBlock);
        context.write(keyOut, valueOut);
        System.out.println("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
    }
}
