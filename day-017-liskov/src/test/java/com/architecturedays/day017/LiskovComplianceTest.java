package com.architecturedays.day017;

import com.architecturedays.day017.despues.BirdFlightService;
import com.architecturedays.day017.despues.Eagle;
import com.architecturedays.day017.despues.FlyingBird;
import com.architecturedays.day017.despues.Penguin;
import com.architecturedays.day017.despues.Sparrow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that verify LSP compliance in the "despues" hierarchy.
 */
class LiskovComplianceTest {

    private final BirdFlightService service = new BirdFlightService();

    @Test
    @DisplayName("DESPUES: any FlyingBird can be substituted without surprise")
    void anyFlyingBirdSubstitutesWithoutSurprise() {
        List<FlyingBird> birds = List.of(new Sparrow(), new Eagle());

        // Every subtipo of FlyingBird satisfies the contract.
        // No exception, no behavior change, no instanceof.
        assertDoesNotThrow(() -> service.flyAll(birds));
    }

    @Test
    @DisplayName("DESPUES: Sparrow alone satisfies FlyingBird contract")
    void sparrowSatisfiesFlyingBirdContract() {
        List<FlyingBird> birds = List.of(new Sparrow());

        assertDoesNotThrow(() -> service.flyAll(birds));
    }

    @Test
    @DisplayName("DESPUES: Eagle alone satisfies FlyingBird contract")
    void eagleSatisfiesFlyingBirdContract() {
        List<FlyingBird> birds = List.of(new Eagle());

        assertDoesNotThrow(() -> service.flyAll(birds));
    }

    @Test
    @DisplayName("DESPUES: Penguin is still a Bird — it has a name and can describe itself")
    void penguinIsABirdWithBehavior() {
        Penguin penguin = new Penguin();

        assertEquals("Penguin", penguin.getName());
        assertEquals("Bird: Penguin", penguin.describe());
        // penguin.fly() does not exist — the compiler prevents the mistake entirely
        assertDoesNotThrow(penguin::swim);
    }

    @Test
    @DisplayName("DESPUES: Penguin cannot be passed to BirdFlightService — enforced at compile time")
    void penguinCannotBePassedToFlightService() {
        // This test documents the compile-time guarantee.
        // The following line would NOT compile — the type system prevents it:
        //
        //   service.flyAll(List.of(new Penguin())); // compile error: Penguin is not FlyingBird
        //
        // No runtime check needed. No try/catch. No instanceof.
        // The hierarchy itself is the protection.
        //
        // We verify the Penguin type is Bird but NOT FlyingBird.
        Penguin penguin = new Penguin();
        com.architecturedays.day017.despues.Bird bird = penguin;  // compiles: Penguin IS a Bird

        // FlyingBird flyingBird = penguin; // would NOT compile — that's the point
        assertEquals("Bird: Penguin", bird.describe());
    }

    @Test
    @DisplayName("DESPUES: empty list of FlyingBird is a valid no-op")
    void emptyListIsAValidNoOp() {
        assertDoesNotThrow(() -> service.flyAll(List.of()));
    }
}
