package bk.controller;

import bk.entity.Customer;
import bk.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        // Admin access check
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login"; // Or redirect to an unauthorized page
        }

        List<Customer> users;
        long totalUsers;
        int totalPages;

        if (keyword != null && !keyword.trim().isEmpty()) {
            users = customerService.searchCustomers(keyword.trim(), page - 1, size);
            totalUsers = customerService.getSearchResultsCount(keyword.trim());
            totalPages = customerService.getSearchTotalPages(keyword.trim(), size);
        } else {
            users = customerService.getCustomers(page - 1, size);
            totalUsers = customerService.getTotalCustomers();
            totalPages = customerService.getTotalPages(size);
        }

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        return "admin/user-list"; // Create this Thymeleaf template
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login";
        }

        Customer customer = customerService.getCustomerById(id);
        if (customer != null) {
            if (customer.getStatus() == Customer.Status.ACTIVE) {
                customer.setStatus(Customer.Status.INACTIVE);
                redirectAttributes.addFlashAttribute("message", "Người dùng " + customer.getUsername() + " đã bị khóa.");
            } else {
                customer.setStatus(Customer.Status.ACTIVE);
                redirectAttributes.addFlashAttribute("message", "Người dùng " + customer.getUsername() + " đã được mở khóa.");
            }
            customerService.updateCustomer(customer);
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng.");
        }
        return "redirect:/admin/users";
    }
}