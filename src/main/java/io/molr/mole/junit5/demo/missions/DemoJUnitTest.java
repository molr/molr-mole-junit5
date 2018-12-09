package io.molr.mole.junit5.demo.missions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DemoJUnitTest {

    @Test
    public void successfulTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void failingTest() {
        Assertions.assertTrue(false);
    }
}
