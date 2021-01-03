package battleship;

import battleship.ships.EmptySea;
import battleship.ships.Ship;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OceanTest {

    @Test
    void placeAllShipsRandomly() {
        Ocean o = new Ocean();
        Set<Ship> ships = new HashSet<>();

        for (int i = 0; i < Ocean.HEIGHT; i++) {
            for (int j = 0; j < Ocean.WIDTH; j++) {
                if (o.getShipArray()[i][j] instanceof EmptySea)
                    continue;

                Ship s = o.getShipArray()[i][j];
                ships.add(s);

                Rectangle r = s.getShipWithFrameRect();
                for (int k = r.y; k < r.y + r.height; k++) {
                    for (int l = r.x; l < r.x + r.width; l++) {
                        if (o.getShipArray()[i][j] instanceof EmptySea)
                            continue;

                        assertEquals(s, o.getShipArray()[i][j]);
                    }
                }
            }
        }

        assertEquals(10, ships.size());
    }

    @Test
    void isOccupied() {
        Ocean o = new Ocean(false);

        for (int i = 0; i < Ocean.HEIGHT; i++) {
            for (int j = 0; j < Ocean.WIDTH; j++) {
                assertFalse(o.isOccupied(i, j));
            }
        }
    }

    @Test
    void shootAt() {
        Ocean o = new Ocean();

        if (o.getShipArray()[0][4] instanceof EmptySea) {
            assertFalse(o.shootAt(0, 4));
        } else {
            assertTrue(o.shootAt(0, 4));
        }
    }

    @Test
    void isGameOver() {
        Ocean o = new Ocean();

        assertFalse(o.isGameOver());

        for (int i = 0; i < Ocean.HEIGHT; i++) {
            for (int j = 0; j < Ocean.WIDTH; j++) {
                o.shootAt(i, j);
            }
        }

        assertTrue(o.isGameOver());
    }
}