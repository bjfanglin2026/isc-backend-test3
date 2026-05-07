package com.isc.controller;

import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private com.isc.config.IscProperties iscProperties;

    private String doPost(String api, String body) {
        Map<String, String> path = new HashMap<>(2);
        path.put("https://", api);
        return ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
    }

    @GetMapping("/explore")
    public String testPath(@RequestParam(name = "path") String path) {
        String fullApi = "/artemis" + path;
        String result = doPost(fullApi, "{}");
        return result;
    }
}
