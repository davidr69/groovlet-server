package net.lavacro.serverless.engine.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.*;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.annotations.GroovyService;
import net.lavacro.serverless.configuration.GoogleConfig;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@GroovyService(value = "gcl")
public class GoogleLogger {
	private Logging logging;

	public GoogleLogger(GoogleConfig googleConfig) {
		LoggingOptions options;
		try {
			byte[] decoded = Base64.getDecoder().decode(googleConfig.getLogging());
			GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(decoded));
			options = LoggingOptions.newBuilder().setCredentials(credentials).build();
			logging = options.getService();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void info(final String bucket, final String message) {
			textMessage(Severity.INFO, message, bucket);
		}

	public void error(final String bucket, final String message) {
			textMessage(Severity.ERROR, message, bucket);
		}

	public void info(final String bucket, final Map<String, String> data) {
		jsonMessage(Severity.INFO, data, bucket);
	}

	public void error(final String bucket, final Map<String, String> data) {
		jsonMessage(Severity.ERROR, data, bucket);
	}

	private void textMessage(final Severity severity, final String message, final String bucket) {
		LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(message))
				.setSeverity(severity)
				.setLogName(bucket)
				.setResource(MonitoredResource.newBuilder("global").build())
				.build();
		logging.write(Collections.singleton(entry));
	}

	private void jsonMessage(final Severity severity, final Map<String, String> data, final String bucket) {
		LogEntry entry = LogEntry.newBuilder(Payload.JsonPayload.of(data))
				.setSeverity(severity)
				.setLogName(bucket)
				.setResource(MonitoredResource.newBuilder("global").build())
				.build();
		logging.write(Collections.singleton(entry));
	}
}
