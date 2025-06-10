package bk.dao;

import bk.entity.ProductCart;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCartDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public ProductCart findByCustomerAndProduct(Long customerId, Long productId) {
        Session session = sessionFactory.getCurrentSession();
        Query<ProductCart> query = session.createQuery(
                "FROM ProductCart WHERE customerId = :customerId AND productId = :productId", ProductCart.class);
        query.setParameter("customerId", customerId);
        query.setParameter("productId", productId);
        return query.uniqueResult(); // Returns null if no unique result found
    }

    public void saveOrUpdate(ProductCart productCart) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(productCart);
    }
}