package fr.eletutour.asgard.heimdall.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "heimdall.elk")
public class HeimdallElkProperties {

    /**
     * Host of the Logstash instance.
     */
    private String host = "localhost";

    /**
     * Port of the Logstash instance.
     */
    private int port = 5044;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
