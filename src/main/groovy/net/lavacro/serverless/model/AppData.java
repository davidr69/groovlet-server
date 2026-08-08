package net.lavacro.serverless.model;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;
import net.lavacro.serverless.engine.BaseGroovy;

import java.util.Map;

@Getter
@Setter
public class AppData {
	private String source;
	private Map<String,String> config;
	private Map<String,String> params;
	private Map<String, Object> message; // Kafka payload
	private String app;
	private String jsonValidator;

	// execution params
	private GroovyShell shell;
	private Script script;
	private BaseGroovy instance;

	private String stdout;
	private String stderr;

	// runtime
	private Long maxMemory;
}
