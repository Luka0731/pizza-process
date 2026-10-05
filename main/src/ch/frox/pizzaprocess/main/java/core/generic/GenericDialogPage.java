package ch.frox.pizzaprocess.main.java.core.generic;



/**
 * Generic class for dialog pages.
 * 
 * NOTE:
 * The enum lists all dialog pages.
 * The first enum constant will be the starting page.
 * The path of every page is built automatically by convention.
 * If a page enum does not follow the convention, you can override getPath() and or getFile().
 * If a single page constant in a normal page enum has an odd file name, you can also override  getPath() and or getFile() on that constant.
 * 
 * WHERE TO SAVE FILES CONVENTION:
 * - enum.java:  src/.../dialog/exampledialog/ExampleDialogPage.java
 * - page.xhtml: webContent/resources/pages/exampledialog/ExamplePage.xhtml
 * - page.css:   webContent/resources/pages/exampledialog/ExamplePage.css
 * 
 * NAMING CONVENTION:
 * - enum.java:     PascalCase
 * - enum constant: SCREAMING_SNAKE_CASE
 * - page.xhtml:    PascalCase
 * - dialog folder: flatcase
**/
public interface GenericDialogPage {
    // NOTE: already implemented by every enum
    String name();



    default String getFile() {
        StringBuilder file = new StringBuilder();
        for (String word : name().toLowerCase().split("_")) {
            file.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return file.toString();
    }
}