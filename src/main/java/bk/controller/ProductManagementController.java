package bk.controller;

import bk.entity.Customer;
import bk.entity.Product;
import bk.service.ProductService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
public class ProductManagementController {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ProductService productService;

    private String uploadImageToCloud(MultipartFile file) throws IOException {
        try {
            // Sử dụng phương thức upload để gửi file lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            // Lấy ra đường dẫn truy cập
            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isEmpty()) {
                throw new IOException("Secure URL not found in Cloudinary upload response");
            }
            return secureUrl;
        } catch (IOException e) {
            // Log lỗi hoặc xử lý ngoại lệ theo ý của bạn
            e.printStackTrace();
        }
        return null; // Trả về null hoặc xử lý lỗi theo ý bạn

    }

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        // Admin access check (already implemented in AdminController, but good to re-check)
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login"; // Or redirect to an unauthorized page
        }

        List<Product> products;
        long totalProducts;
        int totalPages;

        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productService.searchProducts(keyword.trim(), page - 1, size);
            totalProducts = productService.getSearchResultsCount(keyword.trim());
            totalPages = productService.getSearchTotalPages(keyword.trim(), size);
        } else {
            products = productService.getProducts(page - 1, size);
            totalProducts = productService.getTotalProducts();
            totalPages = productService.getTotalPages(size);
        }

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword); // Keep the search keyword in the model
        return "admin/product-list"; // Create this Thymeleaf template
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login";
        }
        model.addAttribute("product", new Product());
        return "admin/product-form"; // Create this Thymeleaf template for create/edit
    }

    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult bindingResult,
            @RequestParam("productImageFile") MultipartFile productImageFile,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        try {
            if (!productImageFile.isEmpty()) {
                String imageUrl = uploadImageToCloud(productImageFile);
                product.setImage(imageUrl);
            } else if (product.getId() != null && product.getImage() == null) {
                // If editing and no new image is uploaded, retain the existing image
                Product existingProduct = productService.getProductById(product.getId());
                if (existingProduct != null) {
                    product.setImage(existingProduct.getImage());
                }
            }
            productService.saveOrUpdate(product);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được lưu thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải ảnh lên: " + e.getMessage());
            return "redirect:/admin/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu sản phẩm: " + e.getMessage());
            return "redirect:/admin/products";
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login";
        }
        Product product = productService.getProductById(id);
        if (product == null) {
            // Handle product not found, e.g., redirect to product list with an error
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        return "admin/product-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getRole() != Customer.Role.ADMIN) {
            return "redirect:/auth/login";
        }
        Product product = productService.getProductById(id);
        if (product != null) {
            productService.delete(product);
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã được xóa thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm để xóa.");
        }
        return "redirect:/admin/products";
    }
}