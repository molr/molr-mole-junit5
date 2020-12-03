package io.molr.mole.junit5.demo.conf;

import io.molr.mole.junit5.mole.JUnit5Mission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoMissionConfiguration {

    @Bean
    public JUnit5Mission demoMission() {
        return JUnit5Mission.fromNameAndPackage("Molr Demo Tests","io.molr.mole.junit5.demo.missions");
    }

}
