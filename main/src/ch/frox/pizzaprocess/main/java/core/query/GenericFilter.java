package ch.frox.pizzaprocess.main.java.core.query;

import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import jakarta.validation.constraints.NotNull;



public abstract class GenericFilter<S extends Enum<S> & GenericSort> {
    @NotNull(message = "Please choose how the list should be sorted!")
    private S sort;
    private final Class<S> sortType;

    protected GenericFilter() {
        sortType = TypesUtil.getGenericTypeCasted(getClass(), GenericFilter.class, 0);;
        sort = sortType.getEnumConstants()[0]; ;
    }



    // |----- getters & setters -----|

    public S[] getPossibleSorts() {
        return sortType.getEnumConstants();
    }

    public S getSort() {
        return sort;
    }

    public String getSortName() {
        return (sort == null)? null : sort.name();
    }

    public void setSortName(String sortName) {
        sort = (sortName == null || sortName.isBlank())? null : Enum.valueOf(sortType, sortName);
    }
}