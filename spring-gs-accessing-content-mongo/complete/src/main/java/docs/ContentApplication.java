package docs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ContentApplication {

	private static final Logger log = LoggerFactory.getLogger(ContentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ContentApplication.class);
	}

//	@Bean
//	public CommandLineRunner demo(ContentMetadataContentStore store) {
//		return (args) -> {
//            log.info("");
//            log.info(store.toString());
//		};
//	}
}
