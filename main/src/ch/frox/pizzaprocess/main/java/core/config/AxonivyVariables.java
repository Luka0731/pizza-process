package ch.frox.pizzaprocess.main.java.core.config;

import ch.frox.pizzaprocess.main.java.core.exception.crash.ConfigurationException;
import ch.ivyteam.ivy.environment.Ivy;



public final class AxonivyVariables {

    private AxonivyVariables() {}



    public static String jpaName() { 
        return find("config.jpaName", String.class, new ConfigurationException("required ivy variable 'jpaName' is not set")); 
    }



    // |----- helper methods -----|

    private static <T> T find(String name, Class<T> type, T fallback) {
        String raw = Ivy.var().get(name);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return type.cast(convert(raw.trim(), type));
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException("ivy variable '" + name + "' = '" + raw + "' is not a valid " + type.getSimpleName(), ex);
        }
    }

    private static <T> T find(String name, Class<T> type, RuntimeException fallback) {
        String raw = Ivy.var().get(name);
        if (raw == null || raw.isBlank()) {
            throw fallback;
        }
        try {
            return type.cast(convert(raw.trim(), type));
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException("ivy variable '" + name + "' = '" + raw + "' is not a valid " + type.getSimpleName(), ex);
        }
    }

    private static Object convert(String raw, Class<?> type) {
        if (type == String.class) return raw;
        if (type == Integer.class || type == int.class) return Integer.valueOf(raw);
        if (type == Double.class || type == double.class) return Double.valueOf(raw);
        if (type == Boolean.class || type == boolean.class) {
            if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) return Boolean.valueOf(raw);
            throw new IllegalArgumentException("expected 'true' or 'false'");
        }
        throw new UnsupportedOperationException("AxonivyVariables cannot convert to " + type.getName());
    }
}