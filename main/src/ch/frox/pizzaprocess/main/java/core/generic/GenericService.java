package ch.frox.pizzaprocess.main.java.core.generic;

import java.util.List;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.exception.EntityNotFoundException;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import ch.frox.pizzaprocess.main.java.core.util.ValidationUtil;



public class GenericService<E extends GenericEntity<ID>, ID, R extends GenericRepository<E, ID>> {
    protected final R repository;
    protected final Class<E> entityClass;

    public GenericService() {
        repository = Registry.get(TypesUtil.getGenericTypeCasted(this.getClass(), GenericService.class, 2));
        entityClass = TypesUtil.getGenericTypeCasted(this.getClass(), GenericService.class, 0);
    }



    public List<E> getAll() {
        return repository.findAll();
    }

    public E getById(ID id) {
        E entity = repository.findById(id);
        if (entity == null) throw new EntityNotFoundException(entityClass, id);
        return repository.findById(id);
    }

    public E save(E entity) {
        ValidationUtil.validateElseThrow(entity);
        return repository.save(entity);
    }

    public E update(E entity) {
        ID id = entity.getId();
        if (!repository.existsById(id)) throw new EntityNotFoundException(entityClass, id);
        ValidationUtil.validateElseThrow(entity);
        return repository.update(entity);
    }

    public void deleteById(ID id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException(entityClass, id);
        repository.deleteById(id);
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}