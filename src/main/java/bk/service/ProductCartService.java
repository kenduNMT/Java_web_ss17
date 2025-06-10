package bk.service;

import bk.dao.ProductCartDAO;
import bk.entity.ProductCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCartService {

    @Autowired
    private ProductCartDAO productCartDAO;

    @Transactional
    public void addOrUpdateProductInCart(Long customerId, Long productId, Integer quantity) {
        ProductCart existingCartItem = productCartDAO.findByCustomerAndProduct(customerId, productId);

        if (existingCartItem != null) {
            // Update quantity if product already exists in cart
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            productCartDAO.saveOrUpdate(existingCartItem);
        } else {
            // Add new product to cart
            ProductCart newCartItem = new ProductCart(customerId, productId, quantity);
            productCartDAO.saveOrUpdate(newCartItem);
        }
    }
}