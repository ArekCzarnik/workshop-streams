package com.obi.next.workshop.kafka.streams.examples;

import com.google.gson.Gson;
import com.obi.next.workshop.kafka.streams.KafkaConfiguration;
import com.obi.next.workshop.kafka.streams.domain.Weather;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class FilterCountStreamService {

    private KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
    private KafkaStreams kafkaStreams = buildStream();
    private Gson gson = new Gson();

    private KafkaStreams buildStream() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream("weather");
        stream
                .filter((key, value) -> Double.parseDouble(stringToObject(value).celsius) > 3)
                .groupBy((key, value) -> stringToObject(value).plz) // KStream -> KGroupedStream
                .count() // KGroupedStream -> KTable
                .toStream() // KTable -> KStream
                .to("count-plz-output", Produced.with(Serdes.String(), Serdes.Long()));
        return new KafkaStreams(builder.build(), kafkaConfiguration.configuration());
    }

    @PostConstruct
    private void init() {
       // kafkaStreams.start();
    }

    @PreDestroy
    private void destroy() {
        kafkaStreams.close();
    }

    private Weather stringToObject(String value) {
        return gson.fromJson(value, Weather.class);
    }

}
