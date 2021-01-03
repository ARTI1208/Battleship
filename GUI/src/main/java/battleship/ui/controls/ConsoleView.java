package battleship.ui.controls;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleView extends ScrollPane {

    private final TextArea text = new TextArea();

    private final PrintStream outputStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int i) {
            text.appendText(String.valueOf((char) i));
        }
    });

    {
        text.setEditable(false);
        setContent(text);
        setFitToHeight(true);
        setFitToWidth(true);
        text.setWrapText(false);

    }

    /**
     * @return stream to write to console (log) view
     */
    public PrintStream getOutputStream() {
        return outputStream;
    }
}
