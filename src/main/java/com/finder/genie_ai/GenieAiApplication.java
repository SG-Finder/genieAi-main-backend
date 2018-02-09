package com.finder.genie_ai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EntityScan(
		basePackages = { "com.finder.genie_ai.model" },
		basePackageClasses = {Jsr310JpaConverters.class})
public class GenieAiApplication implements ApplicationRunner {

	private static final Logger logger = LogManager.getLogger(GenieAiApplication.class);

	public static void main(String[] args) {
		System.getProperties().put("server.port", 8081);
		SpringApplication.run(GenieAiApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		logger.debug("Debugging log");
		logger.info("Info log");
		logger.warn("Hey, This is a warning!");
		logger.error("Oops! We have an Error. OK");
		logger.fatal("Damn! Fatal error. Please fix me.");
	}
}

