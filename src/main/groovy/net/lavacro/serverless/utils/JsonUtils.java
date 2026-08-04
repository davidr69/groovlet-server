package net.lavacro.serverless.utils;

import com.networknt.schema.InputFormat;
import lombok.extern.slf4j.Slf4j;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import java.util.List;

@Slf4j
public class JsonUtils {
	public String schemaValidator(String jsonSchema, String jsonData) {
		log.info("Validating data schema ...");

		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
				SpecVersion.VersionFlag.V202012,
				builder -> builder.jsonNodeReader(JsonNodeReader.builder().locationAware().build())
		);
		SchemaValidatorsConfig config = SchemaValidatorsConfig.builder().build();
		JsonSchema schema = factory.getSchema(jsonSchema, InputFormat.JSON, config);
		Set<ValidationMessage> messages = schema.validate(jsonData, InputFormat.JSON, executionContext ->
			executionContext.getExecutionConfig().setFormatAssertionsEnabled(true)
		);
		List<ValidationMessage> messageList = messages.stream().toList();

		List<String> errorMessages = messageList.stream().map(ValidationMessage::getMessage).toList();
		if(errorMessages.isEmpty()) {
			return null;
		}

		String rv = String.join(", ", errorMessages);
		log.error("Error validating schema: {}", rv);
		return rv;
	}
}
