package net.lavacro.serverless.service;

import net.lavacro.serverless.model.GitBlob;
import net.lavacro.serverless.model.GitTreeItem;
import net.lavacro.serverless.model.GitTreeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "github-api", url = "${github.url}")
interface GithubIntf {
	@GetMapping(value = "/repos/{owner}/{repo}/git/trees/{branch}")
	GitTreeResponse tree(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = "${github.api-version}", required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "branch") final String branch
	);

	@GetMapping(value = "/repos/{owner}/{repo}/git/blobs/{sha}")
	GitBlob getBlob(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = "${github.api-version}", required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "sha") final String sha
	);

	@GetMapping(value = "/repos/{owner}/{repo}/contents/{path}")
	List<GitTreeItem> listDir(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = "${github.api-version}", required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "path") final String path,
			@RequestParam(value = "ref", required = false) final String ref
	);
}
