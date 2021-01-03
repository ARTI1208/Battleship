package battleship.ships;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptySeaTest {

    @Test
    void shootAt() {
        EmptySea emptySea = new EmptySea(2, 3);

        assertFalse(emptySea.shootAt(2, 4));
        assertFalse(emptySea.isHit(2, 3));

        assertFalse(emptySea.shootAt(2, 3));
        assertTrue(emptySea.isHit(2, 3));
    }
}