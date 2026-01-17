package net.lavacro.serverless.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MyYamlLoader {
	private MyYamlLoader() { }

	public static Map<String,String> yamlToMap(final String str) {
		if(str == null || str.isEmpty()) {
			log.error("Empty YAML string");
			return new HashMap<>();
		}
		LoaderOptions options = new LoaderOptions();
		options.setTagInspector(tag -> tag.getClassName().equals(HashMap.class.getName()));
		Yaml yaml = new Yaml(options);
		try {
			Map<String,String> resp = yaml.load("!!java.util.HashMap\n" + str);
			log.info("Loaded YAML; {} items", resp.size());
			return resp;
		} catch(Exception e) {
			log.error("Unable to load YAML:", e);
			return new HashMap<>();
		}
	}
}
