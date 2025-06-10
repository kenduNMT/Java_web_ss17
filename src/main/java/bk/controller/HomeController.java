package bk.controller;

import bk.entity.Customer;
import bk.entity.Product;
import bk.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/home")
    public String showHomePage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            Model model,
            HttpSession session) {

        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/auth/login";
        }

        List<Product> products;
        int totalPages;
        long totalProducts;

        // Handle search functionality
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search.trim(), page, size);
            totalPages = productService.getSearchTotalPages(search.trim(), size);
            totalProducts = productService.getSearchResultsCount(search.trim());
            model.addAttribute("search", search);
        } else {
            products = productService.getProducts(page, size);
            totalPages = productService.getTotalPages(size);
            totalProducts = productService.getTotalProducts();
        }

        // Add attributes to model
        model.addAttribute("user", loggedInUser);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("size", size);

        // Add pagination helper attributes
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("previousPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "home";
    }

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }
}