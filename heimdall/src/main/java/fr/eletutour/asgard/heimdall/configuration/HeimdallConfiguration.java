package fr.eletutour.asgard.heimdall.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class HeimdallConfiguration {
    // This class is used to enable AspectJ auto-proxying in the Heimdall module.
    // It allows for the use of AOP (Aspect-Oriented Programming) features such as logging, security, etc.
    // No additional beans or configurations are needed at this time.
}
