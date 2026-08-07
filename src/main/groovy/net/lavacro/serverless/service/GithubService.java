package net.lavacro.serverless.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import net.lavacro.serverless.configuration.GithubConfig;
import net.lavacro.serverless.model.*;

import net.lavacro.serverless.utils.MyYamlLoader;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {
	private final GithubIntf githubIntf;
	private final GithubConfig githubConfig;

	@Retryable(
			retryFor = {FeignException.class},
			noRetryFor = {FeignException.Unauthorized.class},
			backoff = @Backoff(delay = 1000, multiplier = 2)
	)
	public AppData stageApp(final String appPath) {
		log.info("[{}] Get from github ...", appPath);

		List<GitTreeItem> items = githubIntf.listDir(
				githubConfig.getToken(),
				null,
				githubConfig.getOwner(),
				githubConfig.getRepo(),
				"syslog-watcher",
				githubConfig.getBranch()
		);

		Optional<GitTreeItem> manifest = items.stream().filter(it -> it.getPath().equals(appPath + "/manifest.yml")).findFirst();
		if(manifest.isEmpty()) {
			log.error("manifest not found");
			return null;
		}

		String manifestContent = getBlob(manifest.get().getSha());
		ManifestModel manifestModel = MyYamlLoader.yamlToObject(manifestContent, ManifestModel.class);

		log.info("Loaded manifest: {}", manifestModel);

		Optional<GitTreeItem> app = items.stream().filter(it -> it.getPath().equals(appPath + "/" + manifestModel.getAppFile())).findFirst();
		if(app.isEmpty()) {
			log.error("app not found");
			return null;
		}

		String appContent = getBlob(app.get().getSha());

		AppData appData = new AppData();
		appData.setSource(appContent);
		appData.setParams(new HashMap<>());
		appData.setConfig(new HashMap<>());
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

