package net.lavacro.serverless;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients
@EnableRetry
@Slf4j
public class MainApp {
	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}
}
