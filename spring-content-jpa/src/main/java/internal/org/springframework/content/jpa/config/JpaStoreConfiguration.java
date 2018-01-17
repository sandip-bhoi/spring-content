package internal.org.springframework.content.jpa.config;

import internal.org.springframework.content.jpa.store.JpaStoreSchemaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class JpaStoreConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public JpaStoreSchemaManager jpaStoreSchemaManager() {
        return new JpaStoreSchemaManager(dataSource);
    }

    @PostConstruct
    public void schemaSetup() {
        jpaStoreSchemaManager().create();
    }
}
