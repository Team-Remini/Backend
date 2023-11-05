package com.likelion.remini.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

//RestTemplate 을 사용하기 위해 Spring Bean 컴포넌트로 등록하는 설정을 추가
@Configuration
public class ClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
