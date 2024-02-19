package com.example.Order_management.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.IOException;

@Configuration
@Profile("test")
public class EmbeddedPostgresConfig {

    @Value("${embedded.postgresql.port}")
    private int postgresPort;

    @Value("${embedded.postgresql.database}")
    private String postgresDatabase;

    @Value("${embedded.postgresql.username}")
    private String postgresUsername;

    @Value("${embedded.postgresql.password}")
    private String postgresPassword;

    @Bean(destroyMethod = "stop")
    public EmbeddedPostgres embeddedPostgres() throws IOException {
        EmbeddedPostgres embeddedPostgres = new EmbeddedPostgres();
        embeddedPostgres.start("localhost", postgresPort, postgresDatabase, postgresUsername, postgresPassword);
        return embeddedPostgres;
    }

}

