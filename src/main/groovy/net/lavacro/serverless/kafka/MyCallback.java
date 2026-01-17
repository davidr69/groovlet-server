package net.lavacro.serverless.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class MyCallback implements Callback {
	@Override
	public void onCompletion(RecordMetadata recordMetadata, Exception e) {
		if(e == null) {
			log.info("Offset: {}, topic: {}, partition: {}", recordMetadata.offset(), recordMetadata.topic(), recordMetadata.partition());
		} else {
			log.error("Callback found exception: {}", e.getMessage());
		}
	}
}
