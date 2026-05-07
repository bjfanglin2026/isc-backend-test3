package com.isc.controller;

import com.isc.config.IscProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Autowired
    private IscProperties iscProperties;

    @GetMapping("/isc")
    public Map<String, Object> getIscConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("host", iscProperties.getHost());
        config.put("port", iscProperties.getPort());
        config.put("appKey", iscProperties.getAppKey());
        // For video plugin initialization, appSecret is needed
        // In production, consider using a backend proxy for all ISC API calls
        config.put("appSecret", iscProperties.getAppSecret());
        return config;
    }
}
