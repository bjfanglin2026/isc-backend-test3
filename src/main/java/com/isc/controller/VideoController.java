package com.isc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.isc.service.IscService;
import com.isc.service.UserService;
import com.isc.entity.User;
import com.isc.repository.UserRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private IscService iscService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/cameras")
    public Map<String, Object> getCameras(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "100") int pageSize,
            @RequestParam(required = false) String username) {
        logger.info("获取监控点列表: pageNo={}, pageSize={}, username={}", pageNo, pageSize, username);
        try {
            Map<String, Object> result = iscService.getCameraList(pageNo, pageSize);
            result.put("success", "0".equals(result.get("code")));
            
            // 如果指定了用户名，过滤出该用户有权限的摄像机
            if (username != null && !username.isEmpty()) {
                Map<String, Object> userCameras = userService.getUserCamerasByUsername(username);
                result.put("userCameras", userCameras.get("list"));
                result.put("filtered", true);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("获取监控点列表失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("code", "-1");
            error.put("msg", "获取监控点列表失败: " + e.getMessage());
            return error;
        }
    }

    @GetMapping("/camera/{cameraIndexCode}")
    public Map<String, Object> getCamera(@PathVariable String cameraIndexCode) {
        logger.info("获取监控点详情: cameraIndexCode={}", cameraIndexCode);
        try {
            var result = iscService.getCameraByIndexCode(cameraIndexCode);
            Map<String, Object> response = new HashMap<>();
            response.put("success", "0".equals(result.getString("code")));
            response.put("code", result.getString("code"));
            response.put("msg", result.getString("msg"));
            response.put("data", result.getJSONObject("data"));
            return response;
        } catch (Exception e) {
            logger.error("获取监控点详情失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("code", "-1");
            error.put("msg", "获取监控点详情失败: " + e.getMessage());
            return error;
        }
    }

    @GetMapping("/previewUrl")
    public Map<String, Object> getPreviewUrl(
            @RequestParam String cameraIndexCode,
            @RequestParam(defaultValue = "0") int streamType,
            @RequestParam(defaultValue = "rtsp") String protocol) {
        logger.info("获取预览URL: cameraIndexCode={}, streamType={}, protocol={}", cameraIndexCode, streamType, protocol);
        try {
            String result = iscService.getPreviewUrl(cameraIndexCode, streamType, protocol);
            JSONObject json = JSON.parseObject(result);
            Map<String, Object> response = new HashMap<>();
            response.put("code", json != null ? json.getString("code") : "-1");
            response.put("msg", json != null ? json.getString("msg") : "error");
            if (json != null && "0".equals(json.getString("code"))) {
                response.put("data", json.getJSONObject("data"));
                response.put("success", true);
            } else {
                response.put("success", false);
            }
            return response;
        } catch (Exception e) {
            logger.error("获取预览URL失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("code", "-1");
            error.put("msg", e.getMessage());
            return error;
        }
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "远陌云 Backend");
        return result;
    }

    /**
     * 测试API路径 - POST方式
     */
    @PostMapping("/test")
    public String testApiPost(@RequestBody JSONObject body) {
        String path = body.getString("path");
        String fullPath = "/artemis" + path;
        Map<String, String> pathMap = new HashMap<>(2);
        pathMap.put("https://", fullPath);
        String jsonBody = body.getJSONObject("body") != null ? body.getJSONObject("body").toJSONString() : "{}";
        String result = com.hikvision.artemis.sdk.ArtemisHttpUtil.doPostStringArtemis(pathMap, jsonBody, null, null, "application/json");
        logger.info("testApi path={} body={} response={}", fullPath, jsonBody, result);
        return result;
    }
}
