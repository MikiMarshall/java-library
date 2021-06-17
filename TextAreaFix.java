/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * TextAreaFix - A small wrapper for JavaFX TextArea that replaces
 *               (odd) default TAB behavior with something more
 *               intuitive.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Thin extension of the JavaFX TextArea class, adding an event filter and
 * handle that changes the default TAB behavior to be more like other fields,
 * where:
 *  - TAB moves focus to the next focusable control, and
 *  - SHIFT-TAB moves to the previous.
 */
public class TextAreaFix extends TextArea {
    public TextAreaFix() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            Node node = (Node) event.getSource();

            // Fix Tab event
            if ((code == KeyCode.TAB) && !event.isShiftDown() &&
                    !event.isControlDown()) {
                // Consume original event and make our own
                event.consume();

                // Re-fire event with Control key down status reversed
                node.fireEvent(new KeyEvent(event.getSource(),
                            event.getTarget(), event.getEventType(),
                            event.getCharacter(), event.getText(),
                            event.getCode(), event.isShiftDown(),
                            true,
                            event.isAltDown(), event.isMetaDown()));
            }
        });
    }
}
