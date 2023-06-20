package com.artbridge.artwork.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberProducerImpl implements MemberProducer {
    /*TODO: - need refactoring*/

    // 토픽명
    private static final String TOPIC_MEMBERNAME = "member-name-request";

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public MemberProducerImpl(ObjectMapper objectMapper, KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }



    @Override
    public void requestMemberName(Long id) {
        MemberNameDTO memberNameDTO = new MemberNameDTO(id);
        try {
            String message = objectMapper.writeValueAsString(memberNameDTO);
            log.info("Request MemberName to Kafka Producer: {}", message);
            Message<byte[]> kafkaMessage = MessageBuilder
                .withPayload(message.getBytes())
                .setHeader(KafkaHeaders.TOPIC, TOPIC_MEMBERNAME)
                .build();
            kafkaTemplate.send(kafkaMessage).get();
            log.info("Request MemberName to Kafka Producer Success: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
