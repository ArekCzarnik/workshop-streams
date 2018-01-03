package com.obi.next.workshop.kafka.streams.examples;

import com.obi.next.workshop.kafka.streams.KafkaConfiguration;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class FilterStreamService {

    private KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
    private KafkaStreams kafkaStreams = buildStream();

    private KafkaStreams buildStream() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream("weather");
        stream.filter((key, value) -> value.length() > 1)
                .to("length_out");
        return new KafkaStreams(builder.build(), kafkaConfiguration.configuration());
    }

    @PostConstruct
    private void init() {
     //   kafkaStreams.start();
    }

    @PreDestroy
    private void destroy() {
        kafkaStreams.close();
    }


}