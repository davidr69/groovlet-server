package net.lavacro.serverless.configuration;

import net.lavacro.serverless.model.AppData;
import net.lavacro.serverless.service.WorkflowService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "runtimeStats")
public class RuntimeStats {

	@ReadOperation
	public Map<String,Object> getRuntimeStats() {
		Map<String, AppData> appMap = WorkflowService.getAppMap();

		Map<String,Object> map = new HashMap<>();
		map.put("loadedClasses", appMap.size());
		map.put("classNames", appMap.keySet());
		return map;
	}
}
