package ch.frox.pizzaprocess.main.java.core.exception.crash;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class CircularDependencyException extends ConfigurationException {
    
    public CircularDependencyException(Class<?> type, Set<Class<?>> inCreation) {
        super("circular dependency: " + describeChain(type, inCreation));
    }



    private static String describeChain(Class<?> type, Set<Class<?>> inCreation) {
        List<String> names = new ArrayList<>();
        inCreation.forEach(c -> names.add(c.getSimpleName()));
        names.add(type.getSimpleName());
        return String.join(" -> ", names);
    }
}