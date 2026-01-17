package net.lavacro.serverless.service;

import java.util.Base64;
import java.util.List;

import feign.FeignException;

import net.lavacro.serverless.configuration.GithubConfig;
import net.lavacro.serverless.model.AppData;
import net.lavacro.serverless.model.GitBlob;
import net.lavacro.serverless.model.GitTreeItem;
import net.lavacro.serverless.model.GitTreeResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GithubService {
	private final GithubIntf githubIntf;
	private final GithubConfig githubConfig;

	GithubService(GithubIntf githubIntf, GithubConfig githubConfig) {
		this.githubIntf = githubIntf;
		this.githubConfig = githubConfig;
	}

	@Retryable(
			retryFor = {FeignException.class},
			noRetryFor = {FeignException.Unauthorized.class},
			backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public AppData stageApp(final String path) {
		log.info("[{}] Get from github ...", path);
		GitTreeResponse resp = githubIntf.tree(
				githubConfig.getToken(),
				null,
				githubConfig.getOwner(),
				githubConfig.getRepo(),
				githubConfig.getBranch()
		);

		log.info("Found {} entries", resp.getTree().size());

		List<GitTreeItem> dirList = resp.getTree().stream().filter(it -> it.getPath().startsWith(path + "/")).toList();

		log.info("Filtered down to {}", dirList.size());

		AppData appData = new AppData();

		for(GitTreeItem item: dirList) {
			if(path.equals(item.getPath())) {
				continue;
			}

			String filename = item.getPath().substring(path.length() + 1);
			log.info("loading {} ...", filename);
			if("app.groovy".equals(filename)) {
				appData.setSource(getBlob(item.getSha()));
				break;
			}
			log.info("{}", getBlob(item.getSha()));
		}
		return appData;
	}

	@Recover
	public AppData stageApp(FeignException e, String path) {
		log.info("Recovering ... {}", e.getMessage());
		return new AppData();
	}

	@Retryable(
			retryFor = {FeignException.class},
			backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	private String getBlob(final String sha) {
		try {
			GitBlob blob = githubIntf.getBlob(
					githubConfig.getToken(),
					null,
					githubConfig.getOwner(),
					githubConfig.getRepo(),
					sha
			);
			return new String(Base64.getMimeDecoder().decode(blob.getContent()));
		} catch(FeignException e) {
			log.error("Could not load blob contents: {}", e.getMessage());
			return null;
		}
	}

	@Recover
	public String getBlob(FeignException e, String sha) {
		log.info("Recovering in blob ... {}", e.getMessage());
		return null;
	}

}

@FeignClient(value = "github-api", url = "${github.url}")
interface GithubIntf {
	String API_VERSION = "2022-11-28";

	@GetMapping(value = "/repos/{owner}/{repo}/git/trees/{branch}?recursive=1")
	GitTreeResponse tree(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = API_VERSION, required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "branch") final String branch
	);

	@GetMapping(value = "/repos/{owner}/{repo}/git/blobs/{sha}")
	GitBlob getBlob(
			@RequestHeader("Authorization") final String auth,
			@RequestHeader(value = "X-GitHub-Api-Version", defaultValue = API_VERSION, required = false) final String apiVersion,
			@PathVariable(value = "owner") final String owner,
			@PathVariable(value = "repo") final String repo,
			@PathVariable(value = "sha") final String sha
	);
}
