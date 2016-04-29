bucket_name=cs5300-project2-hy456
cluster_id=j-1ARBGJ15MPL9G

if [ "$1" != "SimplePageRank" ] && [ "$1" != "BlockPageRank" ] && [ "$1" != "GaussPageRank" ]; then
	echo "Incorrect argument."
	exit 1
fi

mkdir -p "out/$1Out"
cd src

javac -classpath /usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/common/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/yarn/lib/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/mapreduce/lib/*:/usr/local/Cellar/hadoop/2.7.2/libexec/share/hadoop/mapreduce/*:./  -d "../out/$1Out/" $1/*.java

cd ../out/$1Out
jar -cvf $1.jar -C $1 Conf/ .
aws s3 cp $1.jar s3://$bucket_name/
aws emr add-steps --cluster-id $cluster_id --steps Type=CUSTOM_JAR,Name=$1,ActionOnFailure=CONTINUE,Jar=s3://$bucket_name/$1.jar,MainClass=$1.PageRankRunner,Args=s3n://$bucket_name/data/,s3n://$bucket_name/data/
