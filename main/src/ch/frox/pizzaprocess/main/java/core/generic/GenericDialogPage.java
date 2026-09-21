package ch.frox.pizzaprocess.main.java.core.generic;



/**
 * Generic class for dialog pages.
 * The enum only lists its pages.
 * The first enum constant will be the starting page.
 * The path of every page is built automatically by convention.
 * If a page enum does not follow the convention, you can override getPath() and or getFile().
 * If a single page constant in a normal page enum has an odd file name, you can also override  getPath() and or getFile() on that constant.
 * 
 *
 * where to save files convention:
 * - enum.java:  src/.../dialog/exampledialog/ExampleDialogPage.java
 * - page.xhtml: webContent/resources/pages/exampledialog/ExamplePage.xhtml
 * - page.css:   webContent/resources/pages/exampledialog/ExamplePage.css (next to the page, jsf only serves css and js from inside webContent/resources)
 * 
 * nameing convention:
 * - enum.java:     PascalCase
 * - enum constant: SCREAMING_SNAKE_CASE
 * - page.xhtml:    PascalCase
 * - dialog folder: flatcase
**/
public interface GenericDialogPage {
    // IMPORTANT: both methods are already implemented by every enum
    String name();
    Class<?> getDeclaringClass();



    default String getPath() {
        String packageName = getDeclaringClass().getPackageName();
        String folderName = packageName.substring(packageName.lastIndexOf('.') + 1);
        return "/resources/pages/" + folderName + "/" + getFile() + ".xhtml";
    }

    default String getFile() {
        StringBuilder file = new StringBuilder();
        for (String word : name().toLowerCase().split("_")) {
            if (word.isEmpty()) continue;
            file.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return file.toString();
    }
}