package io.molr.mole.junit5.demo;

import io.molr.mole.core.conf.LocalSuperMoleConfiguration;
import io.molr.mole.server.conf.SingleMoleRestServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({LocalSuperMoleConfiguration.class, SingleMoleRestServiceConfiguration.class})
public class Junit5DemoMolrServer {

    public static void main(String... args) {
        SpringApplication.run(Junit5DemoMolrServer.class);
    }

}
