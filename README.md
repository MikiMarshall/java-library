# java-library
Various handy Java methods that earned a place in my reusable code library.

## JavaFX class wrappers
### TextAreaFix 
 * Thin extension of the JavaFX TextArea class, adding an event filter and
   handle that changes the default TAB behavior to be more like other fields,
   where:
     - TAB moves focus to the next focusable control, and
     - SHIFT-TAB moves to the previous.

 * USAGE: Instantiate in place of TextArea to fix default TAB behavior:
```
    TextArea synopsis;
    ...
    synopsis = new TextAreaFix();
    // Otherwise works just like TextArea ...
    synopsis.getStyleClass().add("synopsis");
    synopsis.setPromptText("Synopsis");
    synopsis.textProperty().addListener((obs, oldText, newText) -> {
        if (card != null) {
            card.setSynopsis(newText);
        }
    });
```
