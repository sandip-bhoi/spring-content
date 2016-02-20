package org.springframework.content.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages={/*"org.springframework.content.examples", */"internal.org.springframework.content"})
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@Configuration
	@EnableMongoRepositories()
	@EnableMongoContentStores()
	public static class MongoRepositoriesConfiguration extends AbstractSpringContentMongoConfiguration
	{
	}
}
