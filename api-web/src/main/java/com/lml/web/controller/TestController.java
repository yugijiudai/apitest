package com.lml.web.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.web.dto.UserDto;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-05
 */
@RestController
public class TestController {

    @PostMapping(value = "/postForJson")
    public JSONObject postForJson(@RequestBody UserDto userDto, HttpServletRequest req) {
        return buildJsonSuccess(userDto);
    }

    @PostMapping(value = "/postForForm")
    public JSONObject postForForm(UserDto userDto, HttpServletRequest req, HttpServletResponse res) {
        res.addHeader("hello", "asasasas");
        System.out.println(req.getHeader("hello"));
        System.out.println(req.getHeader("okc"));
        return buildJsonSuccess(userDto);
    }

    @PostMapping(value = "/testPost")
    public JSONObject testPost(@RequestBody UserDto userDto, HttpServletRequest req) {
        System.out.println(userDto);
        System.out.println(req.getHeader("Content-Type"));
        System.out.println(req.getHeader("Authorization"));
        return buildJsonSuccess(userDto);
    }


    @PostMapping(value = "/uploadFile")
    public JSONObject uploadFile(MultipartFile[] uploadFile, String name) {
        StringBuilder sb = new StringBuilder();
        for (MultipartFile multipartFile : uploadFile) {
            sb.append(multipartFile.getOriginalFilename()).append("-");
        }
        sb.append(name);
        return buildSuccess(sb);
    }

    @PutMapping(value = "/put")
    public JSONObject put(@RequestBody UserDto userDto) {
        return buildJsonSuccess(userDto);
    }

    @DeleteMapping(value = "/delete")
    public JSONObject delete(String name) {
        return buildSuccess(name);
    }

    @GetMapping(value = "/get")
    public JSONObject get(String name) {
        return buildSuccess(name);
    }

    @PostMapping(value = "/login")
    public JSONObject login(@RequestBody UserDto userDto, HttpServletRequest request) {
        String random = IdUtil.randomUUID();
        request.getSession().setAttribute(userDto.getName(), random);
        return buildSuccess(random);
    }

    @GetMapping(value = "/getUser")
    public JSONObject getUser(UserDto userDto, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + ":" + cookie.getValue());
            }
        }
        System.out.println(request.getHeader("helloWorld"));
        Object attribute = request.getSession().getAttribute(userDto.getName());
        if (attribute == null) {
            return buildFail("没有登录");
        }
        return buildSuccess(attribute.toString());
    }

    private JSONObject buildFail(String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", HttpStatus.FORBIDDEN.value());
        jsonObject.set("msg", msg);
        return jsonObject;
    }

    private JSONObject buildJsonSuccess(Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", HttpStatus.OK.value());
        jsonObject.set("data", JSONUtil.toJsonStr(data));
        return jsonObject;
    }

    private JSONObject buildSuccess(Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", HttpStatus.OK.value());
        jsonObject.set("data", data);
        return jsonObject;
    }
}
