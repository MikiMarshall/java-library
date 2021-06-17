/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Dialog - A collection of my own reusable common dialogs.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2020.02.20
 * Version:     2020.02.20
 *
 * Notes:
 */

package mikilib;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Dialog {
    public static boolean chooseYesNo(String title, String msg) {
        // Define the dialog title, message and buttons
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);

        // Present dialog for user response and return result
        return (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES);
    }
}
