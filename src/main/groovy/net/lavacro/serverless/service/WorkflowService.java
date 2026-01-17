package net.lavacro.serverless.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.engine.Dynamic;
import net.lavacro.serverless.model.AppData;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WorkflowService {
	private final GithubService githubService;
	@Getter
	private static Map<String,AppData> appMap;
	private final Dynamic dynamic;

	WorkflowService(GithubService githubService, Dynamic dynamic) {
		this.githubService = githubService;
		this.dynamic = dynamic;
		appMap = new ConcurrentHashMap<>();
	}

	public void loadFromGithub(final String app) {
		log.info("[{}] Getting from github", app);
		AppData appData = githubService.stageApp(app);
		appData.setApp(app);
		appMap.put(app, appData);
		dynamic.loadClass(appData);
		log.info("[{}] Loaded", app);
	}

	public Callable<Object> doit(final String app, final Map<String, Object> message, Map<String,String> params) {
		return () -> {
			AppData appData = appMap.get(app);
			appData.setMessage(message);
			appData.setParams(params);

			log.info("[{}] In processing thread", app);
			try {
				dynamic.process(appData);
				log.info("[{}] stdout? {}", app, appData.getStdout());
			} catch(Exception e) {
				log.error("[{}] Process caused exception", app, e);
			}
			log.info("[{}] Ended processing", app);

			return 0;
		};
	}

	public void unloadApp(final String app) {
		AppData appData = appMap.get(app);
		if(appData == null) {
			log.error("{} does not exist or is already unloaded", app);
			return;
		}
		appData.getShell().getClassLoader().clearCache();
		appMap.remove(app);
		log.info("Unloaded {}", app);
	}
}
