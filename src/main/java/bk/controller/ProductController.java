package bk.controller;

import bk.entity.Customer;
import bk.entity.Product;
import bk.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        Product product = productService.getProductById(id);

        if (product == null) {
            // Product not found, redirect to home with error message
            session.setAttribute("errorMessage", "Sản phẩm không tồn tại!");
            return "redirect:/home";
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("product", product);
        model.addAttribute("isInStock", productService.isInStock(product));
        model.addAttribute("formattedPrice", productService.formatPrice(product.getPrice()));

        return "product-detail";
    }
}