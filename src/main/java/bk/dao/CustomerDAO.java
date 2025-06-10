package bk.dao;

import bk.entity.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Customer customer) {
        Session session = sessionFactory.getCurrentSession();
        session.save(customer);
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
}