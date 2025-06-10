package bk.dao;

import bk.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProductDAO {

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Get all products with pagination
     */
    public List<Product> getProducts(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery("FROM Product ORDER BY id DESC", Product.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Get total count of products
     */
    public long getTotalProducts() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
        return query.getSingleResult();
    }

    /**
     * Get product by ID
     */
    public Product getProductById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Product.class, id);
    }

    /**
     * Save or update product
     */
    public void saveOrUpdate(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(product);
    }

    /**
     * Delete product
     */
    public void delete(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(product);
    }

    /**
     * Search products by name with pagination
     */
    public List<Product> searchProducts(String keyword, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery(
                "FROM Product WHERE productName LIKE :keyword ORDER BY id DESC",
                Product.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Get count of search results
     */
    public long getSearchResultsCount(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE productName LIKE :keyword",
                Long.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getSingleResult();
    }
}