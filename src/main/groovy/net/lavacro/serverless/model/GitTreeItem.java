package net.lavacro.serverless.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitTreeItem {
	private String path;
	private String mode;
	private String type;
	private String sha;
	private Integer size;
	private String url;
}
