package internal.org.springframework.boot.autoconfigure.content;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.mongodb.Mongo;

@Configuration
@ConditionalOnClass(Mongo.class)
@Import(MongoContentAutoConfigureRegistrar.class)
public class MongoContentAutoConfiguration {

}
