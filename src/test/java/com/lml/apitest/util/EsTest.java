package com.lml.apitest.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.apitest.BaseTest;
import com.lml.apitest.handler.RequestCallBackHandler;
import com.lml.apitest.vo.RestVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-06
 */
public class EsTest extends BaseTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void userPostTest() {
        String url = "http://datastory1:5601/api/sense/proxy?uri=https%3A%2F%2Fes-banyan.datatub.com%2F_count";
        JSONObject json = InitUtil.loadReqContent("es/esTest.json");
        HttpResponse authorization = HttpRequest.post(url)
                .body(json)
                .header("kbn-version", "4.5.4")
                .header("Authorization", "Basic aGFvd2VuOmlyX3k2RWt0Mk5vZw==")
                .execute();
        System.out.println(authorization);
    }

    @Test
    public void userPostTest3() {
        String url = "http://datastory1:5601/api/sense/proxy?uri=https%3A%2F%2Fes-banyan.datatub.com%2F_count";
/*        JSONObject json = InitUtil.loadReqContent("es/esTest.json");
        Map<String, Object> map = Maps.newHashMap();
        map.put("kbn-version", "4.5.4");
        map.put("Authorization", "Basic aGFvd2VuOmlyX3k2RWt0Mk5vZw==");
        RestVo<String> result = RestUtil.post(url, json.toString(), String.class, map);
        System.out.println(result);*/


        String reqJsonStr = "{\"query\": {\"bool\": {\"should\": [{\"match_phrase\": {\"title\": \"Air Jordan\"} }, {\"match_phrase\": {\"content\": \"Air Jordan\"} } ], \"minimum_number_should_match\": 1 } } }";
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON);
        // HttpHeaders headers = this.createHeaders("haowen", "ir_y6Ekt2Nog");
        headers.add("kbn-version", "4.5.4");
        // headers.add("Authorization", "Basic aGFvd2VuOmlyX3k2RWt0Mk5vZw==");
        headers.setBasicAuth("haowen", "ir_y6Ekt2Nog");
        headers.setBearerAuth("aGFvd2VuOmlyX3k2RWt0Mk5vZw==");
        HttpEntity<String> entity = new HttpEntity<>(reqJsonStr, headers);
        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(resp);

    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.US_ASCII) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }


    @Test
    public void userPostTest2() {
        String url = "http://localhost:8080/postForJson";
        JSONObject json = InitUtil.loadReqContent("demo/userPost.json");
        JSONObject param = json.getJSONObject("request").getJSONObject("param");
        System.out.println(param);
        HttpResponse authorization = HttpRequest.post(url)
                .body(param)
                .header("kbn-version", "4.5.4")
                .execute();
        System.out.println(authorization);
    }


}