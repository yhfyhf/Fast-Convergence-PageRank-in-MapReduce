package BlockPageRank;

import Conf.Conf;
import Conf.LoggerConf;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Christina on 4/19/16.
 */
public class PageRankRunner {

    public static void main (String[] args) throws Exception {
        Logger log = LoggerConf.getInfoLogger();

        String inputPath = args[0];
        String outputPath = args[1];

        Map<Integer, Long> blockIterNum = new HashMap<>();

        int mpNum = 0;
        for (; mpNum < Conf.MAPREDUCE_ITERATION; mpNum++) {
            Job job = new Job();
            job.setJobName(Conf.FILE_NAME + (mpNum + 1));
            job.setJarByClass(PageRankRunner.class);
            job.setMapperClass(PageRankMapper.class);
            job.setReducerClass(PageRankReducer.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            FileInputFormat.addInputPath(job, new Path(inputPath + Conf.FILE_NAME + mpNum));
            FileOutputFormat.setOutputPath(job, new Path(outputPath + Conf.FILE_NAME + (mpNum + 1)));

            job.waitForCompletion(true);
            float residual = ((float) job.getCounters().findCounter(Counter.RESIDUAL_COUNTER).getValue()) / Conf.MULTIPLE;
            float avgError = residual / Conf.NODES_NUM;

            float threshold = Conf.EPSILON;
            System.out.println("Iteration " + mpNum + " avg error " + String.format("%.6e", avgError));
            System.out.println("Iteration " + mpNum + " inblock iter " +
                    Math.round(job.getCounters().findCounter(Counter.INBLOCK_INTER_COUNTER).getValue() * 1.0 / Conf.BLOCKS_NUM));

            for (int j = 0; j < Conf.BLOCKS_NUM; j++) {
                if (!blockIterNum.containsKey(j)) {
                    blockIterNum.put(j, 0l);
                }
                blockIterNum.put(j, blockIterNum.get(j) + job.getCounters().findCounter(Counter.values()[j]).getValue());
            }


            if (avgError < threshold) {
                break;
            }
//            break;
        }

        for(int i = 0; i < Conf.BLOCKS_NUM; i++) {
            System.out.println("block" + i + " iter num: " + blockIterNum.get(i) / mpNum);
        }

        log.severe("Map reduce done!");
    }

}
