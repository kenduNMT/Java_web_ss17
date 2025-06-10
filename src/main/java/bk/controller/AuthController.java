package bk.controller;

import bk.dto.LoginDTO;
import bk.entity.Customer;
import bk.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid Customer customer,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            customerService.registerCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đăng ký thành công! Chào mừng " + customer.getUsername());
            return "redirect:/auth/success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "register-success";
    }

    // Login methods
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@Valid LoginDTO loginDTO,
                               BindingResult bindingResult,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            // Xác thực người dùng
            Customer customer = customerService.authenticateUser(
                    loginDTO.getUsername(),
                    loginDTO.getPassword()
            );

            if (customer == null) {
                model.addAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng");
                return "login";
            }

            // Lưu thông tin user vào session
            session.setAttribute("loggedInUser", customer);
            session.setAttribute("username", customer.getUsername());
            session.setAttribute("role", customer.getRole().toString());

            // Phân quyền theo role
            if (customer.getRole() == Customer.Role.ADMIN) {
                redirectAttributes.addFlashAttribute("welcomeMessage",
                        "Chào mừng Admin " + customer.getUsername());
                return "redirect:/admin/dashboard";
            } else {
                redirectAttributes.addFlashAttribute("welcomeMessage",
                        "Chào mừng " + customer.getUsername());
                return "redirect:/home";
            }

        } catch (Exception e) {
            model.addAttribute("loginError", "Đã xảy ra lỗi trong quá trình đăng nhập");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "Bạn đã đăng xuất thành công");
        return "redirect:/auth/login";
    }
}