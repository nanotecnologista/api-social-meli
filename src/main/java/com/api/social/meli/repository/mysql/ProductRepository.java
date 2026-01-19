package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndActiveTrue(Long id);

    List<Product> findBySellerIdAndActiveTrue(Long sellerId);

    Page<Product> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);
}
