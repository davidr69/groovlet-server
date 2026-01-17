package net.lavacro.serverless.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google.cloud")
@Getter
@Setter
public class GoogleConfig {
	private String logging;
}
