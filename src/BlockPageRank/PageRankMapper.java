package BlockPageRank;

import Conf.LoggerConf;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Christina on 4/20/16.
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Logger log = LoggerConf.getWarningLogger();

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

        log.info("[ Mapper ] valueIn: " + valueIn.toString());
        String[] tokens = valueIn.toString().trim().split(";");
        if (tokens.length < 2) {
            return;
        }

        String srcNodeId = tokens[0].trim();
        String[] desNodeIds = tokens[1].trim().split(",");
        float srcNodePageRank = Float.parseFloat(tokens[2].trim());
        int srcNodeDegree = desNodeIds.length;
        float nextPageRank = srcNodePageRank / srcNodeDegree;
        String srcBlockId = getBlockId(srcNodeId);
        String edgeInBlock = "";

        // Emit the node info
        Text keyOut = new Text(srcBlockId);
        Text valueOut = new Text(Conf.Conf.NODEINFO + ";" + tokens[0] + ";" + tokens[1] + ";" + tokens[2] + ";");
        context.write(keyOut, valueOut);
        log.info("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);

        // Emit nextPageRank
        for (String desNodeId : desNodeIds) {
            if (desNodeId.isEmpty()) {
                break;
            }
            String desBlockId = getBlockId(desNodeId);
            keyOut = new Text(desBlockId);
            if (srcBlockId.equals(desBlockId)) {
                edgeInBlock += desNodeId + ",";
                valueOut = new Text(Conf.Conf.NEXTPAGERANK_FROM_INBLOCK +";" + desNodeId + ";" + nextPageRank + ";");
            } else {
                valueOut = new Text(Conf.Conf.NEXTPAGERANK_FROM_OUTBLOCK +";" + desNodeId + ";" + nextPageRank + ";");
            }
            context.write(keyOut, valueOut);
//            log.info("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
        }

        // Emit the edge in block
        keyOut = new Text(srcBlockId);
        valueOut = new Text(Conf.Conf.EDGE_INCBLOCK + ";" + srcNodeId + ";" + edgeInBlock + ";");
        context.write(keyOut, valueOut);
        log.info("[ PRMapper ] key: " + keyOut + ", value: " + valueOut);
    }

    protected String getBlockId(String nodeId) {
        return Conf.Conf.getBlockId(nodeId);
    }
}
