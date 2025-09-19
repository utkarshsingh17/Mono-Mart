package com.sb_ecom.backend_ecom.repo;

import com.sb_ecom.backend_ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);
}
