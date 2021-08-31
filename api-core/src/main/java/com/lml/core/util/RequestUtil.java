package com.lml.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.core.dto.RequestDto;
import com.lml.core.enums.MethodEnum;
import com.lml.core.vo.RestVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yugi
 * @apiNote 简易请求工具类, 用于简化请求的步骤, 只需要提供一个参数的json文件即可
 * @since 2021-08-30
 */
@UtilityClass
@Slf4j
public class RequestUtil {

    /**
     * 请求的url默认从缓存读取,这个是默认的key
     */
    private final String REQUEST_URL = "${url}";

    /**
     * 执行get请求
     *
     * @param scriptPath 请求参数的脚本
     * @param name       请求接口的名字
     * @param header     请求头
     * @return 响应体
     */
    public JSONObject doGet(String scriptPath, String name, JSONObject header) {
        RequestDto requestDto = buildRequestDto(name, header, MethodEnum.GET);
        return doApi(scriptPath, requestDto);
    }

    /**
     * 执行postJson请求
     *
     * @param scriptPath 请求参数的脚本
     * @param name       请求接口的名字
     * @param header     请求头
     * @return 响应体
     */
    public JSONObject doPost(String scriptPath, String name, JSONObject header) {
        RequestDto requestDto = buildRequestDto(name, header, MethodEnum.POST);
        return doApi(scriptPath, requestDto);
    }

    /**
     * 执行post表单请求
     *
     * @param scriptPath 请求参数的脚本
     * @param name       请求接口的名字
     * @param header     请求头
     * @return 响应体
     */
    public JSONObject doPostForm(String scriptPath, String name, JSONObject header) {
        RequestDto requestDto = buildRequestDto(name, header, MethodEnum.POST_FROM_DATA);
        return doApi(scriptPath, requestDto);
    }

    /**
     * 执行put请求
     *
     * @param scriptPath 请求参数的脚本
     * @param name       请求接口的名字
     * @param header     请求头
     * @return 响应体
     */
    public JSONObject doPut(String scriptPath, String name, JSONObject header) {
        RequestDto requestDto = buildRequestDto(name, header, MethodEnum.PUT);
        return doApi(scriptPath, requestDto);
    }

    /**
     * 执行delete请求
     *
     * @param scriptPath 请求参数的脚本
     * @param name       请求接口的名字
     * @param header     请求头
     * @return 响应体
     */
    public JSONObject doDelete(String scriptPath, String name, JSONObject header) {
        RequestDto requestDto = buildRequestDto(name, header, MethodEnum.DELETE);
        return doApi(scriptPath, requestDto);
    }

    /**
     * 根据脚本发出请求
     *
     * @param scriptPath 脚本的地址,只放请求的参数
     * @param requestDto {@link RequestDto}
     * @return 返回请求后的数据
     */
    public JSONObject doApi(String scriptPath, RequestDto requestDto) {
        String formatScript = ScriptFormatUtil.formatAllVariable(InitUtil.loadScript(scriptPath));
        JSONObject paramObj = JSONUtil.parseObj(formatScript);
        return doApi(paramObj, requestDto);
    }

    /**
     * 根据请求参数发出请求(最终调用请求的方法)
     *
     * @param requestDto {@link RequestDto}
     * @return 返回请求后的数据
     */
    public JSONObject doApi(JSONObject paramObj, RequestDto requestDto) {
        log.info("请求的接口是:【{}】参数是:{}", requestDto.getName(), paramObj);
        JSONObject requestBody = setCommonParam(requestDto, paramObj);
        RestVo<JSONObject> vo = ApiClientUtil.doApiRequest(requestBody);
        JSONObject result = vo.getResult();
        log.debug("请求接口【{}】,结果:【{}】", requestDto.getName(), result);
        log.debug("请求接口【{}】,响应头是:【{}】", requestDto.getName(), vo.getHttpHeaders());
        return result;
    }


    /**
     * 组装成一些通用的参数
     *
     * @param requestDto {@link RequestDto}
     * @param paramObj   真正请求的参数对象
     * @return 返回封装好的请求对象
     */
    private JSONObject setCommonParam(RequestDto requestDto, JSONObject paramObj) {
        // 基础的请求体
        JSONObject request = new JSONObject();
        String name = requestDto.getName();
        if (StringUtils.isNotBlank(name)) {
            request.set("name", name);
        }
        request.set("useRelativeUrl", requestDto.isUseRelativeUrl());
        request.set("url", requestDto.getUrl());
        request.set("param", paramObj);
        request.set("method", requestDto.getMethod());
        JSONObject baseReq = new JSONObject();
        JSONObject header = requestDto.getHeaders();
        if (header != null) {
            request.set(ApiClientUtil.HEADER, header);
        }
        return baseReq.set(ApiClientUtil.REQ_KEY, request);
    }


    /**
     * 构建基本的请求类
     *
     * @param name       请求接口的名字
     * @param header     请求头
     * @param methodEnum 请求类型
     * @return {@link RequestDto}
     */
    private RequestDto buildRequestDto(String name, JSONObject header, MethodEnum methodEnum) {
        // 地址直接从缓存读取
        String url = GlobalVariableUtil.getCache(REQUEST_URL).toString();
        boolean useRelativeUrl = !url.startsWith("http") && !url.startsWith("https:");
        return new RequestDto().setHeaders(header).setName(name).setUrl(url).setUseRelativeUrl(useRelativeUrl).setMethod(methodEnum.getMethod());
    }


}
