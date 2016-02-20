package org.springframework.content.examples;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
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

	private static final Log log = LogFactory.getLog(ClaimTestConfig.class);
	
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
		if (System.getenv("spring_eg_content_mongo_host") != null) {
	    	String host = System.getenv("spring_eg_content_mongo_host");
	    	String port = System.getenv("spring_eg_content_mongo_port");

	    	log.info(String.format("Connecting to %s:%s", host, port));
	        int nPort = Integer.parseInt(port);
	        Mongo mongo = new MongoClient(host, nPort);
	        return mongo;
	    } else {
	    	log.info("Connecting to localhost");
	        return new MongoClient();
	    }	
	}
	
    @Override
	protected UserCredentials getUserCredentials() {

    	if (System.getenv("spring_eg_content_mongo_username") != null) {
	        String username = System.getenv("spring_eg_content_mongo_username");
	        String password = System.getenv("spring_eg_content_mongo_password");
	    	log.info(String.format("Connecting as %s:%s", username, password));
		    return new UserCredentials(username, password);
	    }
    	return null;
	}

}
