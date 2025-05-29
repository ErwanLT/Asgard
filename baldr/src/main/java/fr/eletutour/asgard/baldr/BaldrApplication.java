package fr.eletutour.asgard.baldr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "fr.eletutour.asgard.baldr",
        "fr.asgard.heimdall" // module contenant les aspects
})
public class BaldrApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaldrApplication.class, args);
    }
}
