/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * GridSpace - A stackable placeholder for elements to populate
 *             GridPane spaces, since they are by default empty
 *             and unclickable.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.geometry.Insets;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * A placeholder for GridPane subpanes to provide handlers for
 * user clicks, as well as provide a container for elements
 * (GridElement) that can be placed in the grid. This is necessary
 * because GridPane subpanes are created empty and cannot respond to
 * any input.
 */
public class GridSpace extends StackPane {
    // Constants
    private static final int BORDER_WIDTH = 1;
    private static final CornerRadii CORNER_RADII = new CornerRadii(5);
    private static final Color HOVER_COLOR = Color.GHOSTWHITE;
    private static final Border HOVER_BORDER = new Border(new BorderStroke(
            HOVER_COLOR, BorderStrokeStyle.DOTTED,
            CORNER_RADII, new BorderWidths(BORDER_WIDTH)));

    // Fields
    private final GridPane parent;

    /**
     * Constructor.
     */
    GridSpace(GridPane parent, int width, int height) {
        this.parent = parent;

        // Define CSS style class
        getStyleClass().add("grid-space");

        // Intialize layout
        initLayout();

        // Set default size
        setPrefSize(width, height);

        // Set event handlers
        setOnMouseClicked(this::onClicked);
        setOnDragOver(this::onDragOver);
        setOnDragExited(this::onDragExit);
        setOnDragDropped(this::onDropped);
        // There's no onDragDetected, cuz spaces don't move
    }

    /**
     * Clear any element(s) occupying this space.
     */
    void clear() {
        // Delete all child elements
        getChildren().clear();
    }

    /**
     * Get element(s) occupying this space.
     * @return      Element(s) in this space.
     */
    public GridElement get() {
        // >>> Currently only returns one
        return (GridElement)getChildren().get(0);
    }

    /**
     * Get the column for this space.
     * @return      column number.
     */
    int getColumn() {
        // Ask the grid where we are
        return parent.getColumn(this);
    }

    /**
     * Get the row for this space.
     * @return      row number.
     */
    int getRow() {
        // Ask the grid where we are
        return parent.getRow(this);
    }

    /**
     * Initialize the display of this node.
     */
    private void initLayout() {
        // Create inset for the selection border to show
        setPadding(new Insets(BORDER_WIDTH));
    }

    /**
     * Check if there's an element here.
     * @return      boolean, true = no element here.
     */
    boolean isEmpty() {
        // Pass on child status
        return (getChildren().isEmpty());
    }

    /**
     * Double-clicking on a GridSpace (an invisible dummy card in an "empty"
     * grid space) creates a new card in that spot.
     * @param event         The click event.
     */
    private void onClicked(MouseEvent event) {
        // Determine type of click
        switch (event.getClickCount()) {
            case 1:     // Single-click
                // Check for meta-keys (Shift, Ctrl)
                if (event.isShiftDown()) {
                    // Shift-click selects a block of cards
                    parent.selectBlock(this);
                } else if (event.isControlDown()) {
                    // Ctrl-click toggles random cards
                    parent.selectRandom(this);
                } else {
                    // Simple click selects or edits a single card
                    if (isEmpty()) {
                        // No element here, unselect all others
                        parent.selectAll(false);
                        parent.OnActiveElement(null);
                    } else {
                        // Select element here
                        parent.selectOne(get());
                    }
                }
                break;

            case 2:     // Double-click
                // If there's no element in this space
                if (isEmpty()) {
                    // Create a new element here
                    parent.OnCreateElement(getColumn(), getRow());
                } else {
                    // Edit the element here
                    parent.OnEditElement(get());
                }
                break;
        }
    }

    /**
     * Clear the drag-over border when the drag moves away from this
     * location.
     * @param event     The source event.
     */
    private void onDragExit(DragEvent event) {
        // Remove drag-over border
        if (event.getDragboard().hasString()) {
            setBorder(Border.EMPTY);
        }

        event.consume();
    }

    /**
     * Handles an element dropped onto a blank space, which calls
     * a method to reposition the element from there to here.
     * @param event     Mouse event that started this.
     */
    private void onDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        // If there's an active drag happening...
        if (dragboard.hasString()) {
            // Get item id stored when drag started
            int id = Integer.parseInt(dragboard.getString());
            GridElement element = parent.getById(id);
            GridSpace from = element.getGridSpace();

            // Relocate the element in the grid
            parent.move(from, this);
            success = true;
        }

        // Finalize drop
        event.setDropCompleted(success);
        event.consume();
    }


    /**
     * Handle when element(s) are dragged over a blank space in the grid.
     * @param event     Mouse event that started this.
     */
    private void onDragOver(DragEvent event) {
        // If drag is active
        if (event.getDragboard().hasString()) {
            // Set as valid move destination and show drag border
            event.acceptTransferModes(TransferMode.MOVE);
            setBorder(HOVER_BORDER);
        }

        event.consume();
    }

    /**
     * Set an element to this space.
     * @param element       Element to put here.
     */
    public void set(GridElement element) {
        // Let the element know where it is >>> (still necessary?)
        if (element != null) {
            element.setLocation(this);
        }

        // Add element to this space
        getChildren().add(element);
    }
}
