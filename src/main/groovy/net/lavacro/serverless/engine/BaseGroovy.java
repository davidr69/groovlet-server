package net.lavacro.serverless.engine;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseGroovy {
	protected Integer exitCode;
	protected String returnMessage;
	protected String message;

	protected PrintStream out;
	protected PrintStream err;

	protected Map<String, Object> services = new HashMap<>();

	protected abstract Object exec(final Map<String, Object> messageBody, final Map<String,String> params);
}
