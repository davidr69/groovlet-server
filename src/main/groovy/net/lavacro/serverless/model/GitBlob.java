package net.lavacro.serverless.model;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class GitBlob {
	private String sha;
	@JsonProperty("node_id")
	private String nodeId;
	private Integer size;
	private String url;
	private String content;
	private String encoding;
}
