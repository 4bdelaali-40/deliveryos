package com.deliveryos.config;

import feign.Logger;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration Feign pour le client AI Engine.
 */
@Configuration
public class AiEngineConfig {

    @Value("${ai-engine.connect-timeout}")
    private int connectTimeout;

    @Value("${ai-engine.read-timeout}")
    private int readTimeout;

    @Bean
    public Request.Options aiEngineRequestOptions() {
        return new Request.Options(
                connectTimeout, TimeUnit.MILLISECONDS,
                readTimeout, TimeUnit.MILLISECONDS,
                true
        );
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}