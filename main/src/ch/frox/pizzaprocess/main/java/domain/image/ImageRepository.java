package ch.frox.pizzaprocess.main.java.domain.image;

import ch.frox.pizzaprocess.main.java.core.generic.GenericRepository;



public class ImageRepository extends GenericRepository<Image, String> {

    public long countUsages(String imageId) {
        return entityManager
            .createQuery("SELECT COUNT(p) FROM Pizza p WHERE p.image.id = :imageId")
            .setParameter("imageId", imageId)
            .getSingleResult();
    }
}