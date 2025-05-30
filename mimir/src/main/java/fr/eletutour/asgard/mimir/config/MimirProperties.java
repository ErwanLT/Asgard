package fr.eletutour.asgard.mimir.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "mimir")
public class MimirProperties {
    private Documentation documentation = new Documentation();

    @Value("${mimir.output.path:./output}")
    private Path outputPath;

    @Data
    public static class Documentation {
        private String outputDir = "docs/";
        private String format = "markdown";
        private List<String> languages = List.of("java", "kotlin");
    }
} 