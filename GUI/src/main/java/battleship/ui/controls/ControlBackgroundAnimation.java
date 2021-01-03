package battleship.ui.controls;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ControlBackgroundAnimation {

    Control control;
    FillTransition fillTransition;
    Rectangle fakeShape;
    Runnable onFinishedRunnable;

    public ControlBackgroundAnimation(Duration duration, Control control, Color clickColor, Color defaultColor) {
        this.control = control;
        fakeShape = new Rectangle();

        fakeShape.setFill(defaultColor);

        fillTransition = new FillTransition(duration, fakeShape, clickColor, defaultColor);
        fillTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                double k = 4;
                Color color = (Color) fakeShape.getFill();

                String c = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));

                control.setStyle("-fx-body-color: " + c);

                if (t < 1 / k)
                    return 1 - Math.pow(k * t, 1);

                return (t - 1 / k) / (1 - 1 / k);
            }
        });
        fillTransition.setOnFinished(event -> {
            Color color = (Color) fakeShape.getFill();
            control.setStyle("-fx-body-color: " + String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255)));
        });
    }

    /**
     * Sets custom runnable that should be run after animation ended
     *
     * @param r runnable that should be run after animation ended
     */
    public void setOnFinished(Runnable r) {
        onFinishedRunnable = r;
        fillTransition.setOnFinished(e -> {
            Color color = (Color) fakeShape.getFill();
            control.setStyle("-fx-body-color: " + String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255)));

            r.run();
        });
    }

    /**
     * Returns custom runnable that should be run after animation ended
     *
     * @return custom runnable that should be run after animation ended
     */
    public Runnable getOnFinished() {
        return onFinishedRunnable;
    }

    /**
     * Starts animation
     */
    public void play() {
        fillTransition.play();
    }

    /**
     * Ends animation
     */
    public void stop() {
        fillTransition.stop();
    }
}
