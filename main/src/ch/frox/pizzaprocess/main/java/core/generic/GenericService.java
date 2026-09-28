package ch.frox.pizzaprocess.main.java.core.generic;

import java.util.List;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.system.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.security.AuthorizationService;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import ch.frox.pizzaprocess.main.java.core.validation.Validater;



public class GenericService<E extends GenericEntity<ID>, ID, R extends GenericRepository<E, ID>> {
    protected final R repository;
    protected final Class<E> entityClass;
    protected final AuthorizationService authorizationService;

    public GenericService() {
        repository = Registry.get(TypesUtil.getGenericTypeCasted(this.getClass(), GenericService.class, 2));
        entityClass = TypesUtil.getGenericTypeCasted(this.getClass(), GenericService.class, 0);
        authorizationService = Registry.get(AuthorizationService.class);
    }



    public List<E> getAll() {
        return repository.findAll();
    }

    public E getById(ID id) {
        E entity = findById(id);
        if (entity == null) throw new EntityNotFoundException(entityClass, id);
        return entity;
    }

    public E findById(ID id) {
        return repository.findById(id);
    }

    public E save(E entity) {
        Validater.of(entity).throwIfAny();
        return repository.save(entity);
    }

    public E update(E entity) {
        reload(entity);
        Validater.of(entity).throwIfAny();
        return repository.update(entity);
    }

    public void delete(E entity) {
        repository.delete(reload(entity));
    }



    // |----- helper methods -----|

    protected E reload(E entity) {
        return getById(entity.getId());
    }
}