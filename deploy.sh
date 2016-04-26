mkdir -p out/SimplePageRankOut
cd src

javac -classpath /usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/common/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/yarn/lib/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/mapreduce/lib/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/mapreduce/*:./  -d ../out/SimplePageRankOut/ SimplePageRank/*.java

cd ../out/SimplePageRankOut
jar -cvf SimplePageRank.jar -C SimplePageRank/ Conf/ .
aws s3 cp SimplePageRank.jar s3://cs5300-project2-hy456/
aws emr add-steps --cluster-id j-3A6FQY94CDG0L --steps Type=CUSTOM_JAR,Name=SimplePageRank,ActionOnFailure=CONTINUE,Jar=s3://cs5300-project2-hy456/SimplePageRank.jar,MainClass=SimplePageRank.PageRankRunner,Args=s3n://cs5300-project2-hy456/data/,s3n://cs5300-project2-hy456/data/
