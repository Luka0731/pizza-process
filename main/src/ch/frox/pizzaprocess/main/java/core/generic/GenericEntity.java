package ch.frox.pizzaprocess.main.java.core.generic;

import java.util.UUID;

import ch.frox.pizzaprocess.main.java.core.exception.MissingIdException;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public abstract class GenericEntity<ID> {
    @Id
    protected ID id;


    
    // |----- setup methods -----|

    @PrePersist
    private final void managePersistInitialization() {
        Class<ID> idType = TypesUtil.getGenericTypeCasted(this.getClass(), GenericEntity.class, 0);
        if (idType == UUID.class) id = idType.cast(UUID.randomUUID());

        init();

        if (id == null) throw new MissingIdException(getClassName());
    }

    protected void init() {}



    // |----- logging methods -----|

    /** 
     * Returns the name of the entity instance. this gets used for logging and error messages.
    **/
    public final String getClassName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    /** 
     * Returns the name of the entity instance. this gets used for important logging and error messages. 
    **/
    public final String getEntityName() {
        return "'" + this.getClass().getSimpleName().toLowerCase() +  "__id:" + id + "'";
    }

    /** 
     * Returns the name of the entity. this gets used for pretty logging.
     * Subclasses can gladly overwright this method. not like the getEntityName() method, this one gets used for pretty and readable formatting in logging.
    **/
    public String getName() {
        return getEntityName();
    }
}