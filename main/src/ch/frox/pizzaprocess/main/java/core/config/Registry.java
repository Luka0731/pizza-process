package ch.frox.pizzaprocess.main.java.core.config;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ch.frox.pizzaprocess.main.java.core.exception.CircularDependencyException;
import ch.frox.pizzaprocess.main.java.core.exception.NoArgConstructorRequiredException;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.data.persistence.IIvyEntityManager;



public final class Registry {
    private static final Map<Class<?>, Object> entries = new HashMap<>();
    private static final Object CREATION_LOCK = new Object();
    private static final Set<Class<?>> inCreation = new LinkedHashSet<>();

    private Registry() {}

    // |----- eger registry -----|
    static {
        Registry.register(IIvyEntityManager.class, Ivy.persistence().get(AxonivyVariables.jpaName()));
    }



    public static <T> T get(Class<T> type) {
        Object existing = entries.get(type);
        if (existing != null) return type.cast(existing);
        synchronized (CREATION_LOCK) { 
            existing = entries.get(type);
            if (existing != null) return type.cast(existing);
            if (inCreation.contains(type)) throw new CircularDependencyException(type, inCreation);
            inCreation.add(type);
            try {
                T created = instantiate(type);
                entries.put(type, created);
                return created;
            } finally {
                inCreation.remove(type);
            }
        }
    }

    /** 
     * This method is for speical situations, like if the instantiation of a class requires more effort or the constructor has parameters.
     * Its trade of is that it can only be loaded eagerly.
    **/
    private static <T> void register(Class<T> type, T service) {
        Objects.requireNonNull(service, "service for " + type.getSimpleName() + " is null");
        if (!entries.containsKey(type)) entries.put(type, service);
    }



    // |----- helper methods -----|

    private static <T> T instantiate(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException cause) throw cause;
            throw new IllegalStateException("creating " + type.getSimpleName() + " failed", ex.getCause());
        } catch (ReflectiveOperationException ex) {
            throw new NoArgConstructorRequiredException(type, ex);
        }
    }
}