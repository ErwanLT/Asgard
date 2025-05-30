package fr.eletutour.asgard.baldr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "fr.eletutour.asgard.*",
})
public class BaldrApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaldrApplication.class, args);
    }
}
