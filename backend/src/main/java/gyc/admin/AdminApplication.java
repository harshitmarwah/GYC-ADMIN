package gyc.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AdminApplication {

    public static void main(String[] args) throws IOException {
        // The sqlite-jdbc driver does not create missing parent directories.
        Files.createDirectories(Path.of("./data"));
        SpringApplication.run(AdminApplication.class, args);
    }
}
