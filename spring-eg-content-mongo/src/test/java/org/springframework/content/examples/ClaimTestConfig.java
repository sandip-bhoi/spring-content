package org.springframework.content.examples;


import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@ComponentScan
@EnableMongoRepositories
@EnableMongoContentStores
public class ClaimTestConfig extends AbstractMongoConfiguration {

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
	
	@Override
	protected String getDatabaseName() {
		return "spring-eg-content-mongo";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient();
	}
}
