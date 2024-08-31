package com.example.pr4;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;

@Configuration
class RequesterConfiguration {

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON).tcp("localhost", 7000);
    }
}
