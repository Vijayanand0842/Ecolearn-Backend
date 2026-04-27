package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByModuleIdOrderByPageNumberAsc(Long moduleId);
}
