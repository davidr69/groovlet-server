package net.lavacro.serverless.utils;

import net.lavacro.serverless.model.ManifestModel;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class MyYamlLoaderTest {
	@Test
	public void yamlToObjectSuccess() {
		String yaml = """
name: syslog-watcher
version: 1.0.0
description: Accepts ulogd2 messages
appFile: syslog-watcher.groovy
#configFile: none
services:
  - syslog
""";

		ManifestModel model = MyYamlLoader.yamlToObject(yaml, ManifestModel.class);
		Assertions.assertNotNull(model);
		Assertions.assertEquals("syslog-watcher", model.getName());
		Assertions.assertEquals("1.0.0", model.getVersion());
		Assertions.assertEquals("Accepts ulogd2 messages", model.getDescription());
		Assertions.assertEquals("syslog-watcher.groovy", model.getAppFile());
		Assertions.assertEquals(1, model.getServices().size());
		Assertions.assertEquals("syslog", model.getServices().getFirst());
	}

	@Test
	public void yamlToObjectFail() {
		String yaml = """
name: syslog-watcher
version: 1.0.0
description: Accepts ulogd2 messages
app_file: syslog-watcher.groovy
services:
  - syslog
""";

		ManifestModel model = MyYamlLoader.yamlToObject(yaml, ManifestModel.class);
		Assertions.assertNotNull(model);
		Assertions.assertEquals("syslog-watcher", model.getName());
		Assertions.assertEquals("1.0.0", model.getVersion());
		Assertions.assertEquals("Accepts ulogd2 messages", model.getDescription());
		Assertions.assertNull(model.getAppFile());
		Assertions.assertEquals(1, model.getServices().size());
		Assertions.assertEquals("syslog", model.getServices().getFirst());
	}
}
