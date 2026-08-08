package net.lavacro.serverless.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitTreeItem {
	private String name;
	private String path;
	private String sha;
	private Integer size;
	private String url;
	@JsonProperty("html_url")
	private String htmlUrl;
	@JsonProperty("git_url")
	private String gitUrl;
	@JsonProperty("download_url")
	private String downloadUrl;
	private String type;

	@JsonProperty("_links")
	private Links links;
	@Data
	static class Links {
		private String self;
		private String git;
		private String html;
	}
}
