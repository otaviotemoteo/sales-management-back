package com.sales.management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupBanner {

    private static final String CYAN = "[36m";
    private static final String BOLD = "[1m";
    private static final String RESET = "[0m";

    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        String port = env.getProperty("server.port", "8080");
        String ctx = env.getProperty("server.servlet.context-path", "");
        String[] profiles = env.getActiveProfiles();
        String profile = profiles.length == 0 ? "default" : String.join(",", profiles);
        String dbPort = env.getProperty("DB_HOST_PORT", "5432");
        String redisPort = env.getProperty("REDIS_HOST_PORT", "6379");

        String base = "http://localhost:" + port + ctx;
        String bar = "=".repeat(70);

        System.out.println();
        System.out.println(CYAN + bar + RESET);
        System.out.println(CYAN + BOLD + "  Sales Management API — all services running!" + RESET);
        System.out.println(CYAN + bar + RESET);
        System.out.println();
        System.out.printf("  Profile    %s%n", profile);
        System.out.printf("  DB         localhost:%s%n", dbPort);
        System.out.printf("  Redis      localhost:%s%n", redisPort);
        System.out.printf("  API        %s%n", base);
        System.out.printf("  Swagger    %s/swagger-ui.html%n", base);

        if (env.acceptsProfiles(Profiles.of("dev"))) {
            System.out.println();
            System.out.println(CYAN + "  Demo accounts (dev only)" + RESET);
            System.out.printf("  Admin      %s / %s%n", DevDataSeeder.ADMIN_EMAIL, DevDataSeeder.ADMIN_PASSWORD);
            System.out.printf("  Vendedor   %s / %s%n", DevDataSeeder.SELLER_EMAIL, DevDataSeeder.SELLER_PASSWORD);
        }

        System.out.println();
        System.out.println(CYAN + bar + RESET);
        System.out.println();
    }
}
