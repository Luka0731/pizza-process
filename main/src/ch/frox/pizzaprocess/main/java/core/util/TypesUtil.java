package ch.frox.pizzaprocess.main.java.core.util;

import java.lang.reflect.ParameterizedType;

import ch.frox.pizzaprocess.main.java.core.exception.ConfigurationException;



public final class TypesUtil {

    private TypesUtil() {}



    /**
     * returns the class of a generic type argument of the subclass
     * 
     * @param subclass 
     * @param parentClass 
     * @param index 0 is the first type argument, 1 the second, and so on
    **/
    public static Class<?> getGenericType(Class<?> childClass, Class<?> parentClass, int index) {    
        if (!(childClass.getGenericSuperclass() instanceof ParameterizedType parent) || parent.getRawType() != parentClass) {
            throw new ConfigurationException(childClass.getSimpleName() + " must directly extend " + parentClass.getSimpleName() + " with concrete types, e.g. 'PizzaRepository extends GenericRepository<Pizza, UUID>'");
        }
        var typeArguments = parent.getActualTypeArguments();
        if (index < 0 || index >= typeArguments.length) {
            throw new ConfigurationException("index " + index + " is out of bounds, " + parentClass.getSimpleName() + " has " + typeArguments.length + " type arguments");
        }
        if (!(typeArguments[index] instanceof Class<?> type)) {
            throw new ConfigurationException("type argument " + index + " of " + childClass.getSimpleName() + " is not a concrete class");
        }
        return type;
    }

    public static <T> Class<T> getGenericTypeCasted(Class<?> subclass, Class<?> parentClass, int index) {
        return (Class<T>) getGenericType(subclass, parentClass, index);
    }
}