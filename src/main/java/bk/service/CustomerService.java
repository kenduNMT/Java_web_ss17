package bk.service;

import bk.dao.CustomerDAO;
import bk.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
}