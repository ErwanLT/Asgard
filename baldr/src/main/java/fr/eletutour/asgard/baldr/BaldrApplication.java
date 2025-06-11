package fr.eletutour.asgard.baldr;

import fr.eletutour.asgard.hel.config.HelAutoConfiguration;
import fr.eletutour.asgard.mimir.config.MimirAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = {
        "fr.eletutour.asgard.baldr",
        "fr.eletutour.asgard.heimdall"
})
@Import({HelAutoConfiguration.class, MimirAutoConfiguration.class})
public class BaldrApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaldrApplication.class, args);
    }
}
