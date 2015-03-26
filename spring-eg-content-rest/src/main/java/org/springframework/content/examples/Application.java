package org.springframework.content.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@SpringBootApplication
@ComponentScan(basePackages={"org.springframework.content.examples", "internal.org.springframework.content"})
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@Configuration
	@EnableMongoRepositories()
	@EnableMongoContentStores()
	public static class MongoRepositoriesConfiguration extends AbstractMongoConfiguration
	{
		// for mongo content
		@Bean
		public GridFsTemplate gridFsTemplate() throws Exception {
			return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
		}
		
		// for mongo data
	    @Override
	    public Mongo mongo() throws Exception {
	        return new MongoClient();
	    }
	    
		@Override
		public MongoTemplate mongoTemplate() throws Exception {
	        return new MongoTemplate(new SimpleMongoDbFactory(mongo(), getDatabaseName()));
		}

		@Override
		protected String getDatabaseName() {
			return "spring-eg-content-rest";
		}
	}
}
