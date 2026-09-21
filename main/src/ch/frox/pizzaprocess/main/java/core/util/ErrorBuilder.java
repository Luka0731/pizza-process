package ch.frox.pizzaprocess.main.java.core.util;

import ch.ivyteam.ivy.bpm.error.BpmError;



/**
 * Only use this class when you want to throw errors that get cauth by the error boudary event in the process.
 */
public final class ErrorBuilder {

    private ErrorBuilder() {}


    
    // miscellaneous
    public static BpmError programMiscellaneous(String message, Throwable cause) {
        return BpmError
            .create("pizzaprocess:something:miscellaneousProgramError")
            .withMessage(message)
            .withCause(cause)
            .build();
    }
}