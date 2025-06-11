package bk.service;

import bk.dao.CustomerDAO;
import bk.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional // Ensure transactional for methods that interact with DAO to modify data
public class CustomerService {

    @Autowired
    private CustomerDAO customerDAO;

    public void registerCustomer(Customer customer) throws Exception {
        // Kiểm tra username đã tồn tại
        if (customerDAO.existsByUsername(customer.getUsername())) {
            throw new Exception("Tên đăng nhập đã tồn tại");
        }
        // Kiểm tra email đã tồn tại
        if (customerDAO.existsByEmail(customer.getEmail())) {
            throw new Exception("Email đã được sử dụng");
        }
        // Mã hóa mật khẩu (trong thực tế nên sử dụng BCrypt)
        // customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        // Lưu customer
        customerDAO.save(customer);
    }

    public boolean existsByUsername(String username) {
        return customerDAO.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return customerDAO.existsByEmail(email);
    }

    public Customer authenticateUser(String username, String password) {
        return customerDAO.findByUsernameAndPassword(username, password);
    }

    // New methods for user management

    /**
     * Get customers with pagination
     */
    public List<Customer> getCustomers(int page, int size) {
        return customerDAO.getCustomers(page, size);
    }

    /**
     * Get total number of customers
     */
    public long getTotalCustomers() {
        return customerDAO.getTotalCustomers();
    }

    /**
     * Calculate total pages for customers
     */
    public int getTotalPages(int size) {
        long totalCustomers = getTotalCustomers();
        return (int) Math.ceil((double) totalCustomers / size);
    }

    /**
     * Get customer by ID
     */
    public Customer getCustomerById(Long id) {
        return customerDAO.getCustomerById(id);
    }

    /**
     * Update customer (for status change)
     */
    public void updateCustomer(Customer customer) {
        customerDAO.update(customer);
    }

    /**
     * Search customers with pagination
     */
    public List<Customer> searchCustomers(String keyword, int page, int size) {
        return customerDAO.searchCustomers(keyword, page, size);
    }

    /**
     * Get search results count for customers
     */
    public long getSearchResultsCount(String keyword) {
        return customerDAO.getSearchResultsCount(keyword);
    }

    /**
     * Calculate total pages for search results for customers
     */
    public int getSearchTotalPages(String keyword, int size) {
        long totalResults = getSearchResultsCount(keyword);
        return (int) Math.ceil((double) totalResults / size);
    }
}