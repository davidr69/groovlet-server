package net.lavacro.serverless.utils;


import net.lavacro.serverless.runtime.MemInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemoryUtils {
	public static final Set<String> pools = ManagementFactory.getMemoryPoolMXBeans()
			.stream()
			.map(MemoryPoolMXBean::getName)
			.collect(Collectors.toSet());

	public static MemInfo getMemoryUsage(String poolName) {
		List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
		for(MemoryPoolMXBean pool: memoryPools) {
			if(pool.getName().equals(poolName)) {
				MemoryUsage usage = pool.getUsage();
				return new MemInfo(usage.getUsed(), usage.getCommitted(), usage.getMax());
			}
		}
		return new MemInfo(0L, 0L, 0L);
	}


}
