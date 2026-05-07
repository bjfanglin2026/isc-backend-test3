package com.isc.service;

import com.isc.entity.User;
import com.isc.entity.UserCamera;
import com.isc.repository.UserRepository;
import com.isc.repository.UserCameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserCameraRepository userCameraRepository;

    public Map<String, Object> getUserList() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> list = users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("displayName", u.getDisplayName());
            m.put("email", u.getEmail());
            m.put("phone", u.getPhone());
            m.put("role", u.getRole());
            m.put("enabled", u.getEnabled());
            m.put("createTime", u.getCreateTime());
            return m;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("list", list);
        result.put("total", list.size());
        return result;
    }

    public Map<String, Object> getUser(Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", false);
            r.put("msg", "用户不存在");
            return r;
        }
        User u = opt.get();
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("id", u.getId());
        data.put("username", u.getUsername());
        data.put("displayName", u.getDisplayName() != null ? u.getDisplayName() : "");
        data.put("email", u.getEmail() != null ? u.getEmail() : "");
        data.put("phone", u.getPhone() != null ? u.getPhone() : "");
        data.put("role", u.getRole());
        data.put("enabled", u.getEnabled());
        data.put("createTime", u.getCreateTime());
        m.put("data", data);
        return m;
    }

    @Transactional
    public Map<String, Object> createUser(Map<String, Object> params) {
        String username = (String) params.get("username");
        if (username == null || username.isBlank()) {
            return Map.of("success", false, "msg", "用户名不能为空");
        }
        
        if (userRepository.existsByUsername(username)) {
            return Map.of("success", false, "msg", "用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword((String) params.getOrDefault("password", "123456"));
        user.setDisplayName((String) params.get("displayName"));
        user.setEmail((String) params.get("email"));
        user.setPhone((String) params.get("phone"));
        user.setRole((String) params.getOrDefault("role", "USER"));
        user.setEnabled(true);
        
        user = userRepository.save(user);
        
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("msg", "用户创建成功");
        r.put("id", user.getId());
        return r;
    }

    @Transactional
    public Map<String, Object> updateUser(Long id, Map<String, Object> params) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("success", false, "msg", "用户不存在");
        }
        
        User user = opt.get();
        if (params.containsKey("displayName")) user.setDisplayName((String) params.get("displayName"));
        if (params.containsKey("email")) user.setEmail((String) params.get("email"));
        if (params.containsKey("phone")) user.setPhone((String) params.get("phone"));
        if (params.containsKey("role")) user.setRole((String) params.get("role"));
        if (params.containsKey("enabled")) user.setEnabled((Boolean) params.get("enabled"));
        if (params.containsKey("password") && params.get("password") != null && !((String)params.get("password")).isBlank()) {
            user.setPassword((String) params.get("password"));
        }
        userRepository.save(user);
        return Map.of("success", true, "msg", "用户更新成功");
    }

    @Transactional
    public Map<String, Object> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return Map.of("success", false, "msg", "用户不存在");
        }
        userCameraRepository.deleteByUserId(id);
        userRepository.deleteById(id);
        return Map.of("success", true, "msg", "用户删除成功");
    }

    public Map<String, Object> getUserCameras(Long userId) {
        List<UserCamera> cameras = userCameraRepository.findByUserId(userId);
        List<Map<String, Object>> list = cameras.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("cameraIndexCode", c.getCameraIndexCode());
            m.put("cameraName", c.getCameraName() != null ? c.getCameraName() : "");
            m.put("permissionType", c.getPermissionType());
            return m;
        }).collect(Collectors.toList());
        
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("list", list);
        r.put("total", list.size());
        return r;
    }

    @Transactional
    public Map<String, Object> assignCameras(Long userId, List<Map<String, Object>> cameras) {
        if (!userRepository.existsById(userId)) {
            return Map.of("success", false, "msg", "用户不存在");
        }
        
        userCameraRepository.deleteByUserId(userId);
        
        for (Map<String, Object> cam : cameras) {
            UserCamera uc = new UserCamera();
            uc.setUserId(userId);
            uc.setCameraIndexCode((String) cam.get("cameraIndexCode"));
            uc.setCameraName((String) cam.get("cameraName"));
            uc.setPermissionType(cam.get("permissionType") != null ? (Integer) cam.get("permissionType") : 1);
            userCameraRepository.save(uc);
        }
        
        return Map.of("success", true, "msg", "摄像机分配成功");
    }

    @Transactional
    public Map<String, Object> addCameraToUser(Long userId, Map<String, Object> camera) {
        String cameraIndexCode = (String) camera.get("cameraIndexCode");
        if (userCameraRepository.existsByUserIdAndCameraIndexCode(userId, cameraIndexCode)) {
            return Map.of("success", false, "msg", "该摄像机已分配给此用户");
        }
        
        UserCamera uc = new UserCamera();
        uc.setUserId(userId);
        uc.setCameraIndexCode(cameraIndexCode);
        uc.setCameraName((String) camera.get("cameraName"));
        uc.setPermissionType(camera.get("permissionType") != null ? (Integer) camera.get("permissionType") : 1);
        userCameraRepository.save(uc);
        
        return Map.of("success", true, "msg", "摄像机分配成功");
    }

    @Transactional
    public Map<String, Object> removeCameraFromUser(Long userId, String cameraIndexCode) {
        userCameraRepository.deleteByUserIdAndCameraIndexCode(userId, cameraIndexCode);
        return Map.of("success", true, "msg", "摄像机移除成功");
    }

    public Map<String, Object> login(String username, String password) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return Map.of("success", false, "msg", "用户不存在");
        }
        User user = opt.get();
        if (!user.getEnabled()) {
            return Map.of("success", false, "msg", "账号已被禁用");
        }
        if (!user.getPassword().equals(password)) {
            return Map.of("success", false, "msg", "密码错误");
        }
        Map<String, Object> r = new HashMap<>();
        r.put("success", true);
        r.put("msg", "登录成功");
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        userData.put("role", user.getRole());
        r.put("user", userData);
        return r;
    }

    public Map<String, Object> getUserCamerasByUsername(String username) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", false);
            r.put("cameras", List.of());
            return r;
        }
        return getUserCameras(opt.get().getId());
    }

}