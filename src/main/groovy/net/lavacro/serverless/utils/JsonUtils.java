package net.lavacro.serverless.utils;

import com.networknt.schema.*;
import com.networknt.schema.Error;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.filters.StringInputStream;

import java.util.List;

@Slf4j
public class JsonUtils {
	private JsonUtils() { }

	public static String schemaValidator(String jsonSchema, String jsonData) {
		log.info("Validating data schema ...");

		SchemaRegistryConfig config = SchemaRegistryConfig.builder().build();
		SchemaRegistry registry = SchemaRegistry.withDefaultDialect(
				SpecificationVersion.DRAFT_2020_12,
				builder -> builder.schemaRegistryConfig(config)
		);
		Schema schema = registry.getSchema(new StringInputStream(jsonSchema));

		List<Error> messageList = schema.validate(jsonData, InputFormat.JSON, executionContext ->
			executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true))
		);

		List<String> errorMessages = messageList.stream().map(Error::getMessage).toList();
		if(errorMessages.isEmpty()) {
			return null;
		}

		String rv = String.join(", ", errorMessages);
		log.error("Error validating schema: {}", rv);
		return rv;
	}
}
