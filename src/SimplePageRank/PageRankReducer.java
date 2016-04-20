package SimplePageRank;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by Christina on 4/18/16.
 */
public class PageRankReducer extends Reducer<Text, Text, Text, Text> {
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
        float oldPageRank = 0;
        float newPageRank = 0;

        while (valuesIn.iterator().hasNext()) {
            Text valueIn = valuesIn.iterator().next();
            String[] temp = valueIn.toString().split(";");
            switch (temp[0]) {
                case Conf.NODEINFO:
                    desNodeIds = temp[1];
                    oldPageRank = Float.parseFloat(temp[2].trim());
                    break;
                case Conf.NEXTPAGERANK:
                    newPageRank += Float.parseFloat(temp[1].trim());
                    break;
            }
        }
        Text keyOut = new Text("");
        Text valueOut = new Text(srcNodeId + ";" + desNodeIds + ";" +
                newPageRank + ";");
        context.write(keyOut, valueOut);
        System.out.println("[ Reducer ] key: " + keyOut + "value: " + valueOut);
    }
}
