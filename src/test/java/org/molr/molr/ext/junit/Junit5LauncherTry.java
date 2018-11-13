package org.molr.molr.ext.junit;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.molr.mole.ext.junit.demo.missions.DemoSlowJUnitTest;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class Junit5LauncherTry {

    @Test
    public void testPlan() {
        String packageName = "org.molr.mole.ext.junit.demo.missions";
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(packageName), selectClass(DemoSlowJUnitTest.class))
                .build();

        //ExtensionContext.Namespace.create()

        //LauncherConfig.builder().

        Launcher launcher = LauncherFactory.create();



        TestPlan testPlan = launcher.discover(request);

        testPlan.getRoots().forEach(e -> testPlan.getDescendants(e).stream().map(d -> d.getSource()).forEach(System.out::println));


    }

}
