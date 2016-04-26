HADOOP_PATH=/usr/local/Cellar/hadoop/2.7.2/libexec
CLASS_PATH=$(HADOOP_PATH)/share/hadoop/common/*:$(HADOOP_PATH)/share/hadoop/yarn/lib/*:$(HADOOP_PATH)/share/hadoop/mapreduce/lib/*:$(HADOOP_PATH)/share/hadoop/mapreduce/*:src/

SimplePageRank: src/SimplePageRank/*.java 
	mkdir -p out/SimplePageRankOut
	javac -classpath $(CLASS_PATH) -d out/SimplePageRankOut/ src/SimplePageRank/*.java
	jar -cvf out/jars/SimplePageRank.jar -C out/SimplePageRankOut/SimplePageRank/ .

clean: 
	rm -r out/SimplePageRankOut; rm out/jars/*.jar
