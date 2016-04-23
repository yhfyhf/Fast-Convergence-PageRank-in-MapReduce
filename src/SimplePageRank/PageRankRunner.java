package SimplePageRank;

import Conf.Conf;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by Christina on 4/23/16.
 */
public class PageRankRunner {
    public static void main(String[] args)
            throws IOException, ClassNotFoundException, InterruptedException {
        String path = args[0] + "data/" + Conf.FILE_NAME;

        for (int i = 0; i < Conf.MAPREDUCE_ITERATION; i++) {
            System.out.println("Iteration num: " + i);

            Job job = new Job();
            job.setJobName(Conf.FILE_NAME + (i + 1));
            job.setJarByClass(PageRankRunner.class);
            job.setMapperClass(PageRankMapper.class);
            job.setReducerClass(PageRankReducer.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);

            String inputPath = path + i;
            String outputPath = path + (i + 1);
            FileInputFormat.addInputPath(job, new Path(inputPath));
            FileOutputFormat.setOutputPath(job, new Path(outputPath));

            job.waitForCompletion(true);

        }
        System.out.println("Map reduce done!");
    }
}
