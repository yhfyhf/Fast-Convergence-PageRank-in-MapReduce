package SimplePageRank;

import Conf.Conf;
import Conf.LoggerConf;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Christina on 4/18/16.
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {

    private Logger log = LoggerConf.getWarningLogger();

    /**
     * keyIn: srcNodeId
     * valueIn: NEXTPAGERANK;nextPageRank;
     * Or
     * valueIn: NODEINFO;desNodeId1,desNodeId2...;srcNodePageRank;
     *
     * keyOut:
     * valueIn: srcNodeId;desNodeId1,desNodeId2...;srcNodeDegree;srcNodePageRank;
     * */
    protected void reduce(Text keyIn, Iterable<Text> valuesIn, Context context)
            throws IOException, InterruptedException {

        String srcNodeId = keyIn.toString().trim();
        String desNodeIds = "";
        float prevPageRank = 0;
        float newPageRank = 0;

        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            String[] tokens = valueIn.toString().split(";");
            switch (Integer.parseInt(tokens[0])) {
                case Conf.NODEINFO:
                    desNodeIds = tokens[1];
                    prevPageRank = Float.parseFloat(tokens[2].trim());
                    break;
                case Conf.NEXTPAGERANK:
                    newPageRank += Float.parseFloat(tokens[1].trim());
                    break;
            }
        }
        Text keyOut = new Text("");
        Text valueOut = new Text(srcNodeId + ";" + desNodeIds + ";" + newPageRank + ";");
        context.write(keyOut, valueOut);

        context.getCounter(Counter.RESIDUAL_COUNTER).increment((long) (Math.abs(newPageRank - prevPageRank) * 1000000));

        log.info("[ Reducer ] key: " + keyOut + "value: " + valueOut);
    }
}
