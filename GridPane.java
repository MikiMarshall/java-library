/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * GridPane - A specialized space based on JavaFX ScrollPane
 *            providing an unlimited "desktop" for GridSpace.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import java.util.*;

/**
 * Since GridPane refuses to provide a method to tell what Node
 * is at a particular col/row (even though that's exactly how it
 * placed it there to begin with), this wrapper adds a lookup table
 * to GridPane to provide that functionality. It also adds some
 * default layout, like columns, rows, width, height, padding, etc.
 */
public class GridPane extends ScrollPane {
    // Constants
    private static final int MAX_COL_ROWS = 999;
    public enum OrientBy {
        COL, ROW;
    }

    // Fields
    private int columns;
    private int rows;
    private int width;
    private int height;
    private GridElement lastSelected = null;
    private static OrientBy orientation;
    private static TreeMap<String, GridSpace> gridSpaces;
    private TreeSet<GridElement> selectedElements;
    private TreeMap<Integer, GridElement> allElements;

    /**
     * Constructor.
     * @param columns   Initial grid size in columns.
     * @param rows      Initial grid size in rows.
     * @param width     Set column width.
     * @param height    Set row height.
     */
    public GridPane(int columns, int rows, int width, int height,
                    OrientBy orientation) {
        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;
        GridPane.orientation = orientation;

        // All spaces (GridSpace) that may contain elements (GridElement)
        gridSpaces = new TreeMap<>();

        // A list of all and currently selected elements (GridElement)
        allElements = new TreeMap<>();
        selectedElements = new TreeSet<>();
    }

    /**
     * Clear all elements from the grid by clearing each of the spaces in
     * the grid (but keeping the spaces intact).
     */
    protected void clearGrid() {
        // Clear any active elements
        OnActiveElement(null);

        // Clear any elements occupying grid spaces (but keep the spaces)
        for (GridSpace space : gridSpaces.values()) {
            space.clear();
        }

        // Also clear the lookup tables
        allElements.clear();
        selectedElements.clear();
        lastSelected = null;
    }

    /**
     * Clicking on anything other than a card, or pressing ESC
     * clears any selected cards.
     */
    private void clearSelections() {
        // Clear any active elements
        //OnActiveElement(null);

        // Remove highlight from all selected cards
        for (GridElement element : selectedElements) {
            element.showSelect(false);
        }

        // Clear selection list
        selectedElements.clear();
        lastSelected = null;
    }

    /**
     * Delete an element from the grid.
     * @param element       Element to delete.
     */
    private void delete(GridElement element) {
        // Clear the space holding the element
        GridSpace space = gridSpaces.get(toKey(
                element.getColumn(), element.getRow()));
        space.clear();

        // Also remove from lookup tables
        int id = element.getElementId();
        allElements.remove(id);
        selectedElements.remove(element);

        // Tell element subclass to delete itself (as from DB)
        OnDeleteElement(element);
    }

    /**
     * Deletes all elements in the selection list after prompting the
     * user to make sure.
     * @param elementName   The name of the element's subtype, for the prompt.
     */
    protected void deleteSelected(String elementName) {
        // Abort if none selected
        int n = selectedElements.size();
        if (n > 0) {
            // Prompt user if they're sure
            String msg = "Delete " + n + " " + elementName + ((n > 1) ? "s?" : "?");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg,
                    ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);

            // Shift default from Yes to No button
            Button yesButton = (Button) alert.getDialogPane().lookupButton(
                    ButtonType.YES);
            yesButton.setDefaultButton(false);
            Button noButton = (Button) alert.getDialogPane().lookupButton(
                    ButtonType.NO);
            noButton.setDefaultButton(true);

            // Display confirmation dialog
            alert.showAndWait();

            // If confirmed, delete each selected element
            if (alert.getResult() == ButtonType.YES) {
                // Make a copy, so the original lists can be updated
                List<GridElement> elements = new ArrayList<>(selectedElements);

                // Delete each element and remove from lookup tables
                for (GridElement element : elements) {
                    delete(element);
                }
            }
        }
    }

    /**
     * This is the thing that GridPane refuses to do, return the
     * Node at a particular location (not sure why).
     * @param column    Column in grid.
     * @param row       Row in grid.
     * @return          Space at this location.
     */
    protected GridElement get(int column, int row) {
        GridSpace space = gridSpaces.get(toKey(column, row));
        return space.get();
    }

    /**
     * Retrieve element by its ID.
     * @param id    Element ID.
     * @return      Element [GridElement]
     */
    GridElement getById(int id) {
        // Simply return
        return allElements.get(id);
    }

    /**
     * Return the column this space occupies in the grid.
     * @param space     Space in question [GridSpace].
     * @return          Column it occupies.
     */
    int getColumn(GridSpace space) {
        return javafx.scene.layout.GridPane.getColumnIndex(space);
    }

    /*
    int getColumns() {
        return columns;
    } */

    public static GridSpace getGridSpace(int column, int row) {
        return gridSpaces.get(toKey(column, row));
    }

    int getRow(GridSpace space) {
        return javafx.scene.layout.GridPane.getRowIndex(space);
    }

    /*
    int getRows() {
        return rows;
    } */

    protected Collection<GridElement> getSelections() {
        return selectedElements;
    }

    /**
     * Initialize the grid layout.
     * USAGE NOTE: Always call this from the subclass, otherwise no
     * setup will occur.
     *   protected void initLayout() {
     *      super.initLayout()
     *      ...
     *   }
     */
    protected void initLayout() {
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setMinSize(width, height);

        // For each grid location
        for (int col = 0; col < columns; col++) {
            for (int row =0; row < rows; row++) {
                // Add a placeholder "space" here and to the lookup table
                GridSpace gridSpace = new GridSpace(this, width, height);
                grid.add(gridSpace, col, row);
                gridSpaces.put(toKey(gridSpace), gridSpace);
            }
        }

        // Define CSS style classes
        getStyleClass().add("grid-scroll-pane");
        grid.getStyleClass().add("grid-pane");

        // Add the grid to the scroll pane
        setContent(grid);
    }

    /**
     * (Simplified version??)
     * Move element(s) from old pace to new space.
     * @param oldSpace      Move from here.
     * @param newSpace      To here.
     */
    void move(GridSpace oldSpace, GridSpace newSpace) {
        // Relocate elements from old space to new, then clear old
        newSpace.set(oldSpace.get());
        oldSpace.clear();

        // Clear multiple selections; just select the moved one
        selectOne(newSpace.get());
    }

    /**
     * An empty method to be overridden in the subclass to make an
     * element active in this space. (Usually from a single-click.)
     * @param element   Element to activate.
     */
    protected void OnActiveElement(GridElement element) {
        // OVERRIDE this in the subclass to activate an element
    }

    /**
     * An empty method to be overridden in the subclass to create an
     * element in this space.
     * @param column    At this column location.
     * @param row       At this row location.
     */
    protected void OnCreateElement(int column, int row) {
        // OVERRIDE this in the subclass to create an element
    }

    /**
     * An empty method to be overridden in the subclass to delete an
     * element in this space.
     * @param element   Element to activate.
     */
    protected void OnDeleteElement(GridElement element) {
        // OVERRIDE this in the subclass to create an element
    }

    /**
     * An empty method to be overridden in the subclass to edit an
     * element in this space. (Usually from a double-click.)
     * @param element   Element to edit.
     */
    protected void OnEditElement(GridElement element) {
        // OVERRIDE this in the subclass to edit an element
    }

    /**
     * Selects an element and shows the selection border.
     * @param element   the element to select.
     * @param select    select/deselect.
     */
    private void select(GridElement element, boolean select) {
        // Update selection to lookup and last selected
        if (select) {
            selectedElements.add(element);
            lastSelected = element;
        } else {
            selectedElements.remove(element);
            lastSelected = null;
        }

        // Update selection border
        element.showSelect(select);
    }

    /**
     * This either selects or clears all of the loaded elements.
     * @param value     (boolean) true = selectOne, false = clear.
     */
    public void selectAll(boolean value) {
        // Select/unselect each element
        for (GridElement element : allElements.values()) {
            select(element, value);
        }
    }

    /**
     * Select a block of cards on shift-click, from the last selected
     * (to emulate how it works in file managers) to this one, in
     * any direction.
     * @param clicked      one corner of the selection block.
     */
    void selectBlock(GridSpace clicked) {
        // Use last-selected as the other corner (standard?)
        GridSpace last = lastSelected.getGridSpace();

        // If there is another already selected
        if (last != null) {
            // Set lower/upper block corners
            int c1 = Integer.min(clicked.getColumn(), last.getColumn());
            int r1 = Integer.min(clicked.getRow(), last.getRow());
            int c2 = Integer.max(clicked.getColumn(), last.getColumn());
            int r2 = Integer.max(clicked.getRow(), last.getRow());

            // For each occupied space on the grid
            for (int col = 0; col < columns; col++) {
                for (int row = 0; row < rows; row++) {
                    GridSpace space = gridSpaces.get(toKey(col, row));
                    if (!space.isEmpty()) {
                        // Update selection in relation to block
                        select(space.get(),
                                ((col >= c1) && (row >= r1) &&
                                 (col <= c2) && (row <= r2)));
                    }
                }
            }

            // Restore the previous last selected element
            lastSelected = last.get();
        } else {
            // No other selections; is there an element here?
            if (!clicked.isEmpty()) {
                // Select it by itself (as if it's the "first")
                selectOne(clicked.get());
            }
        }
    }

    /**
     * Responding to a single click (wih no meta-keys), this selects one
     * element space, clearing any other that is currently selected.
     * @param element     Space to highlight.
     */
    protected void selectOne(GridElement element) {
        // Clear any current selections
        clearSelections();

        // Skip if there's nothing here
        if (element != null) {
            // Select this one and make it the active one
            select(element, true);
            OnActiveElement(element);
        }
    }

    /**
     * Select this space on a Ctrl-click, which simply adds this space
     * to the current list of selected spaces. If this spot is already
     * selected, then toggle it off again.
     * @param space      Add to the selection list.
     */
    void selectRandom(GridSpace space) {
        // If this spot has an element
        if (!space.isEmpty()) {
            GridElement element = space.get();
            // Toggle selection for this element
            boolean toggle = !selectedElements.contains(element);
            select(element, toggle);
        }
    }

    /**
     * Sets an element in the space set in the element's column
     * and row values.
     * @param element      Element to place here.
     */
    public void set(GridElement element) {
        // Add element at the desired space
        GridSpace space = gridSpaces.get(toKey(
                element.getColumn(), element.getRow()));
        space.set(element);

        // Add element to lookup table
        allElements.put(element.getElementId(), element);
    }

    /**
     * Set the desktop background at the ScrollPane level, which remains
     * static when the elements are scrolled.
     * @param filename      Image file to use as a background.
     */
    public void setBackground(String filename) {
        // Create the background object
        Image image = new Image(filename);
        Background background = new Background(new BackgroundImage(
                image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT));

        // Update the scrollpane background (which doesn't scroll)
        setBackground(background);
    }

    /**
     * If the user wants to switch orientations, this updates all of
     * the keys (from col.row to row.col, or vice-versa), without moving
     * anything.
     * @param newOrientation    the orientation to change to.
     */
    protected void setOrientation(OrientBy newOrientation) {
        // Clear any selections first before changing orientation
        selectAll(false);

        // Ignore if same orientation
        if (orientation != newOrientation) {
            // Extract all nodes with old orientation
            Collection<GridSpace> spaces = gridSpaces.values();

            // Update orientation and reload nodes into a new tree
            orientation = newOrientation;
            TreeMap<String, GridSpace> newGridMap = new TreeMap<>();
            for (GridSpace space : spaces) {
                newGridMap.put(toKey(space), space);
            }

            // Replace old tree with new one
            gridSpaces = newGridMap;
        }
    }

    /**
     * This is the fun part, where columns and rows are turned into a
     * single key to store and retrieve 2-dimensional data, using the
     * current Orientation setting to prioritize BY_COL or BY_ROW.
     * @param col           Column location.
     * @param row           Row location.
     * @return              String col.row/row.col key.
     */
    private static String toKey(int col, int row) {
        String result;
        final String FMT = "%03d.%03d";

        // Signal bounds error (without messy exceptions)
        if (col > MAX_COL_ROWS) {
            result = "BND.ERR";
            String msg = "Oops... rows or columns exceeds 999!";
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.showAndWait();
        } else {
            // Create a key from tow integers, by orientation
            if (orientation == GridPane.OrientBy.COL) {
                result = String.format(FMT, col, row);
            } else {
                result = String.format(FMT, row, col);
            }
        }

        return result;
    }

    /**
     * A wrapper for toKey() for when you know the space [GridSpace]
     * already and it's easier to just pass that than to extract its
     * location.
     * @param space         The grid space to get a key from.
     */
    private static String toKey(GridSpace space) {
        return (toKey(space.getColumn(), space.getRow()));
    }
}
