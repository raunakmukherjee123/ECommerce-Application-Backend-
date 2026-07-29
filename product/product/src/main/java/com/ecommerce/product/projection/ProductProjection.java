package com.ecommerce.product.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ProductProjection {
    public String getName();
    public String getDescription();
    public BigDecimal getPrice();
    public String getCategory();
    public String getImageUrl();
    public Integer getStockQuantity();
    public LocalDateTime getCreatedAt();
    public LocalDateTime getUpdatedAt();
}
