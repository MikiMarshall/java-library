/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Config - Handles reading/updating an application config file.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created: 2018.02.17
 * Version: 2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.*;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Saves and restores all application-level configuration data to disk.
 */
public class Config {
    // Constants
    final private static String RECENT_FILE = "RecentFile";
    final public static int RECENT_FILE_MAX = 5;

    // Fields
    private String filename;
    private Properties props = new Properties();

    /**
     * Constructor. Loads a config file, if it exists with this name,
     * keeping it in memory for the application's lifespan.
     * @param filename      Configuration file.
     */
    public Config(String filename) {
        // Fields
        this.filename = filename;
        File file = new File(filename);

        // Only if file exists and is normal (not diretory or link)
        if (file.exists() && file.isFile()) {
            // Load the config file
            try (InputStream in = new FileInputStream(filename)) {
                props.load(in);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
                alert.showAndWait();
            }
        }
    }

    /**
     * Clear all recent file entries from the config file, in case
     * things get messy (like after files get moved about).
     */
    public void clearRecentFiles() {
        for (int v = 0; v <= RECENT_FILE_MAX; v++) {
            set((RECENT_FILE + v), "");
        }

        update();
    }

    /**
     * A wrapper for the base class getProperty() that first checks to
     * make sure the config file has been loaded. If no file was
     * loaded (e.g., none has been saved yet), then return "".
     * @param key           the key of the desired property.
     * @param defaultValue  value to return if key not found.
     * @return              value found, else the default.
     */
    public String get(String key, String defaultValue) {
        // Return value associated with this key, if any
        return (props.getProperty(key, defaultValue));
    }

    /**
     * Wrapper for getProperty() that converts saved Y/N to boolean.
     * @param key           property key.
     * @param defaultValue  boolean default if not found.
     * @return              value found, else the default.
     */
    public boolean get(String key, boolean defaultValue) {
        // Get the value for this key
        String result = get(key, "");

        // If no result, return default
        if (result.isEmpty()) {
            return defaultValue;
        } else {
            // Otherwise return value converted to boolean
            return (result.equals("Y"));
        }
    }

    /**
     * Get an entry from the recent file list. Only valid up to the
     * RECENT_FILE_MAX index.
     * @param n     Index, where 0 = most recent.
     * @return      Return file name at index.
     */
    public File getRecentFile(int n) {
        File file = null;

        // Retrieve entry in valid range
        if (n < RECENT_FILE_MAX) {
            String filename = get((RECENT_FILE + n), "");
            file = (filename.isEmpty()) ? null : new File(filename);
        }

        return file;
    }

    /**
     * Restores the saved layout metrix for the given scene.
     * @param key       Key used to save this scene's metrix.
     * @param stage     Stage to restore metrix to.
     */
    public void restoreMetrix(String key, Stage stage, boolean maxDefault) {
        // Restore maximized mode
        stage.setMaximized(get(key + ".maximized", maxDefault));

        // Retrieve metrix as a CSV string
        String csv = props.getProperty(key + ".metrix", "");
        if (!csv.isEmpty()) {
            // Use metrix to update stage
            Metrix metrix = new Metrix(csv);
            metrix.toStage(stage);
        }
    }

    /**
     * Save a scene's layout metrix using the node's name as the key.
     * @param key       Key name to save this scene as.
     * @param stage     Stage to update the metrix data for.
     */
    public void saveMetrix(String key, Stage stage) {
        // Save stage metrix data
        set(key + ".maximized", stage.isMaximized());
        set(key + ".metrix", new Metrix(stage));
    }

    /**
     * Set a value for this key, then update the config file.
     * @param key       key for retrieving this value later.
     * @param value     value to save.
     */
    public void set(String key, String value) {
        props.setProperty(key, value);
        update();
    }

    /**
     * Set a value for this key, converting boolean to Y/N string value.
     * @param key       key for retrieving this value later.
     * @param value     boolean data.
     */
    public void set(String key, boolean value) {
        // Convert boolean to alpha and set
        set(key, (value ? "Y" : "N"));
    }

    /**
     * Wrapper for set() for Metrix values, which are converted to
     * CSV string values.
     * @param key       key for retrieving this value later.
     * @param metrix    metrix data.
     */
    private void set(String key, Metrix metrix) {
        // Extract metrix string to set property
        set(key, metrix.toString());
    }

    /**
     * Sets the most recently opened file for the current application
     * as the first in the list and bumps the rest down one.
     * (entries are numbered 1 to RECENT_FILE_MAX).
     */
    public void setRecentFile(File file) {
        String filename = file.toString();

        // Load previous saved filenames
        LinkedList<String> entries = new LinkedList<>();
        for (int v = 0; v < RECENT_FILE_MAX; v++) {
            String entry = get((RECENT_FILE + v), "");

            // Omit blanks or filenames that match this one
            if (!(entry.isEmpty() || entry.equals(filename))) {
                entries.add(entry);
            }
        }

        // Add this one to the front and re-save the list
        entries.addFirst(filename);
        int size = entries.size();

        // Resave the list
        for (int v = 0; v < RECENT_FILE_MAX; v++) {
            set((RECENT_FILE + v), (v < size) ? entries.get(v) : "");
        }

        // Update the config file
        update();
    }

     /**
     * Write the current properties to the config file.
     * Do this automatically every time a property is updated, to
     * save ourselves from the update-on-exit thing.
     */
    private void update() {
        try (OutputStream out = new FileOutputStream(filename)) {
            props.store(out, null);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.toString());
            alert.showAndWait();
        }
    }
}
