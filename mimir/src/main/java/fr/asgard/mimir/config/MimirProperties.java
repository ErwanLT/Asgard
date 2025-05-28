package fr.asgard.mimir.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "mimir")
public class MimirProperties {
    private Documentation documentation = new Documentation();
    private KnowledgeBase knowledgeBase = new KnowledgeBase();
    private Analysis analysis = new Analysis();
    private Path outputPath;

    @Data
    public static class Documentation {
        private String outputDir = "docs/";
        private String format = "markdown";
        private List<String> languages = List.of("java", "kotlin");
    }

    @Data
    public static class KnowledgeBase {
        private String storage = "elasticsearch";
        private String indexPrefix = "asgard-docs";
    }

    @Data
    public static class Analysis {
        private boolean enabled = true;
        private List<Rule> rules = List.of(
            new Rule("code-quality", "warning"),
            new Rule("security", "error")
        );
    }

    @Data
    public static class Rule {
        private String name;
        private String severity;

        public Rule() {}

        public Rule(String name, String severity) {
            this.name = name;
            this.severity = severity;
        }
    }
} 