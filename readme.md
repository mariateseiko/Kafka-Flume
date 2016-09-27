Create hive table - info in hive.sql

Start flume agent
/usr/hdp/current/flume-server/bin/flume-ng agent --conf conf --conf-file config.properties --name aux1
-Dflume.root.logger=INFO,console --classpath ./kafka-flume-1.0-SNAPSHOT-4.jar -Xms512m -Xmx1024m
-Dcom.sun.management.jmxremote

Start kafka producer
java -cp kafka-flume-1.0-SNAPSHOT-jar-with-dependencies-3.jar com.epam.bigdata.aux1.FileProducer
/media/sf_SharedVM_Folder/dataset/testdata.txt
