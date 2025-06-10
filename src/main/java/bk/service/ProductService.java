package bk.service;

import bk.dao.ProductDAO;
import bk.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;

    /**
     * Get products with pagination
     */
    public List<Product> getProducts(int page, int size) {
        return productDAO.getProducts(page, size);
    }

    /**
     * Get total number of products
     */
    public long getTotalProducts() {
        return productDAO.getTotalProducts();
    }

    /**
     * Calculate total pages
     */
    public int getTotalPages(int size) {
        long totalProducts = getTotalProducts();
        return (int) Math.ceil((double) totalProducts / size);
    }

    /**
     * Get product by ID
     */
    public Product getProductById(Long id) {
        return productDAO.getProductById(id);
    }

    /**
     * Save or update product
     */
    public void saveOrUpdate(Product product) {
        productDAO.saveOrUpdate(product);
    }

    /**
     * Delete product
     */
    public void delete(Product product) {
        productDAO.delete(product);
    }

    /**
     * Search products with pagination
     */
    public List<Product> searchProducts(String keyword, int page, int size) {
        return productDAO.searchProducts(keyword, page, size);
    }

    /**
     * Get search results count
     */
    public long getSearchResultsCount(String keyword) {
        return productDAO.getSearchResultsCount(keyword);
    }

    /**
     * Calculate total pages for search results
     */
    public int getSearchTotalPages(String keyword, int size) {
        long totalResults = getSearchResultsCount(keyword);
        return (int) Math.ceil((double) totalResults / size);
    }

    /**
     * Check if product is in stock
     */
    public boolean isInStock(Product product) {
        return product != null && product.getStock() > 0;
    }

    /**
     * Format price to Vietnamese currency
     */
    public String formatPrice(Double price) {
        if (price == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", price);
    }
}