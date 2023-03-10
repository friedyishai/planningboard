package com.whiteboard.config;

import com.whiteboard.dto.UserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserSession userSession() {
        return new UserSession();
    }
}
