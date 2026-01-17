package net.lavacro.serverless.engine;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.annotations.GroovyService;
import net.lavacro.serverless.model.AppData;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
public class Dynamic {
	private final ScheduledExecutorService executor;
	private final ConfigurableApplicationContext applicationContext;

	Dynamic(ConfigurableApplicationContext applicationContext) {
		executor = Executors.newScheduledThreadPool(64, new NamedThreadFactory("scriptlet"));
		this.applicationContext = applicationContext;
	}

	public void loadClass(AppData appData) {
		CompilerConfiguration config = new CompilerConfiguration();
		SecureASTCustomizer secure = new SecureASTCustomizer();
		secure.setDisallowedImports(List.of("java.util.List"));
		config.addCompilationCustomizers(secure);

		Binding binding = new Binding();

		GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());

		GroovyShell shell = new GroovyShell(groovyClassLoader, binding, config);
		appData.setShell(shell);

		try {
			Class<?> groovyClass = shell.getClassLoader().parseClass(appData.getSource());
			BaseGroovy instance = (BaseGroovy) groovyClass.getDeclaredConstructor().newInstance();
			Map<String, Object> beans = applicationContext.getBeansWithAnnotation(GroovyService.class);

			beans.forEach((k,v) -> {
				log.info("{} = {}", k, v.getClass().getName());
				GroovyService annotation = v.getClass().getAnnotation(GroovyService.class);
				instance.services.put(annotation.value(), v);
			});
			appData.setInstance(instance);
		} catch(Exception e) {
			log.error("[{}] Error creating script", appData.getApp(), e);
		}
	}

	@SuppressWarnings("deprecation")
	public Object process(AppData appData) {
		log.info("[{}] Going to run process ...", appData.getApp());

		Callable<Object> thread = () -> {
			// This throws a deprecation warning, but .threadId() doesn't report the OS thread
			long tid = Thread.currentThread().getId();
			Capacity.threadMap.put(tid, appData);
			log.info("Launching thread {}", tid);

			try {
				Method method = appData.getInstance().getClass().getMethod("exec", Map.class, Map.class);
				Object res = method.invoke(appData.getInstance(), appData.getMessage(), appData.getParams());
				appData.getInstance().returnMessage = res.toString();
				log.debug("Object: {}", res);
			} catch (Exception e) {
				log.error("Error during process", e);
			}
			Capacity.threadMap.remove(tid);
			log.info("Terminating thread {}", tid);
			return null;
		};

		Future<Object> future = executor.submit(thread);

		try {
			PipedOutputStream pipedOut = new PipedOutputStream();
			PipedOutputStream pipedErr = new PipedOutputStream();

			PipedInputStream pipedInOut = new PipedInputStream(pipedOut);
			PipedInputStream pipedInErr = new PipedInputStream(pipedErr);

			BufferedReader outReader = new BufferedReader(new InputStreamReader(pipedInOut));
			BufferedReader errReader = new BufferedReader(new InputStreamReader(pipedInErr));

			PrintStream psOut = new PrintStream(pipedOut);
			PrintStream psErr = new PrintStream(pipedErr);

			appData.getInstance().out = psOut;
			appData.getInstance().err = psErr;

			future.get(30, TimeUnit.SECONDS);

			psOut.flush();
			psErr.flush();

			appData.setStdout(readFromStream(outReader));
			appData.setStderr(readFromStream(errReader));

			outReader.close();
			errReader.close();

			psOut.close();
			psErr.close();
		} catch(TimeoutException te) {
			log.error("[{}] Timeout for {}", appData.getApp(), appData.getMessage());
			future.cancel(true);
		} catch(Exception e) {
			log.error("[{}] Execution error for {}", appData.getApp(), appData.getMessage(), e);
		}

		log.info("[{}] Thread completed", appData.getApp());

		return null;
	}

	private String readFromStream(BufferedReader reader) {
		StringBuilder sb = new StringBuilder();
		String line;

		try {
			while(reader.ready() && (line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch(IOException e) {
			//
		}
		return sb.toString();
	}
}
