package battleship.ships;

import battleship.Ocean;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    Ship ship;

    @Test
    void isHorizontal() {
        Ship ship = new Destroyer();

        assertFalse(ship.isHorizontal());
    }

    @Test
    void setHorizontal() {
        Ship ship = new Submarine();

        ship.setHorizontal(true);
        assertTrue(ship.isHorizontal());

        ship.setHorizontal(false);
        assertFalse(ship.isHorizontal());
    }

    @Test
    void getLength() {
        Ship ship;

        ship = new Battleship();
        assertEquals(4, ship.getLength());

        ship = new Cruiser();
        assertEquals(3, ship.getLength());

        ship = new Destroyer();
        assertEquals(2, ship.getLength());

        ship = new Submarine();
        assertEquals(1, ship.getLength());

        ship = new EmptySea(4, 5);
        assertEquals(1, ship.getLength());
    }

    @Test
    void getShipWithFrameRect() {
        Ship ship;
        ship = new Battleship();
        ship.setBowRow(4);
        ship.setBowColumn(6);
        ship.setHorizontal(true);

        assertEquals(new Rectangle(5, 3, 5, 3), ship.getShipWithFrameRect());


        ship = new Cruiser();
        ship.setBowRow(4);
        ship.setBowColumn(2);

        assertEquals(new Rectangle(1, 3, 3, 5), ship.getShipWithFrameRect());


        ship = new Cruiser();
        ship.setBowRow(8);
        ship.setBowColumn(2);

        assertNull(ship.getShipWithFrameRect());
    }

    @Test
    void checkAndPlace() {

        ship = new Cruiser();
        ship.setBowRow(11);
        ship.setBowColumn(2);

        assertFalse(ship.checkAndPlace(ship.getBowRow(), ship.getBowColumn(), false, new Ocean(false)));


        ship.setBowRow(8);
        ship.setBowColumn(2);

        assertFalse(ship.checkAndPlace(ship.getBowRow(), ship.getBowColumn(), false, new Ocean(false)));

        ship.setBowRow(2);
        ship.setBowColumn(2);

        assertTrue(ship.checkAndPlace(ship.getBowRow(), ship.getBowColumn(), false, new Ocean(false)));
    }

    @Test
    void shootAt() {
        ship = new Cruiser();
        ship.setBowRow(5);
        ship.setBowColumn(2);
        assertTrue(ship.shootAt(6, 2));
        assertTrue(ship.shootAt(6, 2));
        assertTrue(ship.shootAt(7, 2));
        assertTrue(ship.shootAt(5, 2));

        //now is sunk
        assertFalse(ship.shootAt(6, 2));

        assertFalse(ship.shootAt(0, 0));
    }

    @Test
    void isSunk() {
        ship = new Cruiser();
        ship.setBowRow(5);
        ship.setBowColumn(2);

        assertFalse(ship.isSunk());

        ship.shootAt(5, 2);
        ship.shootAt(6, 2);
        ship.shootAt(7, 2);

        assertTrue(ship.isSunk());
    }

    @Test
    void isHit() {
        ship = new Cruiser();
        ship.setBowRow(5);
        ship.setBowColumn(2);

        assertFalse(ship.isHit(5, 2));
        assertFalse(ship.isHit(6, 2));
        assertFalse(ship.isHit(7, 2));

        ship.shootAt(5, 2);
        assertTrue(ship.isHit(5, 2));
        assertFalse(ship.isHit(6, 2));
        assertFalse(ship.isHit(7, 2));

        ship.shootAt(6, 2);
        assertTrue(ship.isHit(5, 2));
        assertTrue(ship.isHit(6, 2));
        assertFalse(ship.isHit(7, 2));

        ship.shootAt(7, 2);
        assertTrue(ship.isHit(5, 2));
        assertTrue(ship.isHit(6, 2));
        assertTrue(ship.isHit(7, 2));
    }
}