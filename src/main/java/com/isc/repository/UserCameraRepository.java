package com.isc.repository;

import com.isc.entity.UserCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserCameraRepository extends JpaRepository<UserCamera, Long> {
    List<UserCamera> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    void deleteByUserIdAndCameraIndexCode(Long userId, String cameraIndexCode);
    boolean existsByUserIdAndCameraIndexCode(Long userId, String cameraIndexCode);
}
