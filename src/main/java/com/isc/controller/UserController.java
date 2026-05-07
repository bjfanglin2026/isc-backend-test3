package com.isc.controller;

import com.isc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    private UserService userService;

    // ============ 用户管理 ============

    @GetMapping("/users")
    public Map<String, Object> getUserList() {
        return userService.getUserList();
    }

    @GetMapping("/user/{id}")
    public Map<String, Object> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/user")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> params) {
        return userService.createUser(params);
    }

    @PutMapping("/user/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        return userService.updateUser(id, params);
    }

    @DeleteMapping("/user/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // ============ 用户摄像机权限 ============

    @GetMapping("/user/{id}/cameras")
    public Map<String, Object> getUserCameras(@PathVariable Long id) {
        return userService.getUserCameras(id);
    }

    @PostMapping("/user/{id}/cameras")
    public Map<String, Object> assignCameras(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cameras = (List<Map<String, Object>>) params.get("cameras");
        return userService.assignCameras(id, cameras);
    }

    @PostMapping("/user/{id}/camera")
    public Map<String, Object> addCameraToUser(@PathVariable Long id, @RequestBody Map<String, Object> camera) {
        return userService.addCameraToUser(id, camera);
    }

    @DeleteMapping("/user/{id}/camera/{cameraIndexCode}")
    public Map<String, Object> removeCameraFromUser(@PathVariable Long id, @PathVariable String cameraIndexCode) {
        return userService.removeCameraFromUser(id, cameraIndexCode);
    }

    // ============ 登录 ============

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        return userService.login(username, password);
    }
}
