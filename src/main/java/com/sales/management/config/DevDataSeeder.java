package com.sales.management.config;

import com.sales.management.model.entity.User;
import com.sales.management.model.enums.UserRole;
import com.sales.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    public static final String ADMIN_EMAIL = "admin@sales.local";
    public static final String ADMIN_PASSWORD = "admin123";
    public static final String SELLER_EMAIL = "vendedor@sales.local";
    public static final String SELLER_PASSWORD = "vendedor123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seed("Admin Demo", ADMIN_EMAIL, ADMIN_PASSWORD, UserRole.ADMIN);
        seed("Vendedor Demo", SELLER_EMAIL, SELLER_PASSWORD, UserRole.SELLER);
    }

    private void seed(String name, String email, String rawPassword, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .build();
        userRepository.save(user);
        log.info("Seeded {} user: {}", role, email);
    }
}
