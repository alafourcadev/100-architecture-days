package com.architecturedays.day017;

import com.architecturedays.day017.antes.Bird;
import com.architecturedays.day017.antes.BirdFlightService;
import com.architecturedays.day017.antes.Eagle;
import com.architecturedays.day017.antes.Penguin;
import com.architecturedays.day017.antes.Sparrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests that document the LSP violation in the "antes" hierarchy.
 */
class LiskovViolationTest {

    private final BirdFlightService service = new BirdFlightService();

    @Test
    @DisplayName("ANTES: flying birds work fine — the client has no reason to suspect a problem")
    void flyingBirdsWorkFine() {
        List<Bird> birds = List.of(new Sparrow(), new Eagle());

        // No issue here. Client trusts the Bird contract and it holds.
        assertDoesNotThrow(() -> service.flyAll(birds));
    }

    @Test
    @DisplayName("ANTES: adding a Penguin to the list explodes at runtime — LSP violation")
    void penguinExplodesAtRuntime() {
        List<Bird> birds = List.of(new Sparrow(), new Eagle(), new Penguin());

        // The client did nothing wrong. It called the contract Bird advertises.
        // The subtipo lied — and the runtime pays the price.
        assertThrows(
                UnsupportedOperationException.class,
                () -> service.flyAll(birds),
                "Penguin.fly() should throw — this is the LSP violation in action");
    }

    @Test
    @DisplayName("ANTES: even a single Penguin in a mixed list is enough to blow up")
    void singlePenguinInListBlowsUp() {
        List<Bird> birds = List.of(new Penguin());

        assertThrows(UnsupportedOperationException.class, () -> service.flyAll(birds));
    }
}
