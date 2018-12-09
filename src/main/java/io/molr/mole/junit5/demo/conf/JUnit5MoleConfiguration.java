package io.molr.mole.junit5.demo.conf;

import io.molr.mole.junit5.mole.JUnit5Mission;
import io.molr.mole.junit5.mole.JUnit5Mole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class JUnit5MoleConfiguration {

    @Autowired
    private Set<JUnit5Mission> missions;

    @Bean
    public JUnit5Mole demoJUnit5Mole() {
        return new JUnit5Mole(missions);
    }
}
