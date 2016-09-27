package com.epam.bigdata.aux1;

import com.google.common.io.Resources;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class FileProducer {
    private Producer<String, String> producer;
    private static final String TOPIC = "auxhw";

    private static final Logger LOG = Logger.getLogger(FileProducer.class.getSimpleName());

    FileProducer() throws IOException{
        try (InputStream propStream = Resources.getResource("kafka.properties").openStream()) {
            Properties properties = new Properties();
            properties.load(propStream);
            producer = new KafkaProducer<>(properties);
        }
    }
    public static void main(String[] args) throws Exception{
        FileProducer fileProducer = new FileProducer();
        fileProducer.processFiles(args[0]);
    }

    private void processFiles(String path) throws IOException {
        try(Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                try(Stream<String> lines = Files.lines(filePath, Charset.forName("ISO-8859-1"))) {
                    lines.forEach(line -> producer.send(new ProducerRecord<>(TOPIC, null, line)));
                } catch (IOException e) {
                    LOG.error("Failed to read line");
                }
            });
        } finally {
            producer.close();
        }
    }
}
