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
     * Get all products with pagination and sorting
     */
    public List<Product> getProducts(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery("FROM Product ORDER BY id DESC", Product.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Product> products = query.getResultList();
        System.out.println("ProductDAO.getProducts() returned: " + products.size() + " products.");
        return products;
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
     * Delete product by ID
     */
    public void deleteById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Product product = session.get(Product.class, id);
        if (product != null) {
            session.delete(product);
        }
    }

    /**
     * Advanced search with multiple filters
     */
    public List<Product> searchProducts(String keyword, Double minPrice, Double maxPrice,
                                        Integer minStock, String sortBy, String sortDirection,
                                        int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("FROM Product WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" AND (productName LIKE :keyword OR description LIKE :keyword)");
        }
        if (minPrice != null) {
            hql.append(" AND price >= :minPrice");
        }
        if (maxPrice != null) {
            hql.append(" AND price <= :maxPrice");
        }
        if (minStock != null) {
            hql.append(" AND stock >= :minStock");
        }

        hql.append(" ORDER BY ").append(sortBy).append(" ").append(sortDirection);

        Query<Product> query = session.createQuery(hql.toString(), Product.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        if (minStock != null) {
            query.setParameter("minStock", minStock);
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Search products by name with pagination
     */
    public List<Product> searchProducts(String keyword, int page, int size) {
        return searchProducts(keyword, null, null, null, "id", "DESC", page, size);
    }

    /**
     * Get count of search results with filters
     */
    public long getSearchResultsCount(String keyword, Double minPrice, Double maxPrice, Integer minStock) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            hql.append(" AND (productName LIKE :keyword OR description LIKE :keyword)");
        }
        if (minPrice != null) {
            hql.append(" AND price >= :minPrice");
        }
        if (maxPrice != null) {
            hql.append(" AND price <= :maxPrice");
        }
        if (minStock != null) {
            hql.append(" AND stock >= :minStock");
        }

        Query<Long> query = session.createQuery(hql.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        if (minStock != null) {
            query.setParameter("minStock", minStock);
        }

        return query.getSingleResult();
    }

    /**
     * Get count of search results
     */
    public long getSearchResultsCount(String keyword) {
        return getSearchResultsCount(keyword, null, null, null);
    }

    /**
     * Get products with low stock
     */
    public List<Product> getLowStockProducts(int threshold) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery(
                "FROM Product WHERE stock <= :threshold ORDER BY stock ASC", Product.class);
        query.setParameter("threshold", threshold);
        return query.getResultList();
    }

    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery(
                "FROM Product WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY price ASC", Product.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }

    /**
     * Update product stock
     */
    public void updateStock(Long productId, Integer newStock) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("UPDATE Product SET stock = :stock WHERE id = :id");
        query.setParameter("stock", newStock);
        query.setParameter("id", productId);
        query.executeUpdate();
    }

    /**
     * Get product statistics
     */
    public Object[] getProductStatistics() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "SELECT COUNT(p), AVG(p.price), SUM(p.stock), MAX(p.price), MIN(p.price) FROM Product p");
        return (Object[]) query.getSingleResult();
    }
}