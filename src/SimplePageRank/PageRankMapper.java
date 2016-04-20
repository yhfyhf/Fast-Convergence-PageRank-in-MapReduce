package SimplePageRank;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by Christina on 4/18/16.
 */
public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {
    /**
     * keyIn:
     * valueIn: srcNodeId;desNodeId1,desNodeId2...;srcNodePageRank;
     *
     * keyOut: desNodeId
     * valueOut: NEXTPAGERANK;nextPageRank;
     * Or
     * keyOut: srcNodeId
     * valueOut: NODEINFO;desNodeId1,desNodeId2...;srcOldNodePageRank;
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


        //emit the srcNodeInfo
        Text keyOut = new Text(srcNodeId);
        Text valueOut = new Text(Conf.NODEINFO + ";" + temp[1] + ";" + temp[2] + ";");
        context.write(keyOut, valueOut);
        System.out.println("[ Mapper ] key: " + keyOut + ", value: " + valueOut);

        //emit the nextPageRank
        //check if it has desNode
        //if not, emit srcNode and its pageRank
        if (desNodeIds.length == 0 || desNodeIds[0].isEmpty()) {
            valueOut = new Text(Conf.NEXTPAGERANK + ";" + nextPageRank);
            context.write(keyOut, valueOut);
            System.out.println("[ Mapper ] key: " + keyOut + ", value: " + valueOut);
        } else {
            for (String desNodeId : desNodeIds) {
                keyOut = new Text(desNodeId);
                valueOut = new Text(Conf.NEXTPAGERANK +";" + nextPageRank);
                context.write(keyOut, valueOut);
                System.out.println("[ Mapper ] key: " + keyOut + ", value: " + valueOut);
            }
        }
    }
}
