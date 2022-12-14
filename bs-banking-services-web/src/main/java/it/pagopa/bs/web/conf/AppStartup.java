package it.pagopa.bs.web.conf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(scanBasePackages = {"it.pagopa.bs"})
public class AppStartup {

    public static void main(String[] args) {
        SpringApplication.run(AppStartup.class, args);
    }
}
