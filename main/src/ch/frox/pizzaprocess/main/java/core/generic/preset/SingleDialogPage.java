package ch.frox.pizzaprocess.main.java.core.generic.preset;

import ch.frox.pizzaprocess.main.java.core.generic.GenericDialogPage;



/**
 * Dialog page enum for dialogs with only one page.
 * Naming conventions still applie here, just name the only page of that dialog "Page.xhtml".
**/
public enum SingleDialogPage implements GenericDialogPage {
    PAGE;



    @Override
    public String getFile() {
        return "Page";
    }
}