package net.lavacro.serverless.stubs

abstract class BaseGroovy {
	Integer exitCode
	String returnMessage
	String message

	PrintStream out
	PrintStream err

	Map<String, Object> services = new HashMap<>()

	protected abstract Object exec(final String messageBody, final Map<String,String> params)
}
