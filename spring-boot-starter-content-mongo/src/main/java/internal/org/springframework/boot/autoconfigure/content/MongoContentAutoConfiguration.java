package internal.org.springframework.boot.autoconfigure.content;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.Configuration;

import com.mongodb.Mongo;

@Configuration
@ConditionalOnClass(Mongo.class)
public class MongoContentAutoConfiguration {

	@EnableMongoContentStores
	private static class AutoConfigure {
	}
}
