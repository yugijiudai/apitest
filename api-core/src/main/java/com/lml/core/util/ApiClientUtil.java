package com.lml.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.core.dto.RequestDto;
import com.lml.core.enums.MethodEnum;
import com.lml.core.exception.BizException;
import com.lml.core.factory.RequestHandlerFactory;
import com.lml.core.handler.RequestCallBackHandler;
import com.lml.core.handler.RequestHandler;
import com.lml.core.vo.RestVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.MultiMapUtils;
import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * @author yugi
 * @apiNote http请求的客户端工具类
 * @since 2019-08-06
 */
@UtilityClass
@Slf4j
public class ApiClientUtil {

    /**
     * 脚本request字段
     */
    private final String REQ_KEY = "request";

    /**
     * 脚本response字段
     */
    private final String RES_KEY = "response";

    /**
     * 请求header的cookie字段
     */
    private final String COOKIE_KEY = "Set-Cookie";


    /**
     * 根据脚本配置,加载好脚本并且进行接口的请求
     *
     * @param fileName      要加载的脚本路径
     * @param callBackLists 请求接口后需要执行的回调,是个list,可以自己定义然后回调的处理顺序
     */
    public void doApiRequest(String fileName, List<RequestCallBackHandler> callBackLists) {
        JSONObject json = InitUtil.loadReqContent(fileName);
        doApiRequest(json, callBackLists);
    }

    /**
     * 直接传加载好的脚本并且进行接口的请求
     *
     * @param json          加载好的脚本
     * @param callBackLists 请求接口后需要执行的回调,是个list,可以自己定义然后回调的处理顺序
     */
    public void doApiRequest(JSONObject json, List<RequestCallBackHandler> callBackLists) {
        RestVo<JSONObject> actual = doApiRequest(json);
        // 获取断言的数据
        String response = json.getStr(RES_KEY);
        JSONObject expectJson = JSONUtil.parseObj(response);
        log.info("断言的数据是:{}", expectJson);
        // 进行回调处理
        if (CollectionUtils.isNotEmpty(callBackLists)) {
            for (RequestCallBackHandler requestCallBackHandler : callBackLists) {
                requestCallBackHandler.doCallBack(actual, expectJson);
            }
        }
    }

    /**
     * 直接传加载好的脚本并且进行接口的请求
     *
     * @param json 加载好的脚本
     * @return 返回请求后的数据
     */
    public RestVo<JSONObject> doApiRequest(JSONObject json) {
        // 将request的内容映射到对应的实体类里
        RequestDto requestDto = JSONUtil.toBean(json.getStr(REQ_KEY), RequestDto.class);
        MethodEnum method = MethodEnum.parese(requestDto.getMethod());
        // 根据方法类型获取对应的请求处理器
        RequestHandler handler = RequestHandlerFactory.getHandler(method);
        return handler.doHandle(requestDto);
    }


    /**
     * 根据给定的key值,从请求的头部获取对应的cookie值
     *
     * @param httpHeaders 请求头部
     * @param key         cookie的key
     * @return 符合这个cookie的key的值列表
     */
    public List<Object> getCookieByKey(HttpHeaders httpHeaders, String key) {
        ListValuedMap<String, Object> map = transCookieToMap(httpHeaders);
        if (map.isEmpty()) {
            throw new BizException("没有" + key + "这个cookie!");
        }
        return map.get(key);
    }

    /**
     * 将cookie转成多值类型的map
     *
     * @param httpHeaders 请求头部
     * @return 返回一个多值类型的map
     */
    public ListValuedMap<String, Object> transCookieToMap(HttpHeaders httpHeaders) {
        ListValuedMap<String, Object> map = MultiMapUtils.newListValuedHashMap();
        List<String> cookies = httpHeaders.get(COOKIE_KEY);
        if (CollectionUtils.isEmpty(cookies)) {
            return map;
        }
        for (String cookie : cookies) {
            String[] split = cookie.split(";");
            for (String tmp : split) {
                String[] values = tmp.split("=");
                map.put(values[0], values.length != 2 ? null : values[1]);
            }
        }
        return map;
    }

}
