package battleship.ui.controls;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldCellTest {

    @BeforeAll
    static void setUpApp() {
        Application app = new Application() {
            @Override
            public void start(Stage primaryStage) throws Exception {

            }
        };

        Thread t = new Thread(()-> {
            Application.launch(app.getClass());
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void setSelected() {
        FieldCell cell = new FieldCell();
        cell.setSelected(true);
        assertTrue(cell.isSelected());
        cell.setSelected(false);
        assertFalse(cell.isSelected());
    }

    @Test
    void isSelected() {
        FieldCell cell = new FieldCell();
        assertFalse(cell.isSelected());
    }

    @Test
    void markHit() {
        FieldCell cell = new FieldCell();
        cell.markHit();
        assertTrue(cell.getStyleClass().contains("field-cell-hit"));
        assertFalse(cell.getStyleClass().contains("field-cell-no-hit"));
    }

    @Test
    void unmarkHit() {
        FieldCell cell = new FieldCell();
        cell.markHit();
        cell.unmarkHit();
        assertTrue(cell.getStyleClass().contains("field-cell-no-hit"));
        assertFalse(cell.getStyleClass().contains("field-cell-hit"));
        cell.applyShootResult(FieldCell.ShootResult.MISS);
        cell.unmarkHit();
        assertEquals("", cell.getText());
    }

    @Test
    void applyShootResult() {
        FieldCell cell = new FieldCell();
        assertEquals("", cell.getText());
        cell.applyShootResult(FieldCell.ShootResult.MISS);
        assertEquals("-", cell.getText());
        cell.applyShootResult(FieldCell.ShootResult.HIT);
        assertEquals("X", cell.getText());
        cell.applyShootResult(FieldCell.ShootResult.SUNK);
        assertEquals("S", cell.getText());
        cell.applyShootResult(FieldCell.ShootResult.INVALID);
        assertEquals("S", cell.getText());
    }
}