package com.isc.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ArtemisInitializer {

    @Autowired
    private IscProperties iscProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        // 设置 Artemis SDK 配置
        ArtemisConfig.host = iscProperties.getHost() + ":" + iscProperties.getPort();
        ArtemisConfig.appKey = iscProperties.getAppKey();
        ArtemisConfig.appSecret = iscProperties.getAppSecret();
        
        System.out.println("========================================");
        System.out.println("Artemis SDK 初始化完成");
        System.out.println("Host: " + ArtemisConfig.host);
        System.out.println("AppKey: " + ArtemisConfig.appKey);
        System.out.println("========================================");
    }
}
