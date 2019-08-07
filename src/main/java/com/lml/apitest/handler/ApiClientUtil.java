package com.lml.apitest.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.exception.InitException;
import com.lml.apitest.factory.RequestHandlerFactory;
import com.lml.apitest.vo.ApiVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    public void doApiRequest(String fileName) {
        JSONObject json = loadReqContent(fileName);
        RequestDto requestDto = JSONUtil.toBean(json.getStr(REQ_KEY), RequestDto.class);
        MethodEnum method = MethodEnum.parese(requestDto.getMethod());
        // 根据方法类型获取对应的请求处理器
        RequestHandler handler = RequestHandlerFactory.getHandler(method);
        ApiVo actual = handler.doHandle(requestDto);
        log.info("请求回来的数据是:{}", actual);

        // 获取断言的数据
        String response = json.getStr(RES_KEY);
        ApiVo expectVo = JSONUtil.toBean(response, ApiVo.class);
        log.info("断言的数据是:{}", expectVo);


        assertResult(actual, expectVo);
    }

    private static void assertResult(ApiVo actual, ApiVo expectVo) {
        Assert.assertEquals(expectVo.getCode(), actual.getCode());
        String expectMsg = expectVo.getMsg();
        if (StringUtils.isNotBlank(expectMsg)) {
            Assert.assertEquals(expectMsg, actual.getMsg());
        }
        Object expectData = expectVo.getData();
        if (expectData != null) {
            String data = expectData.toString();
            Object actualData = actual.getData();
            if (JSONUtil.isJsonObj(data)) {
                JSONObject expectJsonData = JSONUtil.parseObj(data);
                JSONObject actualJsonData = JSONUtil.parseObj(actualData);
                for (Map.Entry<String, Object> entry : expectJsonData.entrySet()) {
                    Assert.assertEquals(entry.getValue(), actualJsonData.get(entry.getKey()));
                }
            }
            else if (JSONUtil.isJsonArray(data)) {
                JSONArray expectJsonArray = JSONUtil.parseArray(data);
                JSONArray actualJsonArray = JSONUtil.parseArray(actualData.toString());
                Assert.assertEquals(expectJsonArray, actualJsonArray);
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
