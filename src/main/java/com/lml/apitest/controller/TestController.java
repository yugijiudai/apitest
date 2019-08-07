package com.lml.apitest.controller;

import com.lml.apitest.dto.UserDto;
import com.lml.apitest.util.ApiUtil;
import com.lml.apitest.vo.ApiVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-05
 */
@RestController
public class TestController {

    @PostMapping(value = "/postForJson")
    public ApiVo<UserDto> postForJson(@RequestBody UserDto userDto, HttpServletRequest req) {
        return ApiUtil.buildSuccess(userDto);
    }

    @PostMapping(value = "/postForForm")
    public ApiVo<UserDto> postForForm(UserDto userDto, HttpServletRequest req) {
        return ApiUtil.buildSuccess(userDto);
    }

    @PutMapping(value = "/put")
    public ApiVo<UserDto> put(@RequestBody UserDto userDto) {
        return ApiUtil.buildSuccess(userDto);
    }

    @DeleteMapping(value = "/delete")
    public ApiVo<String> delete(String name) {
        return ApiUtil.buildSuccess(name);
    }

    @GetMapping(value = "/get")
    public ApiVo<String> get(String name) {
        return ApiUtil.buildSuccess(name);
    }
}
