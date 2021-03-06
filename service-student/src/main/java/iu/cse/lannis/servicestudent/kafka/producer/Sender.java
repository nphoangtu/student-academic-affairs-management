package iu.cse.lannis.servicestudent.kafka.producer;

import iu.cse.lannis.servicestudent.entity.Student;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Sender {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, Student> kafkaTemplate;

    public void send(String topic, Student payload) {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }

    public void sendWithId(String topic, String uuid, Student payload) {
        LOGGER.info("sending payload={} to topic={} with id={}", payload, topic, uuid);
        kafkaTemplate.send(new ProducerRecord<>(topic, uuid, payload));
    }
}
