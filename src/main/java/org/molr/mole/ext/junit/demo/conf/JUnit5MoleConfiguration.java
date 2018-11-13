package org.molr.mole.ext.junit.demo.conf;

import com.google.common.collect.ImmutableSet;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.molr.mole.ext.junit.mole.JUnit5Mission;
import org.molr.mole.ext.junit.mole.JUnit5Mole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

@Configuration
public class JUnit5MoleConfiguration {

    @Autowired
    private Set<JUnit5Mission> missions;

    @Bean
    public JUnit5Mole demoJUnit5Mole() {
        return new JUnit5Mole(missions);
    }
}
