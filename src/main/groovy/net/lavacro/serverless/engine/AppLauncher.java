package net.lavacro.serverless.engine;

import groovy.json.JsonOutput;
import net.lavacro.serverless.service.WorkflowService;
import net.lavacro.serverless.utils.JsonUtils;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.kafka.common.header.Headers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AppLauncher {
	private final Set<String> appList = new HashSet<>();
	private final WorkflowService workflowService;

	private final BlockingQueue<Runnable> threadQueue = new LinkedBlockingQueue<>();
	private final ThreadPoolExecutor threads = new ThreadPoolExecutor(
			64, 64, 0L, TimeUnit.SECONDS, threadQueue
	);

	@Value("${app.required-header}")
	private String requiredHeader;

	public AppLauncher(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void process(Map<String, Object> message, Headers headers) {
		Header appHeader = headers.lastHeader(requiredHeader);
		if(appHeader != null) {
			log.info("Message is for this platform");
			String appName = new String(appHeader.value());

			Header actionHeader = headers.lastHeader("action");
			if(actionHeader != null) {
				String action = new String(actionHeader.value());
				if(action.equals("unload")) {
					log.info("Going to unload {}", appName);
					workflowService.unloadApp(appName);
					appList.remove(appName);
				} else {
					log.error("Action not supported");
				}
				return;
			}

			int activeThreads = threads.getActiveCount();
			log.info("Active threads: {}", activeThreads);
			if(activeThreads == 64) {
				log.info("******** Thread will be queued ********");
			}

			if(!appList.contains(appName)) {
				log.info("*** Loading app: {}", appName);
				workflowService.loadFromGithub(appName);
				appList.add(appName);
			} else {
				log.info("*** App {} already loaded", appName);
			}

			String payload = JsonOutput.toJson(message);
			String schema = WorkflowService.getAppMap().get(appName).getJsonValidator();
			String validationProblems = null;

			if(schema == null) {
				validationProblems = JsonUtils.schemaValidator(schema, payload);
			}
			if(validationProblems == null) {
				threads.submit(workflowService.doit(appName, message, new HashMap<>()));
			} else {
				log.error("Validation problems: {}", validationProblems);
			}
		} else {
			log.info("Message is not for me");
		}
	}
}
