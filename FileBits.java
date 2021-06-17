/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * FileBits - Fun additions to File to add additional functionality
 *            for other utility classes and stuff.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * FileBits class, housing any handy reusable bits that don't fit in another
 * static "Utility" class.
 * Static/singleton - this class is merely a container for helpful methods.
 */
public class FileBits {
    private static FileBits ourInstance = new FileBits();

    /**
     * Return instance of static instance. Not sure where this is useful.
     * @return      the only instance of this class we have.
     */
    public static FileBits getInstance() {
        return ourInstance;
    }

    /**
     * Private constructor, ensuring the class a singleton.
     */
    private FileBits() {}

    /**
     * Private common code for the open and save dialogs, handling
     * creating the FileChooser dialog and setting the extension filter.
     * @param title     Dialog title.
     * @param ext       Extension filter.
     * @param extInfo   Extension filter description.
     * @return          The chooser dialog object.
     */
    private static FileChooser createFileChooser(String title, String ext,
                                                 String extInfo) {
        // Create dialog and file filter
        FileChooser dlg = new FileChooser();
        dlg.setTitle(title);
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter(extInfo, "*" + ext);
        dlg.getExtensionFilters().add(filter);

        // Return the file chooser dialog
        return dlg;
    }

    /**
     * Fix a missing file extension, typically happening after using a
     * Save FileChooser with an extension filter, where the user assumes
     * the extension is appended (but it isn't).
     * @param file      File returned from the FileChooser
     * @param ext       Extension that's supposed to be there
     * @return          Fixed file, or the null if none chosen
     */
    private static File fixFileExtension(File file, String ext) {
        if (file != null) {
            String str = file.getAbsolutePath();
            // If the extension is missing, append one
            if (!str.endsWith(ext)) {
                return new File(str + ext);
            }
        }
        // Pass good files, or if they are null
        return file;
    }

    /**
     * Simple wrapper for the DirectoryChooser(), used to select a
     * folder to open/save something without selecting a specific file.
     * @param stage     Parent window.
     * @param title     Window title to display
     * @return          Select folder (or null if none)
     */
    public static File folderChooser(Stage stage, String title) {
        DirectoryChooser dlg = new DirectoryChooser();
        dlg.setTitle(title);

        return (dlg.showDialog(stage));
    }

    /**
     * Simple wrapper for the the file open dialog that chooses a file with
     * the desired extension (only one for now).
     * @param stage     Parent stage to display the dialog over.
     * @param title     Dialog title.
     * @param ext       Filter the files seen to this extension.
     * @param extInfo   Description of the filter extension.
     * @return          The chosen File object, or null if none selected.
     */
    public static File openDialog(Stage stage, String title, String ext,
                                  String extInfo) {
        // Show the dialog and return the user's choice
        FileChooser dlg = createFileChooser(title, ext, extInfo);
        return (dlg.showOpenDialog(stage));
    }

    /**
     * Simple wrapper for the the file save dialog that accepts a file with
     * the desired extension (only one for now). Unlike the open dialog, this
     * one makes sure the proper extension is appended, since the file chooser
     * doesn't do this by default.
     * @param stage     Parent stage to display the dialog over.
     * @param title     Dialog title.
     * @param ext       Filter the files seen to this extension.
     * @param extInfo   Description of the filter extension.
     * @return          The chosen File object, or null if none selected.
     */
    public static File saveDialog(Stage stage,  String title, String ext,
                                  String extInfo) {
        // Show the dialog and return the user's choice, with extension
        FileChooser dlg = createFileChooser(title, ext, extInfo);
        return fixFileExtension(dlg.showSaveDialog(stage), ext);
    }
}
