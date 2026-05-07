package com.isc.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class PageController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() throws IOException {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'><title>远陌云视频平台</title></head>" +
               "<body><h1>远陌云 Backend 服务已启动</h1>" +
               "<p>后端API: <a href='/api/video/health'>/api/video/health</a></p>" +
               "<p>监控点列表: <a href='/api/video/cameras'>/api/video/cameras</a></p>" +
               "</body></html>";
    }
}
