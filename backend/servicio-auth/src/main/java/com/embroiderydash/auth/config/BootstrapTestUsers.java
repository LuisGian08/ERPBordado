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
import java.util.List;

@Configuration
public class BootstrapTestUsers {
    @Bean ApplicationRunner seedTestUsers(UserRepository users, PasswordEncoder encoder,
            @Value("${app.test-users.admin.username:}") String adminUsername,
            @Value("${app.test-users.admin.password:}") String adminPassword,
            @Value("${app.test-users.company-admin.username:}") String companyAdminUsername,
            @Value("${app.test-users.company-admin.password:}") String companyAdminPassword,
            @Value("${app.test-users.designer.username:}") String designerUsername,
            @Value("${app.test-users.designer.password:}") String designerPassword,
            @Value("${app.test-users.operator.username:}") String operatorUsername,
            @Value("${app.test-users.operator.password:}") String operatorPassword) {
        return args -> List.of(
                new Seed(adminUsername, "Administrador de prueba", adminPassword, Role.ADMIN),
                new Seed(companyAdminUsername, "Administrador de empresa", companyAdminPassword, Role.ADMIN_EMPRESA),
                new Seed(designerUsername, "Diseñador de prueba", designerPassword, Role.DISENADOR),
                new Seed(operatorUsername, "Operador de prueba", operatorPassword, Role.OPERADOR)
        ).forEach(seed -> {
            if (seed.username().isBlank() && seed.password().isBlank()) return;
            if (seed.username().isBlank() || seed.password().isBlank())
                throw new IllegalStateException("Cada cuenta de prueba requiere usuario y contraseña");
            int bytes = seed.password().getBytes(StandardCharsets.UTF_8).length;
            if (seed.password().length() < 12 || bytes > 72)
                throw new IllegalStateException("Las contraseñas de prueba deben tener entre 12 y 72 bytes");
            users.findByUsername(seed.username()).ifPresentOrElse(existing -> {
                if (existing.getRole() != seed.role())
                    throw new IllegalStateException("El usuario de prueba ya existe con un rol diferente: " + seed.username());
            }, () -> users.save(new UserAccount(seed.username(), seed.displayName(), encoder.encode(seed.password()), seed.role())));
        });
    }
    private record Seed(String username, String displayName, String password, Role role) {}
}
