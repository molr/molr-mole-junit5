package org.molr.mole.ext.junit.demo;

import org.molr.mole.server.conf.LocalMolrConfiguration;
import org.molr.mole.server.conf.MolrRestServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({LocalMolrConfiguration.class, MolrRestServiceConfiguration.class})
public class Junit5DemoMolrServer {

    public static void main(String... args) {
        SpringApplication.run(Junit5DemoMolrServer.class);
    }

}
