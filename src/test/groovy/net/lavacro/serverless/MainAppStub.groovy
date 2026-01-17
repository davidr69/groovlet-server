package net.lavacro.serverless;

import lombok.extern.slf4j.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
@Slf4j
class MainAppStub {
	static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(MainAppStub.class, args)

	}
}
