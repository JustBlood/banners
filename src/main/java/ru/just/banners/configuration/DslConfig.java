package ru.just.banners.configuration;

import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DslConfig {
    @Bean
    public DefaultConfiguration configuration() {
        return new DefaultConfiguration();
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }
}
