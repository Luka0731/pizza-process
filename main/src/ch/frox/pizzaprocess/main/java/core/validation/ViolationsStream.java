package ch.frox.pizzaprocess.main.java.core.validation;

import java.util.ArrayList;
import java.util.List;

import ch.frox.pizzaprocess.main.java.core.exception.user.ValidationException;



public class ViolationsStream {
    private final List<Violation> list = new ArrayList<>();



    public ViolationsStream add(String text) {
        return add(null, text);
    }

    public ViolationsStream add(String field, String text) {
        list.add(new Violation(field, text));
        return this;
    }

    public ViolationsStream addAll(ViolationsStream other) {
        list.addAll(other.list);
        return this;
    }

    public List<Violation> list() {
        return List.copyOf(list);
    }


    
    public void throwIfAny(String title) {
        if (!list.isEmpty()) throw new ValidationException(title, list);
    }

    public void throwIfAny() {
        if (!list.isEmpty()) throw new ValidationException(list);
    }
}