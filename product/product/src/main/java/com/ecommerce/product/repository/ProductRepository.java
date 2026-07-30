package com.ecommerce.product.repository;


import com.ecommerce.product.model.Product;
import com.ecommerce.product.projection.ProductProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByIsActiveTrue();

    @Query(value = "SELECT p from Product p WHERE p.isActive=true AND p.stockQuantity>0 AND LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

   Optional<Product> findByIdAndIsActiveTrue(Long id);

   @Query(value = """
           select p.name as name,
           p.description as description,
           p.price as price,
           p.category as category,
           p.imageUrl as imageUrl,
           p.stockQuantity as stockQuantity,
           p.isActive as isActive
           from Product p where p.id = :id
           """)
    ProductProjection findProductById(Long id);
}
