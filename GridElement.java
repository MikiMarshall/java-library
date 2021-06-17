/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * GridElement - A stackable JavaFX element used to populate
 *               GridPane spaces, which are empty and unclickable
 *               by default.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created:     2018.02.17
 * Version:     2019.12.30
 *
 * Notes:
 */

package mikilib;

import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Base class for any node that can occupy a GridPane subpane,
 * which helps GridPane keep track of nodes in the entire grid.
 * This also sets the drag-n-drop handlers, for highlighting and stuff.
 */
public class GridElement extends StackPane
        implements Comparable<GridElement> {
    // Constants
    private static final int BORDER_WIDTH = 3;
    private static final CornerRadii CORNER_RADII = new CornerRadii(5);
    private static final Color SELECTED_COLOR = Color.YELLOW;
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static Border SELECTED_BORDER = new Border(new BorderStroke(
            SELECTED_COLOR, BorderStrokeStyle.SOLID,
            CORNER_RADII, new BorderWidths(BORDER_WIDTH)));
    private static Border DEFAULT_BORDER = new Border(new BorderStroke(
            DEFAULT_COLOR, BorderStrokeStyle.SOLID,
            CORNER_RADII, new BorderWidths(BORDER_WIDTH)));
    private static final Border HOVER_BORDER = new Border(new BorderStroke(
            Color.GHOSTWHITE, BorderStrokeStyle.DOTTED,
            CORNER_RADII, new BorderWidths(BORDER_WIDTH)));

    // Fields
    static private Image dragCursor;
    private GridSpace parent;
    protected int id;

    /**
     * Constructor. Call to register handlers for drag-n-drop and
     * click mechanics.
     */
    protected GridElement(GridSpace parent, int id) {
        this.parent = parent;
        //this.column = col;
        //this.row = row;
        this.id = id;

        // Set handlers for card-based events
        setOnDragExited(this::onDragExit);
        setOnDragOver(this::onDragOver);
        setOnDragDetected(this::onDragged);
        setOnDragDropped(this::onDropped);
        setOnDragDone(this::onDragDone);

        // Set the initial border
        setBorder(DEFAULT_BORDER);

        // Define CSS style class
        getStyleClass().add("grid-element");
    }

    /**
     * Required for the Comparable interface, this method lets this
     * class work in a Set (TreeSet in GridPane).
     * @param element   An element object to compare to this one.
     * @return          Delta of the two objects (0 = same) using IDs.
     */
    public int compareTo(GridElement element) {
        // Use IDs as unique ordering keys
        return (this.id - element.id);
    }

    int getElementId() {
        return id;
    }

    protected int getColumn() {
        return parent.getColumn();
    }

    GridSpace getGridSpace() {
        return parent;
    }

    protected int getRow() {
        return parent.getRow();
    }

    /**
     * Drag completed, from the perspective of the card being dragged.
     * >>> Not sure what to do with this. Save()?
     * @param event     Drag done event.
     */
    private void onDragDone(DragEvent event) {
        // Find the card that was moved
        //Dragboard dragboard = event.getDragboard();

        // Stuff maybe goes here ...

    }

    /**
     * Clear the drag-over border when the drag moves away from this
     * location.
     * @param event     The source event.
     */
    private void onDragExit(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        // If drag is active, remove hover border
        if (dragboard.hasString()) {
            setBorder(DEFAULT_BORDER);
        }

        event.consume();
    }

    /**
     * Handles when a card has just started being dragged somewhere,
     * creating the dragboard that handles the data (card ID) being
     * transfered, sets the cursor and the transfer type.
     * @param event     Event that sensed a drag had started.
     */
    private void onDragged(MouseEvent event) {
        // Setup a dragboard for this operation
        Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);

        // Display a card cursor for the drag
        if (dragCursor != null) {
            dragboard.setDragView(dragCursor, 0, 0);
        }

        // Pass the element ID so we know what's being dragged
        ClipboardContent content = new ClipboardContent();
        content.putString(String.valueOf(id));
        dragboard.setContent(content);

        event.consume();
    }

    /**
     * Handles when a card or cards is dragged over another card,
     * which should provide a visual indication that this is a
     * drop-worthy location.
     * @param event     Drag event that fired this.
     */
    private void onDragOver(DragEvent event) {
        // If drag is active
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasString()) {
            // Ignore the part where it drags over itself
            if (!dragboard.getString().equals(String.valueOf(id))) {
                // Accept as move destination and highlight
                // >>> really? (not until stacks)
                event.acceptTransferModes(TransferMode.MOVE);
                setBorder(HOVER_BORDER);
            }
        }

        event.consume();
    }

    /**
     * Handles when a card is dropped on *this* card, which creates
     * a stack of cards with the original card on top, or onto a stack
     * which adds it to the bottom of the stack. Either way, the drop
     * target is a card.
     * @param event     Drag event that fired this.
     */
    private void onDropped(DragEvent event) {
        // If drag is active/valid
        boolean success = false;
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasString()) {

            // >>> Put stuff here to handle stack creation/management.

            success = true;
        }

        // Finalize the drop
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Set the border color of the card to indicate its active status.
     * @param value    boolean switch to set active status.
     */
    void showSelect(boolean value) {
        // Set the card border to indicate active state
        setBorder((value) ? SELECTED_BORDER : DEFAULT_BORDER);
    }

    /**
     * Sets the drag cursor for all drag and drop operations for elements.
     * @param cursor    the Image to display while dragging.
     */
    public static void setDragCursor(Image cursor) {
        // Setting the property just shows up under the cursor
        dragCursor = cursor;
    }

    /**
     * Set this element's location by setting a new parent location
     * [GridSpace], usually when it has been moved by drag and drop.
     * @param gridSpace     The grid space moved to.
     */
    void setLocation(GridSpace gridSpace) {
        parent = gridSpace;
    }

    /**
     * Set this element's location from column and row, locating the
     * appropriate space (GridSpace) to place it in. Usually used when
     * a saved card is first loaded and needs to derive its parent from
     * the saved location coordinates.
     * @param col   column coordinate.
     * @param row   row coordinate.
     */
    protected void setLocation(int col, int row) {
        parent = GridPane.getGridSpace(col, row);
    }
}
