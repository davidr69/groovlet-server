package net.lavacro.serverless.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
@Getter
@Setter
public class GithubConfig {
	private String apiVersion;
	private String url;
	private String token;
	private String branch;
	private String owner;
	private String repo;
}
