package com.artbridge.artwork.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberProducerImpl implements MemberProducer {

    // 토픽명
    private static final String TOPIC_MEMBERNAME = "member-name-request";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MemberProducerImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void requestMemberName(Long id) {
        MemberNameDTO memberNameDTO = new MemberNameDTO(id);
        String message = null;
        try {
            message = objectMapper.writeValueAsString(memberNameDTO);
            log.info("Request MemberName to Kafka Producer: {}", message);
            kafkaTemplate.send(new ProducerRecord<>(TOPIC_MEMBERNAME, message)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Request MemberName to Kafka Producer Success: {}", message);
    }
}
