package bk.controller;

import bk.entity.Customer;
import bk.service.ProductCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductCartService productCartService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");

        // Check if user is logged in
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng!");
            // Redirect to login page, remember the original product detail page
            session.setAttribute("redirectAfterLogin", "/product/detail/" + productId);
            return "redirect:/auth/login";
        }

        try {
            productCartService.addOrUpdateProductInCart(loggedInUser.getId(), productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }

        return "redirect:/product/detail/" + productId;
    }
}