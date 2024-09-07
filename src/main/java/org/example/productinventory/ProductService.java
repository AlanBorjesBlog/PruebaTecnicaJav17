package org.example.productinventory;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@Stateless
public class ProductService {
    @PersistenceContext
    private EntityManager em;

    public List<Product> findAll(int page, int size) {
        return em.createQuery("SELECT p FROM Product p", Product.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Product> findByNameOrCategory(String name, String category) {
        return em.createQuery("SELECT p FROM Product p WHERE p.name LIKE :name OR p.category LIKE :category", Product.class)
                .setParameter("name", "%" + name + "%")
                .setParameter("category", "%" + category + "%")
                .getResultList();
    }

    @Transactional
    public void create(Product product) {
        em.persist(product);
    }

    @Transactional
    public void update(Product product) {
        em.merge(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    @Transactional
    public void updateStock(Long id, int stock) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            product.setStock(stock);
            em.merge(product);
        }
    }
}