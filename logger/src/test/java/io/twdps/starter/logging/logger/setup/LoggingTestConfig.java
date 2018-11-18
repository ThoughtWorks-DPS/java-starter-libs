package io.twdps.starter.logging.logger.setup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"io.twdps.starter.logging"})
public class LoggingTestConfig implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(LoggingTestConfig.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
