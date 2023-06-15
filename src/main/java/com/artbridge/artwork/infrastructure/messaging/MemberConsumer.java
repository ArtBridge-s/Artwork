package com.artbridge.artwork.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;


@Service
@Slf4j
public class MemberConsumer {

    private final MemberInPort memberInPort;
    private static final String TOPIC = "member-name";

    public MemberConsumer(MemberInPort memberInPort) {
        this.memberInPort = memberInPort;
    }


    @KafkaListener(topics = TOPIC)
    public void processMessage(String message) {
        log.info("MemberConsumer: {}", message);


        Map<Object, Object> map;
        ObjectMapper mapper = new ObjectMapper();


        try {
            map = mapper.readValue(message, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        memberInPort.updateMemberName(Long.parseLong(map.get("id").toString()), map.get("name").toString());
        log.info("MemberConsumer: {}", map.get("id"), map.get("name").toString());
    }
}
