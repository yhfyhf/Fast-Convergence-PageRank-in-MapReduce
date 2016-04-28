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

    private Logger log = LoggerConf.getInfoLogger();

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
        double prevPageRank = 0.0d;
        double newPageRank = 0.0d;

        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            String[] tokens = valueIn.toString().split(";");
            switch (Integer.parseInt(tokens[0])) {
                case Conf.NODEINFO:
                    desNodeIds = tokens[1];
                    prevPageRank = Double.parseDouble(tokens[2].trim());
                    break;
                case Conf.NEXTPAGERANK:
                    newPageRank += Double.parseDouble(tokens[1].trim());
                    break;
            }
        }

        newPageRank = Conf.DAMPING_FACTOR * newPageRank + Conf.RANDOM_JUMP_FACTOR;
        Text keyOut = new Text("");
        Text valueOut = new Text(srcNodeId + ";" + desNodeIds + ";" + newPageRank + ";");
        context.write(keyOut, valueOut);

        long residual = (long) (Math.abs(prevPageRank - newPageRank) * Conf.MULTIPLE / newPageRank);

        context.getCounter(Counter.RESIDUAL_COUNTER).increment(residual);

        log.info("[ Reducer ] srcNodeId:" + srcNodeId + " residual:" + residual + " pagerank:" + newPageRank);
    }
}
