/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Setup - various setup tools, like common dialogue boxes, to
 *         change an application's settings.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;

/**
 * Setup creates dialogs, as needed, to present various
 * settings choices from the menu. This is an alternative to
 * the original tabbed window containing every setup option
 * in an attempt to keep it simple.
 */
public class Setup {
    // Constants
    final private static double DLG_WIDTH = 600;
    final private static double DLG_HEIGHT = 550;
    final private static int BG_ICON_SIZE = 64;

    // Fields
    private Stage backgroundDialog;
    private Stage parent;
    private String result;
    private String stylesheet;

    /**
     * Constructor.
     * @param parent    Parent stage upon which we modally display.
     */
    public Setup(Stage parent, String stylesheet) {
        this.parent = parent;
        this.stylesheet = stylesheet;
        backgroundDialog = null;
    }

    public String backgroundChooser(String title, String imageFolder) {
        result = "";

        // Do layout only if it hasn't been done yet
        if (backgroundDialog == null) {
            // Create a chooser backgroundDialog
            backgroundDialog = new Stage();
            backgroundDialog.initStyle(StageStyle.UTILITY);
            backgroundDialog.initModality(Modality.APPLICATION_MODAL);
            backgroundDialog.setResizable(false);
            backgroundDialog.initOwner(parent);
            backgroundDialog.setTitle(title);

            // Layout stuffs goes here
            TilePane layout = new TilePane();
            layout.getStyleClass().add("bg-chooser");

            // Retrieve all backgrounds in our resources folder
            File folder = new File(imageFolder);
            File[] files = folder.listFiles();

            // For each found...
            if (files != null && files.length > 0) {
                for (File file : files) {
                    // Create an icon for it with its name as a tooltip
                    String name = file.getPath();
                    ImageView image = new ImageView(new Image(name,
                            BG_ICON_SIZE, BG_ICON_SIZE, true, true));
                    image.getStyleClass().add("image");

                    // Create an image button and add to grid
                    Button button = new Button("", image);
                    button.getStyleClass().add("button");
                    button.setTooltip(new Tooltip(file.getName()));
                    button.setOnAction(event -> setResult(name));
                    layout.getChildren().add(button);
                }
            } else {
                // Missing resources...
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Background texture resources not found.");
                alert.show();
            }

            // Create scene from layout and show backgroundDialog
            Scene scene = new Scene(layout, DLG_WIDTH, DLG_HEIGHT);
            scene.getStylesheets().add(stylesheet);
            backgroundDialog.setScene(scene);
        }

        // Display the dialog and wait for a click
        backgroundDialog.showAndWait();

        // The result should have been set by now
        return result;
    }

    /**
     * Internal event handler to store the result of a chooser
     * backgroundDialog, so I can return it in the same method call.
     * @param value     Value to set as the result.
     */
    // Result setter event handler
    private void setResult(String value) {
        // Set the result to return, then close the backgroundDialog
        result = value;
        backgroundDialog.close();
    }
}
