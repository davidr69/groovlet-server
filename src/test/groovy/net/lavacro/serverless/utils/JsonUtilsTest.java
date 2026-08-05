package net.lavacro.serverless.utils;

import org.junit.jupiter.api.Assertions;

public class JsonUtilsTest {
	private static final String MY_SCHEMA = """
{
}
	""";

	public void testSchemaValidatorSuccess() {
		String payload = """
{
}
		""";

		String result = JsonUtils.schemaValidator(MY_SCHEMA, payload);
		Assertions.assertNull(result);
	}

	public void testSchemaValidatorFail() {
		String payload = """
{
}
		""";

		String result = JsonUtils.schemaValidator(MY_SCHEMA, payload);
		Assertions.assertNotNull(result);
	}
}
