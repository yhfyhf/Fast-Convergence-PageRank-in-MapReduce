package SimplePageRank;

import Conf.Conf;
import Conf.LoggerConf;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Christina on 4/18/16.
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Logger log = LoggerConf.getWarningLogger();

    /**
     * keyIn:
     * valueIn: srcNodeId;desNodeId1,desNodeId2...;srcNodePageRank;
     *
     * keyOut: srcNodeId
     * valueOut: NODEINFO;desNodeId1,desNodeId2...;srcOldNodePageRank;
     *
     * foreach desNode:
     *     keyOut: desNodeId
     *     valueOut: NEXTPAGERANK;nextPageRank;
     */
    public void map(LongWritable keyIn, Text valueIn, Mapper.Context context)
            throws IOException, InterruptedException {
        String[] tokens = valueIn.toString().trim().split(";");

        if (tokens.length < 2) {
            return;
        }

        String srcNodeId = tokens[0].trim();
        String desNodeIdsStr = tokens[1].trim();
        String[] desNodeIds = desNodeIdsStr.split(",");
        int srcNodeDegree = desNodeIds[0].trim().equals("") ? 0 : desNodeIds.length;
        Double srcNodePageRank = Double.parseDouble(tokens[2].trim());
        Double nextPageRank = srcNodeDegree == 0 ? 0.0 : srcNodePageRank / srcNodeDegree;

        // Emit the srcNodeInfo.
        Text keyOut = new Text(srcNodeId);
        Text valueOut = new Text(Conf.NODEINFO + ";" + desNodeIdsStr + ";" + srcNodePageRank + ";");
        context.write(keyOut, valueOut);

        // Emit the nextPageRank.
        for (String desNodeId : desNodeIds) {
            if (!desNodeId.isEmpty()) {
                keyOut = new Text(desNodeId);
            }
            valueOut = new Text(Conf.NEXTPAGERANK +";" + nextPageRank);
            context.write(keyOut, valueOut);
            log.info("[ Mapper ] Emitted NEXTPAGERANK key: " + keyOut + ", value: " + valueOut);
        }
    }
}
