package com.ecommerce.product.projection;

import java.math.BigDecimal;

public interface ProductProjection {
    public String getName();
    public String getDescription();
    public BigDecimal getPrice();
    public String getCategory();
    public String getImageUrl();
    public Integer getStockQuantity();
}
