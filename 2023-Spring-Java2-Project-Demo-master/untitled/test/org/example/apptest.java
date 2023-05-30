package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

class apptest {

    @Test
    @DisplayName("Add two numbers")
    void add() {
        assertEquals(4.0, App.add(2,2));
    }

    @Test
    @DisplayName("Multiply two numbers")
    void multiply() {
        assertAll(() -> assertEquals(4.0, App.multiply(2, 2)),
                () -> assertEquals(-4.0, App.multiply(2, -2)),
                () -> assertEquals(4.0, App.multiply(-2, -2)),
                () -> assertEquals(0.0, App.multiply(1, 0)));
    }
}