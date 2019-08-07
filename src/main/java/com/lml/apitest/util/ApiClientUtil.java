package com.lml.apitest.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.exception.InitException;
import com.lml.apitest.factory.RequestHandlerFactory;
import com.lml.apitest.handler.RequestCallBackHandler;
import com.lml.apitest.handler.RequestHandler;
import com.lml.apitest.vo.RestVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yugi
 * @apiNote
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

    public String getUrl() {
        // TODO yugi: 2019/8/6  这里需要改成从配置读取
        return "http://localhost:8080";
    }


    public void doApiRequest(String fileName, List<RequestCallBackHandler> callBackLists) {
        JSONObject json = loadReqContent(fileName);
        RequestDto requestDto = JSONUtil.toBean(json.getStr(REQ_KEY), RequestDto.class);
        MethodEnum method = MethodEnum.parese(requestDto.getMethod());
        // 根据方法类型获取对应的请求处理器
        RequestHandler handler = RequestHandlerFactory.getHandler(method);
        RestVo<JSONObject> actual = handler.doHandle(requestDto);
        log.info("请求回来的数据是:{}", actual.getResult());

        // 获取断言的数据
        String response = json.getStr(RES_KEY);
        JSONObject expectJson = JSONUtil.parseObj(response);
        log.info("断言的数据是:{}", expectJson);
        if (CollectionUtils.isNotEmpty(callBackLists)) {
            for (RequestCallBackHandler requestCallBackHandler : callBackLists) {
                requestCallBackHandler.doCallBack(actual, expectJson);
            }
        }
    }


    /**
     * 获取脚本数据
     *
     * @param fileName 脚本的名字
     * @return 返回脚本的json格式
     */
    private JSONObject loadReqContent(String fileName) {
        URL resource = ResourceUtil.getResource(fileName);
        if (resource == null) {
            throw new InitException("找不到要加载的脚本 " + fileName);
        }
        String script = FileUtil.readString(FileUtil.file(resource), StandardCharsets.UTF_8);
        return JSONUtil.parseObj(script);
    }

}
