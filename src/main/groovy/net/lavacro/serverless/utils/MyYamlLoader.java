package net.lavacro.serverless.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.InvocationTargetException;
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

	public static<T> T yamlToObject(String str, Class<T> clazz) {
		if(str == null || str.isBlank()) {
			log.warn("yamlToObject: Empty YAML string");
			return newObject(clazz);
		}

		Constructor constructor = getConstructor(clazz);

		Yaml yaml = new Yaml(constructor);
		try {
			T obj = yaml.load(str);
			log.info("YAML loaded");
			return obj;
		} catch(Exception e) {
			log.error("Unable to load YAML:", e);
			return newObject(clazz);
		}
	}

	private static<T> @NotNull Constructor getConstructor(Class<T> clazz) {
		LoaderOptions options = new LoaderOptions();
		Constructor constructor = new Constructor(clazz, options);
		PropertyUtils propertyUtils = new PropertyUtils() {
			@Override
			public boolean isSkipMissingProperties() { return true; } // ignore missing properties

			@Override
			public boolean isAllowReadOnlyProperties() { return true; }
		};

		propertyUtils.setSkipMissingProperties(true);
		constructor.setPropertyUtils(propertyUtils);
		return constructor;
	}

	private static <T> @NotNull T newObject(Class<T> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			log.error("Unable to create instance of class {}", clazz.getName());
			return null;
		}
	}
}
