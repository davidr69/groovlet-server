package net.lavacro.serverless.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GitRequestInterceptor implements RequestInterceptor {
	private final GithubConfig githubConfig;

	@Override
	public void apply(RequestTemplate template) {
		// These headers cannot be overridden by method parameters
		template.header("Authorization", githubConfig.getToken());
		template.header("X-GitHub-Api-Version", githubConfig.getApiVersion());
	}
}