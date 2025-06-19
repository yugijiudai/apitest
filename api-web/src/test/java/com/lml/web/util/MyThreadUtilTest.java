package com.lml.web.util;

import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.core.handler.TaskHandler;
import com.lml.core.util.MyThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-22
 */
@Slf4j
public class MyThreadUtilTest {


    private int num = 2;

    private List<Object> list = Lists.newArrayList("a", "b", "c", "d", "e");

    private TaskHandler taskHandler = new SleepTask();

    private TaskHandler retryTaskHandler = new RetryTask();


    @Test
    public void testConcurrent() throws Exception {
        MyThreadUtil.handleConcurrent(list, num, taskHandler);
    }

    @Test
    public void testHandleConcurrentPartition() throws Exception {
        MyThreadUtil.handleConcurrentPartition(list, num, taskHandler);
    }


    @Test
    public void testRetry() throws Exception {
        MyThreadUtil.handleConcurrent(list, num, retryTaskHandler);
    }


    private String handle(Object content) {
        String threadName = Thread.currentThread().getName();
        String format = StrUtil.format("线程【{}】,开始处理{}", threadName, content);
        System.out.println(format);
        int time = RandomUtil.randomInt(10) * 200;
        long start = System.currentTimeMillis();
        ThreadUtil.sleep(time, TimeUnit.MILLISECONDS);
        format = StrUtil.format("线程【{}】,处理:{}完成", threadName, content);
        System.out.println(format);
        return StrUtil.format("线程【{}】,处理:{}使用了{}", threadName, content, System.currentTimeMillis() - start);
    }


    class SleepTask implements TaskHandler {

        @Override
        public Object runTask(Object param) {
            return handle(param);
        }

        @Override
        public void callBack(List<Object> callBackList) {
            System.out.println("全部完成,执行情况如下...........");
            for (Object param : callBackList) {
                System.out.println(param);
            }
        }
    }


    class RetryTask implements TaskHandler {

        private final Map<String, Integer> errorCounterMap = Maps.newLinkedHashMap();

        private final Integer maxErrorCount = 10;


        @Override
        public Object runTask(Object param) {
            return retryHandle(param, this.errorCounterMap, this.maxErrorCount);
        }

        @Override
        public void callBack(List<Object> callBackList) {
            System.out.println("全部完成,执行情况如下...........");
            for (Object param : callBackList) {
                System.out.println(param);
            }
        }
    }


    private String retryHandle(Object content, Map<String, Integer> errorCounterMap, Integer maxErrorCount) {
        String threadName = Thread.currentThread().getName();
        Console.log("线程【{}】,开始处理{}", threadName, content);
        long start = System.currentTimeMillis();
        int time = RandomUtil.randomInt(10) * 200;
        try {
            this.errorMethod(content);
        }
        catch (Exception e) {
            String key = DigestUtil.md5Hex(content.toString(), Charset.defaultCharset());
            Integer errorCnt = errorCounterMap.getOrDefault(key, 0);
            errorCnt++;
            errorCounterMap.put(key, errorCnt);
            Console.error("线程:{}发生错误,尝试重试{}次....:{}", threadName, errorCnt, content);
            if (errorCnt >= maxErrorCount) {
                String msg = StrUtil.format("线程:{}发生错误次数超过最大{}次,不执行!参数:{}", threadName, maxErrorCount, content);
                throw new RuntimeException(msg);
            }
            return retryHandle(content, errorCounterMap, maxErrorCount);
        }
        ThreadUtil.sleep(time, TimeUnit.MILLISECONDS);
        return StrUtil.format("线程【{}】,处理:{}使用了{}", threadName, content, System.currentTimeMillis() - start);
    }

    private void errorMethod(Object content) {
        int randomError = RandomUtil.randomInt(100);
        if (randomError < 5) {
            throw new RuntimeException(content + "发生错误");
        }
    }


}
