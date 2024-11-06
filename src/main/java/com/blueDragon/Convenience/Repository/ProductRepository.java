package com.blueDragon.Convenience.Repository;

import com.blueDragon.Convenience.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.name = :name AND p.price = :price")
    boolean existsByNameAndPrice(@Param("name") String name, @Param("price") String price);

    @Query("SELECT p FROM Product p WHERE CAST(REPLACE(p.price, ',', '') AS integer) <= :maxPrice ORDER BY CAST(REPLACE(p.price, ',', '') AS integer) DESC")
    List<Product> findProductsByMaxPrice(@Param("maxPrice") int maxPrice);



}
