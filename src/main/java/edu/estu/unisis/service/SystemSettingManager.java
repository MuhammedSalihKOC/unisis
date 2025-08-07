package edu.estu.unisis.service;

import edu.estu.unisis.model.SystemSetting;
import edu.estu.unisis.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemSettingManager implements SystemSettingService {

    @Autowired
    private SystemSettingRepository systemSettingRepository;

    @Override
    public boolean isEnabled(String key) {
        Optional<SystemSetting> result = systemSettingRepository.findBySettingKey(key);
        System.out.println(result.get());
        System.out.println("DEBUG: result = " + result);
        return result.map(setting -> "true".equalsIgnoreCase(setting.getSettingValue().trim()))
                .orElse(false);
    }

}

