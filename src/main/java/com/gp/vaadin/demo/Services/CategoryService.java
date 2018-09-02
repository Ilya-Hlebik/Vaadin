package com.gp.vaadin.demo.Services;

import com.gp.vaadin.demo.Entity.Category;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

import static com.gp.vaadin.demo.Services.StaticFields.NO_CATEGORY;

public class CategoryService {

    private static CategoryService instance;

    private EntityManager em = Persistence.createEntityManagerFactory("demo_hotels").createEntityManager();

    private CategoryService() {
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public void save(Category category) {
        em.getTransaction().begin();
        if (category.getId() == null) {
            TypedQuery<Category> query = em.createNamedQuery("Category.findByName", Category.class);
            query.setParameter("name", category.getName());
            if (!query.getResultList().isEmpty()) {
                em.getTransaction().rollback();
                return;
            }
            em.persist(category);
        } else {
            em.merge(category);
        }
        em.getTransaction().commit();
    }

    public String getCategoryName(Category category) {
        if (category == null) return NO_CATEGORY;

        Category cat = get(category.getId());
        return cat == null ? NO_CATEGORY : cat.getName();
    }

    public Category get(long id) {
        return em.find(Category.class, id);
    }

    public List<Category> getAll() {
        TypedQuery<Category> namedQuery = em.createNamedQuery("Category.getAll", Category.class);
        return namedQuery.getResultList();
    }

    public void delete(Iterable<Category> categories) {
        em.getTransaction().begin();
        categories.forEach(category -> em.remove(get(category.getId())));
        em.getTransaction().commit();
    }
}