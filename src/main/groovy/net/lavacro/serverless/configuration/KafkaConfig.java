package net.lavacro.serverless.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaConfig {
	private String topic;
	private String appHeader;
	private String paramsHeader;
}
