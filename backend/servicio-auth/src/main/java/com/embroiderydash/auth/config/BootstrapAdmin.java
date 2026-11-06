package com.embroiderydash.auth.config;

import com.embroiderydash.auth.domain.Role;
import com.embroiderydash.auth.domain.UserAccount;
import com.embroiderydash.auth.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class BootstrapAdmin {
    @Bean ApplicationRunner createInitialAdmin(UserRepository users, PasswordEncoder encoder,
            @Value("${app.bootstrap-admin.username}") String username,
            @Value("${app.bootstrap-admin.display-name}") String displayName,
            @Value("${app.bootstrap-admin.password}") String password) {
        return args -> {
            if (password == null || password.length() < 12 || password.getBytes(StandardCharsets.UTF_8).length > 72)
                throw new IllegalStateException("ADMIN_PASSWORD es obligatorio y debe tener entre 12 y 72 caracteres");
            if (!users.existsByUsername(username)) users.save(new UserAccount(username, displayName, encoder.encode(password), Role.ADMIN));
        };
    }
}
