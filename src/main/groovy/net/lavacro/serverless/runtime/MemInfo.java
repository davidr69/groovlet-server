package net.lavacro.serverless.runtime;

public record MemInfo(
		Long used,
		Long committed,
		Long max
) { }
