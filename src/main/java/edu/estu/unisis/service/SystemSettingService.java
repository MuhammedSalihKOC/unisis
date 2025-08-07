package edu.estu.unisis.service;

import edu.estu.unisis.model.SystemSetting;

import java.util.List;

public interface SystemSettingService {
    boolean isEnabled(String key);
    List<SystemSetting> getAllSettings();
    void updateValue(String key, String newValue);

}

