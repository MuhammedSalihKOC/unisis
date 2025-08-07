package edu.estu.unisis.repository;

import edu.estu.unisis.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    @Query("SELECT s FROM SystemSetting s WHERE s.settingKey = :key")
    Optional<SystemSetting> findBySettingKey(@Param("key") String key);
}

