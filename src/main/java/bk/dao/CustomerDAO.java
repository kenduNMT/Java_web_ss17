package bk.dao;

import bk.entity.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;

@Repository
@Transactional // Add @Transactional for methods that modify data or need a current session
public class CustomerDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.save(customer);
    }

    // Add this method to update a customer
    public void update(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.update(customer);
    }

    public Customer findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery(
                "FROM Customer WHERE username = :username", Customer.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public Customer findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery(
                "FROM Customer WHERE email = :email", Customer.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    public Customer findByUsernameAndPassword(String username, String password) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery(
                "FROM Customer WHERE username = :username AND password = :password AND status = :status",
                Customer.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        query.setParameter("status", Customer.Status.ACTIVE);
        return query.uniqueResult();
    }

    // New methods for user management

    /**
     * Get all customers with pagination
     */
    public List<Customer> getCustomers(int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery("FROM Customer ORDER BY id DESC", Customer.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Get total count of customers
     */
    public long getTotalCustomers() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(c) FROM Customer c", Long.class);
        return query.getSingleResult();
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Customer.class, id);
    }

    /**
     * Search customers by username or email with pagination
     */
    public List<Customer> searchCustomers(String keyword, int page, int size) {
        Session session = sessionFactory.getCurrentSession();
        Query<Customer> query = session.createQuery(
                "FROM Customer WHERE username LIKE :keyword OR email LIKE :keyword ORDER BY id DESC",
                Customer.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /**
     * Get count of search results for customers
     */
    public long getSearchResultsCount(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Customer c WHERE username LIKE :keyword OR email LIKE :keyword",
                Long.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getSingleResult();
    }
}