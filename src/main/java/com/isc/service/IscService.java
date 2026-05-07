package com.isc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IscService {

    private static final Logger logger = LoggerFactory.getLogger(IscService.class);
    private static final String ARTEMIS_PATH = "/artemis";

    // 内网地址替换为外网域名
    private static final String INTERNAL_HOST = "172.16.18.85";
    private static final String EXTERNAL_HOST = "isc02.bjzatx.com";

    // ==================== 视频监控 API ====================

    /**
     * 分页获取监控点列表
     */
    public JSONObject getCameras(int pageNo, int pageSize) {
        String api = ARTEMIS_PATH + "/api/resource/v1/cameras";
        JSONObject body = new JSONObject();
        body.put("pageNo", pageNo);
        body.put("pageSize", pageSize);
        String result = doPost(api, body.toJSONString());
        logger.info("getCameras 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 查询监控点列表V2
     */
    public JSONObject searchCameras(int pageNo, int pageSize) {
        String api = ARTEMIS_PATH + "/api/resource/v2/camera/search";
        JSONObject body = new JSONObject();
        body.put("pageNo", pageNo);
        body.put("pageSize", pageSize);
        String result = doPost(api, body.toJSONString());
        logger.info("searchCameras 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 根据监控点编号获取详细信息
     */
    public JSONObject getCameraByIndexCode(String cameraIndexCode) {
        String api = ARTEMIS_PATH + "/api/resource/v1/cameras/indexCode";
        JSONObject body = new JSONObject();
        body.put("cameraIndexCode", cameraIndexCode);
        String result = doPost(api, body.toJSONString());
        logger.info("getCameraByIndexCode 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 获取监控点预览取流URL（内网IP替换为外网域名）
     */
    public String getPreviewUrl(String cameraIndexCode, int streamType, String protocol) {
        String api = ARTEMIS_PATH + "/api/video/v2/cameras/previewURLs";
        JSONObject body = new JSONObject();
        body.put("cameraIndexCode", cameraIndexCode);
        body.put("streamType", streamType);
        body.put("protocol", protocol != null ? protocol : "rtsp");
        String result = doPost(api, body.toJSONString());
        logger.info("getPreviewUrl 响应: {}", result);
        if (result != null && result.contains(INTERNAL_HOST)) {
            result = result.replace(INTERNAL_HOST, EXTERNAL_HOST);
            logger.info("替换后的URL: {}", result);
        }
        return result;
    }

    /**
     * 获取监控点列表（简化返回）
     */
    public Map<String, Object> getCameraList(int pageNo, int pageSize) {
        JSONObject response = getCameras(pageNo, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("code", response.getString("code"));
        result.put("msg", response.getString("msg"));
        if ("0".equals(response.getString("code"))) {
            JSONObject data = response.getJSONObject("data");
            if (data != null) {
                result.put("total", data.getInteger("total"));
                result.put("pageNo", data.getInteger("pageNo"));
                result.put("pageSize", data.getInteger("pageSize"));
                result.put("list", parseCameraList(data.getJSONArray("list")));
            }
        }
        return result;
    }

    /**
     * 解析监控点列表，只保留关键字段
     */
    private JSONArray parseCameraList(JSONArray list) {
        JSONArray result = new JSONArray();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                JSONObject camera = list.getJSONObject(i);
                JSONObject simplified = new JSONObject();
                simplified.put("cameraIndexCode", camera.getString("cameraIndexCode"));
                simplified.put("cameraName", camera.getString("cameraName"));
                simplified.put("cameraType", camera.getInteger("cameraType"));
                simplified.put("cameraTypeName", camera.getString("cameraTypeName"));
                simplified.put("ptz", camera.getString("ptz"));
                simplified.put("regionIndexCode", camera.getString("regionIndexCode"));
                simplified.put("createTime", camera.getString("createTime"));
                simplified.put("updateTime", camera.getString("updateTime"));
                result.add(simplified);
            }
        }
        return result;
    }

    // ==================== 用户管理 API ====================

    /**
     * 分页获取用户列表
     * POST /api/resource/v1/users
     */
    public JSONObject getUserList(int pageNo, int pageSize) {
        String api = ARTEMIS_PATH + "/api/resource/v1/role/roles";
        JSONObject body = new JSONObject();
        body.put("pageNo", pageNo);
        body.put("pageSize", pageSize);
        String result = doPost(api, body.toJSONString());
        logger.info("getUserList 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 获取用户详情
     * POST /api/resource/v1/user/details
     */
    public JSONObject getUserById(String userId) {
        String api = ARTEMIS_PATH + "/api/resource/v1/users/indexCode";
        JSONObject body = new JSONObject();
        body.put("userId", userId);
        String result = doPost(api, body.toJSONString());
        logger.info("getUserById 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 创建用户
     * POST /api/resource/v1/user
     */
    public JSONObject createUser(JSONObject user) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user";
        String result = doPost(api, user.toJSONString());
        logger.info("createUser 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 更新用户
     * PUT /api/resource/v1/user
     */
    public JSONObject updateUser(String userId, JSONObject user) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user";
        user.put("userId", userId);
        String result = doPost(api, user.toJSONString());
        logger.info("updateUser 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 删除用户
     * DELETE /api/resource/v1/user/{userId}
     */
    public JSONObject deleteUser(String userId) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user/" + userId;
        String result = doPost(api, "{}");
        logger.info("deleteUser 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 分页获取角色列表
     * POST /api/resource/v1/roles
     */
    public JSONObject getRoleList(int pageNo, int pageSize) {
        String api = ARTEMIS_PATH + "/api/resource/v1/roles";
        JSONObject body = new JSONObject();
        body.put("pageNo", pageNo);
        body.put("pageSize", pageSize);
        String result = doPost(api, body.toJSONString());
        logger.info("getRoleList 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 分配摄像机权限给用户
     * POST /api/resource/v1/user/roles
     */
    public JSONObject assignCamerasToUser(String userId, JSONObject params) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user/roles";
        JSONObject body = new JSONObject();
        body.put("userId", userId);
        body.put("cameraIndexCodes", params.getJSONArray("cameraIndexCodes"));
        body.put("permissionType", params.getInteger("permissionType") != null ? params.getInteger("permissionType") : 1);
        String result = doPost(api, body.toJSONString());
        logger.info("assignCamerasToUser 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 获取用户的摄像机权限
     * POST /api/resource/v1/user/cameras
     */
    public JSONObject getUserCameras(String userId) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user/cameras";
        JSONObject body = new JSONObject();
        body.put("userId", userId);
        body.put("pageNo", 1);
        body.put("pageSize", 500);
        String result = doPost(api, body.toJSONString());
        logger.info("getUserCameras 响应: {}", result);
        return JSON.parseObject(result);
    }

    /**
     * 给用户分配角色
     * POST /api/resource/v1/user/role
     */
    public JSONObject assignRoleToUser(String userId, JSONObject params) {
        String api = ARTEMIS_PATH + "/api/resource/v1/user/role";
        JSONObject body = new JSONObject();
        body.put("userId", userId);
        body.put("roleId", params.getString("roleId"));
        String result = doPost(api, body.toJSONString());
        logger.info("assignRoleToUser 响应: {}", result);
        return JSON.parseObject(result);
    }

    // ==================== 基础请求方法 ====================

    /**
     * 发送POST请求
     */
    private String doPost(String api, String body) {
        Map<String, String> path = new HashMap<>(2);
        path.put("https://", api);
        return ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
    }

    /**
     * 发送PUT请求
     */

    /**
     * 发送DELETE请求
     */

    public Map<String, Object> ptzControl(String cameraIndexCode, Integer action, String command, Integer speed, Integer presetIndex) {
        String api = ARTEMIS_PATH + "/api/video/v1/ptzs/controlling";
        JSONObject body = new JSONObject();
        body.put("cameraIndexCode", cameraIndexCode);
        body.put("action", action);
        body.put("command", command);
        body.put("speed", speed);
        body.put("presetIndex", presetIndex);
        logger.info("PTZ request: {}", body.toJSONString());
        String result = doPost(api, body.toJSONString());
        logger.info("PTZ response: {}", result);
        JSONObject json = JSON.parseObject(result);
        Map<String, Object> response = new HashMap<>();
        if ("0".equals(json.getString("code"))) {
            response.put("success", true);
            response.put("msg", "OK");
        } else {
            response.put("success", false);
            response.put("msg", json.getString("msg"));
        }
        return response;
    }
}
