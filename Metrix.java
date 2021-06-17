/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Metrix - Handles everything to do with saving and restoring
 *          UI window metrix to make it easier to reopen an app
 *          in the same state it was when it was closed.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created: 2018.02.17
 * Version: 2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.regex.PatternSyntaxException;

/**
 * Simplifies dealing with window (JavaFX Node) metrix. Provides an easy way
 * not only to pass multiple values (x, y, w, h), but to extract and set Node
 * metrix, reformat them for saving to a file, and reading them back.
 */
public class Metrix {
    private double x, y, w, h;

    /**
     * Constructor for an empty object, to use for conversions.
     */
    public Metrix() {
        x = 0;
        y = 0;
        w = 0;
        h = 0;
    }

    /**
     * Constructor for x and y (or row and column) coordinates.
     * @param x     or row.
     * @param y     or column.
     */
    public Metrix(double x, double y) {
        this.x = x;
        this.y = y;
        w = 0;
        h = 0;
    }

    /**
     * Constructor for full instantiation of node metrix values.
     * @param x     Position x.
     * @param y     Position y.
     * @param w     Size width.
     * @param h     Size height.
     */
    public Metrix(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * Constructor that automatically pulls metrix data directly from
     * the node.
     * @param node      JavaFX node to pull metrix data from.
     */
    public Metrix(Node node) {
        x = node.getLayoutX();
        y = node.getLayoutY();
        w = node.getLayoutBounds().getWidth();
        h = node.getLayoutBounds().getHeight();
    }

    /**
     * Constructor that automatically pulls metrix data directly from
     * the scene.
     * @param stage     JavaFX scene to pull metrix data from.
     */
    Metrix(Stage stage) {
        x = stage.getX();
        y = stage.getY();
        w = stage.getWidth();
        h = stage.getHeight();
    }

    /**
     * Constructor that pulls metrix info from a string in CSV format,
     * especially handy for receiving values from a SQLite record.
     * @param value     String of numbers in CSV format.
     */
    Metrix(String value) {
        try {
            // Parse the string for metrix values
            if (!value.isEmpty()) {
                String[] data = value.split(", ");
                x = Double.parseDouble(data[0]);
                y = Double.parseDouble(data[1]);
                w = Double.parseDouble(data[2]);
                h = Double.parseDouble(data[3]);
            }
        } catch (PatternSyntaxException | NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }

    /**
     * Use current metrix to relocate and size a node.
     * @param node      Node to update from metrix.
     */
    void toNode(Node node) {
        // Restore position and size
        node.resizeRelocate(x, y, w, h);
    }

    /**
     * Use current metrix to relocate and size a scene.
     * @param stage     Scene to update from metrix.
     */
    void toStage(Stage stage) {
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(w);
        stage.setHeight(h);
    }

    /**
     * Convert current metrix to a string in CSV format, especially useful
     * for saving as text in a SQLite database.
     * @return      Data as a CSV string.
     */
    @Override
    public String toString() {
        // Convert from data to CSV string
        return String.format("%f, %f, %f, %f", x, y, w, h);
    }
}
