package com.lml.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2021-08-30
 */
public class InitUtilTest {


    @Test
    public void testVariable() {
        GlobalVariableUtil.setCache("{{allBrand}}", Lists.newArrayList("\"资生堂", "兰蔻", "雅诗兰黛\"", "'迪奥"));
        GlobalVariableUtil.setCache("{{post}}", "主贴");
        GlobalVariableUtil.setCache("{{money}}", 434.654);
        GlobalVariableUtil.setCache("{{startTime}}", 1622474);
        GlobalVariableUtil.setCache("{{endTime}}", 1623166029049L);
        GlobalVariableUtil.setCache("{{nullName}}", "");
        GlobalVariableUtil.setCache("{{nullEs}}", "{}");
        GlobalVariableUtil.setCache("{{noise}}", "{\"term\": {\"hello\": {\"value\": 1234}}}");
        System.out.println(InitUtil.loadReqContent("demo/variableRequest.json5"));
    }


    @Test
    public void testInitSingle() {
        GlobalVariableUtil.setCache("{{post}}", "\"主贴");
        GlobalVariableUtil.setCache("${post}", "\"主贴");
        String param1 = "{\"query\": {\"bool\": {\"must\": [{\"term\": {\"is_main_post\": {\"value\": \"{{post}}\"} } } ] } } }";
        String param2 = "{\"query\": {\"bool\": {\"must\": [{\"term\": {\"is_main_post\": {\"value\": \"${post}\"} } } ] } } }";
        JSONObject result1 = this.formatAll(param1);
        JSONObject result2 = this.formatVariable(param2);
        Assert.assertEquals(result1, result2);
    }


    @Test
    public void testInitArr() {
        String param1 = "{\"query\": {\"bool\": {\"must\": [{\"terms\": {\"content_ad_noise\": \"#{contentAdNoise}\"} } ] } } }";
        String param2 = "{\"query\": {\"bool\": {\"must\": [{\"terms\": {\"content_ad_noise\": [\"{{contentAdNoise}}\"]} } ] } } }";
        List<String> emptyList = Lists.newArrayList();
        GlobalVariableUtil.setCache("#{contentAdNoise}", emptyList);
        GlobalVariableUtil.setCache("{{contentAdNoise}}", emptyList);
        JSONObject empty1 = this.formatVariable(param1);
        JSONObject empty2 = this.formatAll(param2);
        Assert.assertEquals(empty1, empty2);

        List<String> list = Lists.newArrayList("高质量广告", "\"杂音", "低质量广告\"", "'自发内容");
        GlobalVariableUtil.setCache("#{contentAdNoise}", list);
        GlobalVariableUtil.setCache("{{contentAdNoise}}", list);
        JSONObject arr1 = this.formatVariable(param1);
        JSONObject arr2 = this.formatAll(param2);
        Assert.assertEquals(arr1, arr2);
    }

    @Test
    public void testFormatScript() {
        GlobalVariableUtil.setCache("{{testId}}", "abc123232");
        GlobalVariableUtil.setCache("{{all}}", 10);
        GlobalVariableUtil.setCache("{{time}}", 20);
        GlobalVariableUtil.setCache("{{success}}", 2);
        GlobalVariableUtil.setCache("{{money}}", 325);
        GlobalVariableUtil.setCache("{{sentimentList}}", Lists.newArrayList(-1, 0, 1));
        GlobalVariableUtil.setCache("{{brandList}}", Lists.newArrayList("-6", "0", "6"));
        GlobalVariableUtil.setCache("{{nullList}}", Lists.newArrayList());
        System.out.println(ScriptFormatUtil.formatAllVariable(InitUtil.loadScript("demo/scriptFormat/wechatMsgTemplate.json5")));
        System.out.println(ScriptFormatUtil.formatAllVariable(InitUtil.loadScript("demo/scriptFormat/mailTemplate.txt")));
        System.out.println(ScriptFormatUtil.formatAllVariable(InitUtil.loadScript("demo/scriptFormat/requestNum.json5")));
    }

    @Test
    public void testSqlFormat() {
        GlobalVariableUtil.setCache("{{brand_name}}", "");
        GlobalVariableUtil.setCache("{{category_name}}", "香水");
        GlobalVariableUtil.setCache("{{site_id}}", 10);
        GlobalVariableUtil.setCache("{{volume_type_name}}", "and volume_type_name in ('PGC', 'BGC', 'UGC')");
        GlobalVariableUtil.setCache("{{sentiment}}", Lists.newArrayList(-1, 0, 1));
        GlobalVariableUtil.setCache("{{cat_name}}", Lists.newArrayList("微博", "微信", "新闻"));
        String sqlScript = InitUtil.loadScript("demo/scriptFormat/sqlDemo.txt");
        String assertSql = "select sum(volume_cnt) cnt\n" +
                "from t_brand_category_indicator_v01\n" +
                "where 1 = 1\n" +
                "  and category_name = '香水'\n" +
                "  \n" +
                "  and site_id = 10\n" +
                "  and publish_date between '2023-06-01' and '2023-06-03'\n" +
                "  and sentiment in (-1, 0, 1)\n" +
                "  and cat_name in ('微博', '微信', '新闻')\n" +
                "  and volume_type_name in ('PGC', 'BGC', 'UGC')";
        String result = ScriptFormatUtil.formatSqlCondition(sqlScript);
        Assert.assertEquals(result, assertSql);
    }

    private JSONObject formatAll(String param) {
        JSONObject result = JSONUtil.parseObj(ScriptFormatUtil.formatAllVariable(param));
        System.out.println(result);
        return result;
    }

    private JSONObject formatVariable(String param) {
        JSONObject result = JSONUtil.parseObj(ScriptFormatUtil.formatVariable(param));
        System.out.println(result);
        return result;
    }

}