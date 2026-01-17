package net.lavacro.serverless.kafka;

import net.lavacro.serverless.engine.AppLauncher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MyConsumer {
	private final AppLauncher appLauncher;

	@KafkaListener(topics = "${kafka.topic}")
	public void listen(ConsumerRecord<String, Map<String, Object>> message, Acknowledgment ack) {
		log.info("Received message: {}; partition: {}, offset: {}", message, message.partition(), message.offset());
		ack.acknowledge();
		appLauncher.process(message.value(), message.headers());
	}

	public MyConsumer(AppLauncher appLauncher) {
		this.appLauncher = appLauncher;
	}
}
