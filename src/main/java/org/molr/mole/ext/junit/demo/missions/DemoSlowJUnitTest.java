package org.molr.mole.ext.junit.demo.missions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DemoSlowJUnitTest {

    @Test
    public void successfulTest() throws InterruptedException {
        Thread.sleep(3000);
        Assertions.assertTrue(true);
    }

    @Test
    public void anotherSuccessfulTest() throws InterruptedException {
        Thread.sleep(3000);
        Assertions.assertTrue(true);
    }

    @ParameterizedTest
    @ValueSource(longs = {500, 1000, 2000})
    public void parametrized(long millis) throws InterruptedException {
        Thread.sleep(2000);
        Assertions.assertEquals(1000, millis);
    }
}
