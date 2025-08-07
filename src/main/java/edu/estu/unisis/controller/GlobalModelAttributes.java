package edu.estu.unisis.controller;

import edu.estu.unisis.model.SystemSetting;
import edu.estu.unisis.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private SystemSettingService settingService;

    @ModelAttribute
    public void injectSettings(Model model) {
        model.addAttribute("registrationOpen", settingService.isEnabled("registration_open"));
    }
}

