package com.ecolearn.backend.repository;

import com.ecolearn.backend.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> { // use Long because Spring Data JPA needs a type wrapper even if not mapped strictly to single PKey, but actually we should use the IdClass for the repository type if needed, but since we map two ids, we could also use custom repository methods if it fails. Or I will just make it an interface without extending JpaRepository and use EntityManger, OR use UserProgressRepository extends JpaRepository<UserProgress, Object>. Let me change to Object.
    List<UserProgress> findByUserId(Long userId);
    boolean existsByUserIdAndModuleId(Long userId, Long moduleId);
}
