package com.viewton.config;

import com.viewton.concurrent.ViewtonExecutorService;
import com.viewton.concurrent.ViewtonExecutorServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration which extends component scan for spring context.
 */
@Configuration
@ComponentScan("com.viewton")
public class ViewtonConfiguration {

    @Bean
    @ConditionalOnMissingBean(ViewtonExecutorService.class)
    public ViewtonExecutorService viewtonExecutorService() {
        return new ViewtonExecutorServiceImpl();
    }
}

