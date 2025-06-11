package bk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 255, message = "Tên sản phẩm phải từ 2-255 ký tự")
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0")
    @DecimalMax(value = "999999999.99", message = "Giá sản phẩm không được vượt quá 999,999,999.99")
    private Double price;

    @Column(name = "stock", nullable = false)
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Max(value = 999999, message = "Số lượng tồn kho không được vượt quá 999,999")
    private Integer stock;

    @Column(name = "image")
    @Size(max = 500, message = "URL hình ảnh không được vượt quá 500 ký tự")
    private String image;

    @Column(name = "category")
    @Size(max = 100, message = "Danh mục không được vượt quá 100 ký tự")
    private String category;

    @Column(name = "brand")
    @Size(max = 100, message = "Thương hiệu không được vượt quá 100 ký tự")
    private String brand;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Custom constructor for basic fields
    public Product(String productName, String description, Double price, Integer stock, String image) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.isActive = true;
    }

    // Helper methods
    public boolean isInStock() {
        return this.stock != null && this.stock > 0;
    }

    public boolean isLowStock(int threshold) {
        return this.stock != null && this.stock <= threshold;
    }

    public String getFormattedPrice() {
        if (price == null) return "0 VNĐ";
        return String.format("%,.0f VNĐ", price);
    }

    public String getStockStatus() {
        if (stock == null || stock == 0) {
            return "Hết hàng";
        } else if (stock <= 10) {
            return "Sắp hết";
        } else {
            return "Còn hàng";
        }
    }

    public String getStockStatusClass() {
        if (stock == null || stock == 0) {
            return "text-danger";
        } else if (stock <= 10) {
            return "text-warning";
        } else {
            return "text-success";
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}