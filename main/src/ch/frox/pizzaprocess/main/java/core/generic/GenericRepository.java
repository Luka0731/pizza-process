package ch.frox.pizzaprocess.main.java.core.generic;

import java.util.List;

import ch.frox.pizzaprocess.main.java.core.config.Registry;
import ch.frox.pizzaprocess.main.java.core.util.TypesUtil;
import ch.ivyteam.ivy.process.data.persistence.IIvyEntityManager;



public class GenericRepository<E extends GenericEntity<ID>, ID> {
    protected final IIvyEntityManager entityManager;
    protected final Class<E> entityClass;

    public GenericRepository() {
        entityManager = Registry.get(IIvyEntityManager.class);
        entityClass = TypesUtil.getGenericTypeCasted(this.getClass(), GenericRepository.class, 0);
    }
    

    
    public List<E> findAll() {
        return entityManager.findAll(entityClass);
    }

    public E findById(ID id) {
        return entityManager.find(entityClass, id);
    }

    public E save(E entity) {
        return entityManager.persist(entity);
    }

    public E update(E entity) {
        return entityManager.merge(entity);
    }

    public void deleteById(ID id) {
        E entity = findById(id);
        entityManager.remove(entity); 
    }

    public boolean existsById(ID id) {
        return findById(id) != null;
    }
}