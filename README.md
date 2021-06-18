# java-library

Reusable Java utility classes, functions, and class wrappers I created while building a grid-based GUI organizer application for writers.

Most of these came from my Cardz application, an infinite scrolling desktop of draggable index cards, for organizing notes and ideas for writers. The majority of the code that makes up that application resides in this library, since I designed as much of it as possible to be reusable in other grid-based applications--everything from grid menus and launchers to card games. 

<img src="https://github.com/Mikibits/java-library/blob/master/screenshot.png" width=500 height=auto style="float:right;">

This collection also includes other classes that help simplify commonly used frameworks and practices, such as maintaining config files, SQLite databases, and wrappers to fix weird (nonintuitive) methods in Java's own libraries.

For example:

## TextAreaFix.java

 * A thin extension of the JavaFX TextArea class, adding an event filter and
   handle that changes the default TAB behavior to be more like other fields,
   where:
     - TAB moves focus to the next focusable control, and
     - SHIFT-TAB moves to the previous.

 * USAGE: Simply instantiate in place of TextArea to fix default TAB behavior:
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

See the code for usage notes in other files in this repository, where they are not self-explanatory.

---

I welcome comments and ideas for making these more useful and elegant, and queries to better document them.


Also, I am open for work in Java, Python, Javascript, HTML/CSS, C++, or just about any other language (I pick them up pretty quickly).
