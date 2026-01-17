package net.lavacro.serverless.stubs

import com.google.common.net.InternetDomainName

class NoopStub extends BaseGroovy {
	@Override
	Object exec(String messageBody, Map<String, String> params) {
		/*
			"message" will look like:

			{
				"hostname":"server",
				"date":"2025-03-28T22:26:15.638929-04:00",
				"address":"148.113.206.49",
				"protocol":"TCP",
				"port":36984
			}
		 */
//23-239-4-211.ip.linodeusercontent.com
		out.println("Message: ${message}")

		def gcl = services['gcl']

		def fqdn = InetAddress.getByName(message['address'] as String).canonicalHostName

		message['fqdn'] = fqdn

		out.println("Added fqdn: ${message}")

		if(fqdn != message['address']) {
			try {
				def idn = InternetDomainName.from(fqdn)
				def registrable = idn.topPrivateDomain().toString()   // e.g. clarin.com.ar
				message['domain'] = registrable
				out.println("Added domain: ${registrable}")
			} catch(Exception e) {
				err.println("Couldn't resolve ${fqdn}: ${e.message}")
			}
		}

		gcl.info('syslog-watcher', message)

		return message
	}
}
