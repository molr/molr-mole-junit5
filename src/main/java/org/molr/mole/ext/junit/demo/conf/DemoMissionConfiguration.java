package org.molr.mole.ext.junit.demo.conf;

import org.molr.mole.ext.junit.mole.JUnit5Mission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoMissionConfiguration {

    @Bean
    public JUnit5Mission demoMission() {
        return JUnit5Mission.fromNameAndPackage("Molr Demo Tests","org.molr.mole.ext.junit.demo.missions");
    }

}
