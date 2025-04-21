package com.tu.votingapp;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    // Read the HTTPS port from properties (the one defined in server.port)
    @Value("${server.port}")
    private int httpsPort;

    // Define the HTTP port you want to redirect from
    private final int httpPort = 8080; // Or 80 if running as root/admin

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return factory -> {
            // Enable SSL traffic constraint if SSL is enabled
            if (factory.getSsl() != null && factory.getSsl().isEnabled()) {
                factory.addAdditionalTomcatConnectors(createHttpConnector());
                factory.addContextCustomizers(this::securityCustomizer);
            }
        };
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false); // This connector is not secure
        connector.setRedirectPort(httpsPort); // Redirect requests to the HTTPS port
        return connector;
    }

    private void securityCustomizer(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL"); // Require SSL (transport layer security)
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*"); // Apply to all requests
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
    }
}
