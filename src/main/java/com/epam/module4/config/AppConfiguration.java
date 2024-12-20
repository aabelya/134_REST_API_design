package com.epam.module4.config;

import com.epam.module4.domain.Authority;
import com.epam.module4.domain.Role;
import com.epam.module4.domain.User;
import com.epam.module4.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class AppConfiguration {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createAdminUser(ApplicationReadyEvent ev) {
        UserService userService = ev.getApplicationContext().getBean(UserService.class);
        User user = userService.createUser("admin", "pa$$w0rD", Role.ADMIN, Authority.values());
        log.info("admin user created {}", user);
    }

}
