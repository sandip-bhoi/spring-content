package org.springframework.content.examples;


import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan
@EnableMongoRepositories
@EnableMongoContentStores
public class ClaimTestConfig extends AbstractSpringContentMongoConfiguration {

}
