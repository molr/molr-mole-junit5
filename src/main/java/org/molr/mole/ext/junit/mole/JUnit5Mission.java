package org.molr.mole.ext.junit.mole;

import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.util.Objects;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class JUnit5Mission {

    private final String name;
    private final LauncherDiscoveryRequest request;

    public JUnit5Mission(String name, LauncherDiscoveryRequest request) {
        this.name = name;
        this.request = request;
    }

    public static final JUnit5Mission fromNameAndClass(String name, Class<?> testClass) {
        return new JUnit5Mission(name, LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build());
    }


    public static final JUnit5Mission fromClass(Class<?> testClass) {
        return fromNameAndClass(testClass.getCanonicalName(), testClass);
    }

    public static final JUnit5Mission fromNameAndPackage(String name, String packageName) {
        return new JUnit5Mission(name, LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(packageName))
                .build());
    }

    public static final JUnit5Mission fromPackage(String packageName) {
        return fromNameAndPackage(packageName, packageName);
    }

    public String name() {
        return this.name;
    }

    public LauncherDiscoveryRequest request() {
        return this.request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JUnit5Mission that = (JUnit5Mission) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, request);
    }

    @Override
    public String toString() {
        return "JUnit5Mission{" +
                "name='" + name + '\'' +
                ", request=" + request +
                '}';
    }
}
