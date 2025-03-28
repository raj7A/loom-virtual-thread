package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoopTest {

    @Test
    void triggerVT() {
        Loop.totalIterations = 100;
        Assertions.assertDoesNotThrow(() -> Loop.trigger(new String[]{"VT"}));
    }

    @Test
    void triggerTP() {
        Loop.totalIterations = 100;
        Assertions.assertDoesNotThrow(() -> Loop.trigger(new String[]{"TP"}));
    }
}