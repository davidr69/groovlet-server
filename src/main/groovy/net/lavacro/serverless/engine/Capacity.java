package net.lavacro.serverless.engine;

import jdk.jfr.FlightRecorder;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;
import jdk.jfr.consumer.RecordingStream;
import lombok.extern.slf4j.Slf4j;
import net.lavacro.serverless.model.AppData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class Capacity {
	public final static Map<Long, AppData> threadMap = new ConcurrentHashMap<>();

	public Callable<Object> startFlightRecorder() {
		return () -> {
			log.info("((( inside Callable )))");
			log.info("Is flight recorder available? {}", FlightRecorder.isAvailable());
			try (
					RecordingStream rs = new RecordingStream()
			) {

				long maxMem = 1024 * 1024 * 64;
				AtomicLong allocatedMem = new AtomicLong(0);

				rs.enable("jdk.ObjectAllocationInTLAB");
				rs.enable("jdk.ObjectAllocationOutsideTLAB");

				rs.onEvent("jdk.ObjectAllocationInTLAB", this::checkEvent);
				rs.onEvent("jdk.ObjectAllocationOutsideTLAB", this::checkEvent);

				rs.start();
				log.info("Starting JFR monitor thread");
			}
			return null;
		};
	}

	private void checkEvent(RecordedEvent event) {
		RecordedThread thread = event.getThread();
		long tid = thread.getJavaThreadId();

		log.info("Received memory event for thread {}: {}", tid, event.getEventType().getName());

		threadMap.forEach((k,v) -> log.info("Keymap thread id: {}", k) );
		if(threadMap.containsKey(tid)) {
			long size = event.getLong("allocationSize");
//			long total = allocatedMem.addAndGet(size);
			log.info("Thread {} allocated {} bytes", tid, size);
		}
/*		if(tid == workerThreadId.get()) {
			long size = event.getLong("allocationSize");
			long total = allocatedMem.addAndGet(size);
			if(total > maxMem) {
				log.error("Memory limit exceeded: {} butes allocated, limit is {} bytes", total, maxMem);
				future.cancel(true);
			} else {
				log.info("Thread {} allocated {} bytes, total allocated: {} bytes", tid, size, total);
			}
		}*/
	}

}
