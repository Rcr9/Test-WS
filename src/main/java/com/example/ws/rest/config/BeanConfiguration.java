package com.example.ws.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Configuration
public class BeanConfiguration {
    static final String collectionWsSessionIdBean = "collectionWsSessionId";

    @Bean(value = collectionWsSessionIdBean)
    public Collection<String> collectionWsSessionId() {
        return Collections.synchronizedSet(new HashSet<>());
    }
}
