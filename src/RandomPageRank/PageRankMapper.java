package RandomPageRank;

import Conf.Conf;
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
     * valueIn: uId;vId1, vId2, ...;pageRank;
     *
     * KeyOut: blockId
     * valueOut: BE;vId;uId;NextPageRank
     *
     * KeyOut: blockId
     * valueOut: BC;vId;NextPageRank
     *
     * KeyOut: blockId
     * valueOut:NODEINFO;uId;vId1, vId2, ...;pageRank;
     *
     */
    public void map(LongWritable keyIn, Text valueIn, Mapper.Context context)
            throws IOException, InterruptedException {

        log.info("[ Mapper ] valueIn: " + valueIn.toString());
        String[] tokens = valueIn.toString().trim().split(";");
        if (tokens.length < 2) {
            return;
        }

        int uId = Integer.valueOf(tokens[0].trim());
        String[] vIdsStr = tokens[1].trim().split(",");
        float pageRank = Float.parseFloat(tokens[2].trim());
        int degree = vIdsStr.length;
        float nextPageRank = pageRank / degree;
        int uBlockId = getBlockId(uId);

        Text keyOut = new Text(String.valueOf(uBlockId));
        Text valueOut;

        // Emit the node info
        valueOut = new Text(Conf.NODEINFO + ";" + valueIn.toString());
        context.write(keyOut,valueOut);

        // Emit BE and BC
        if (!vIdsStr[0].isEmpty()) {
            for (String vIdStr : vIdsStr) {
                int vId = Integer.parseInt(vIdStr);
                int vBlockId = getBlockId(vId);
                keyOut = new Text(String.valueOf(vBlockId));
                if (vBlockId == uBlockId) {
                    valueOut = new Text(Conf.BE + ";" + vId + ";" + uId + ";");
                } else {
                    valueOut = new Text(Conf.BC + ";" + vId + ";" + nextPageRank + ";");
                }
                context.write(keyOut, valueOut);
            }
        }

    }

    protected int getBlockId(int nodeId) {
        return Conf.getBlockIdRandom(nodeId);
    }
}
