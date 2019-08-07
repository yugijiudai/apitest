package com.lml.apitest.demo;


import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
    public JSONObject postForForm(UserDto userDto, HttpServletRequest req) {
        return buildJsonSuccess(userDto);
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
        return buildSuccess(null);
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
        jsonObject.put("code", HttpStatus.FORBIDDEN.value());
        jsonObject.put("msg", msg);
        return jsonObject;
    }

    private JSONObject buildJsonSuccess(Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", HttpStatus.OK.value());
        jsonObject.put("data", JSONUtil.toJsonStr(data));
        return jsonObject;
    }

    private JSONObject buildSuccess(Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", HttpStatus.OK.value());
        jsonObject.put("data", data);
        return jsonObject;
    }
}
