package net.lavacro.serverless.model;

import lombok.Data;

import java.util.List;

@Data
public class ManifestModel {
	private String name;
	private String version;
	private String description;

	private String appFile;
	private String configFile;
	private String jsonMessageValidator;

	private List<String> services;
}
