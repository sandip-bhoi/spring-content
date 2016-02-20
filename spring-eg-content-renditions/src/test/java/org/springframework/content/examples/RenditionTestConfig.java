package org.springframework.content.examples;


import org.springframework.content.config.EnableMongoContentStores;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = {"internal.org.springframework.content.docx4j"})
@EnableMongoRepositories
@EnableMongoContentStores
public class RenditionTestConfig extends AbstractSpringContentMongoConfiguration  {

}
