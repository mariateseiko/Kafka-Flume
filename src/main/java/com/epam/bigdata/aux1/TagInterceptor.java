package com.epam.bigdata.aux1;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagInterceptor implements Interceptor {
    private String tagsPath;
    private Map<String, String> tagsMap = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(TagInterceptor.class.getSimpleName());

    public TagInterceptor(String tagsPath) {
        this.tagsPath = tagsPath;
    }

    public void initialize()  {
        extractTags();
    }

    private void extractTags() {
        try {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(conf);
            Path path = new Path(tagsPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\t");
                if (fields.length >= 2 && fields[0] != null && !fields[0].isEmpty()) {
                    tagsMap.put(fields[0], fields[1]);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to read tags data", e);
            throw new RuntimeException();
        }
    }

    public Event intercept(Event event) {
        String body = new String(event.getBody());
        Map<String, String> headers = event.getHeaders();
        String tokens [] = body.split("\\t");
        if(tokens.length > 21 && StringUtils.isNotEmpty(tokens[20]) && StringUtils.isNotEmpty(tagsMap.get(tokens[20]))){
            headers.put("tags", "true");
            String streamId = "0";
            if (StringUtils.isNotEmpty(tokens[21])) {
                streamId = tokens[21];
            }
            headers.put("stream_id", streamId);
            String result = body.substring(0, body.lastIndexOf("\\t")+1) + tagsMap.get(tokens[20]);
            event.setBody(result.getBytes());
        } else {
            headers.put("tags", "false");
        }
        return event;
    }

    public List<Event> intercept(List<Event> list) {
        List<Event> interceptedEvents = new ArrayList<Event>(list.size());
        for (Event event : list) {
            Event interceptedEvent = intercept(event);
            interceptedEvents.add(interceptedEvent);
        }
        return interceptedEvents;
    }

    public void close() {
    }

    public static class Builder
            implements Interceptor.Builder {
        private String tags;
        @Override
        public void configure(Context context) {
            tags = context.getString("tagsPath");
        }

        @Override
        public Interceptor build() {
            return new TagInterceptor(tags);
        }
    }
}
