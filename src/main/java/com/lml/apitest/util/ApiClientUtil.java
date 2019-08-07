package com.lml.apitest.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.apitest.dto.RequestDto;
import com.lml.apitest.enums.MethodEnum;
import com.lml.apitest.factory.RequestHandlerFactory;
import com.lml.apitest.handler.RequestCallBackHandler;
import com.lml.apitest.handler.RequestHandler;
import com.lml.apitest.vo.RestVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

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
     * 根据脚本配置,加载好脚本并且进行接口的请求
     *
     * @param fileName      要加载的脚本路径
     * @param callBackLists 请求接口后需要执行的回调,是个list,可以自己定义然后回调的处理顺序
     */
    public void doApiRequest(String fileName, List<RequestCallBackHandler> callBackLists) {
        JSONObject json = InitUtil.loadReqContent(fileName);
        // 将request的内容映射到对应的实体类里
        RequestDto requestDto = JSONUtil.toBean(json.getStr(REQ_KEY), RequestDto.class);
        MethodEnum method = MethodEnum.parese(requestDto.getMethod());
        // 根据方法类型获取对应的请求处理器
        RequestHandler handler = RequestHandlerFactory.getHandler(method);
        RestVo<JSONObject> actual = handler.doHandle(requestDto);
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


}
