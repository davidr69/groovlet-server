package net.lavacro.serverless.utils;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class JsonUtilsTest {
	private static final String MY_SCHEMA = """
{
	"$schema":"https://json-schema.org/draft/2020-12/schema",
	"type":"object",
	"properties":{
		"hostname":{"type":"string"},
		"date":{"type":"string"},
		"address":{"type":"string"},
		"protocol":{"type":"string"},
		"port":{"type":"integer"}
	},
	"required":["hostname","date","address","protocol","port"]
}
	""";

	@Test
	public void testSchemaValidatorSuccess() {
		String payload = """
{
	"hostname":"server",
	"date":"2025-03-28T22:26:15.638929-04:00",
	"address":"148.113.206.49",
	"protocol":"TCP",
	"port":36984
}
""";

		String result = JsonUtils.schemaValidator(MY_SCHEMA, payload);
		Assertions.assertNull(result);
	}

	@Test
	public void testSchemaValidatorFail() {
		String payload = """
{
	"hostname":"server",
	"date":"2025-03-28T22:26:15.638929-04:00",
	"address":"148.113.206.49",
	"port":"36984"
}
		""";

		String result = JsonUtils.schemaValidator(MY_SCHEMA, payload);
		Assertions.assertNotNull(result);
		System.out.println(result);
	}
}
