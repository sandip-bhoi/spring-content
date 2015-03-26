package org.springframework.content.gs.quickclaim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"org.springframework.content.gs.quickclaim", "internal.org.springframework.content"})
public class Application {
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
