package org.molr.mole.ext.junit.demo;

import org.molr.agency.server.conf.LocalMolrConfiguration;
import org.molr.agency.server.rest.MolrAgencyRestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({MolrAgencyRestService.class, LocalMolrConfiguration.class})
public class Junit5DemoMolrServer {

    public static void main(String... args) {
        SpringApplication.run(Junit5DemoMolrServer.class);
    }

}
