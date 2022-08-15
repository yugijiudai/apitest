package com.lml.core.util;

import cn.hutool.core.thread.ThreadUtil;
import com.lml.core.handler.TaskHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.collections.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author yugi
 * @apiNote 全局变量测试类
 * @since 2022-08-11
 */
@Slf4j
public class GlobalVariableUtilTest {

    @Test
    public void testNoShareGlobalCache() throws Exception {
        List<Object> list = Lists.newArrayList();
        list.add("thread1-2000");
        list.add("thread2-500");
        list.add("thread3-400");
        GlobalVariableUtilTask taskHandler = new GlobalVariableUtilTask();
        MyThreadUtil.handleConcurrent(list, 2, taskHandler);
        Map<String, String> result = taskHandler.getResult();
        Assert.assertEquals(result.get("500"), "thread2");
        Assert.assertEquals(result.get("2000"), "thread1");
        Assert.assertEquals(result.get("400"), "thread3");
    }

    @Test
    public void testShareGlobalCache() throws Exception {
        // 这里的用例只能有2个线程，因为底层调用了乐观锁和悲观锁机制，如果多了无法保证set的顺序和结果
        List<Object> list = Lists.newArrayList();
        list.add("thread1-2000");
        list.add("thread2-500");
        InitUtil.getSettingDto().setShareGlobalCache(true);
        ExecutorService executorService = Executors.newFixedThreadPool(list.size());
        Future<String> thread1 = executorService.submit(() -> handle("thread1", "2000"));
        // 确保thread1设置了值再执行thread2
        ThreadUtil.sleep(100);
        Future<String> thread2 = executorService.submit(() -> handle("thread2", "500"));
        String thread1Result = thread1.get();
        String thread2Result = thread2.get();
        Assert.assertEquals(thread1Result, "2000-thread2");
        Assert.assertEquals(thread2Result, "500-thread2");
    }


    private String handle(String content, String time) {
        String threadName = Thread.currentThread().getName();
        log.info("线程【{}】,开始处理{}", threadName, content);
        String key = "key";
        GlobalVariableUtil.setCache(key, content);
        ThreadUtil.sleep(Integer.parseInt(time), TimeUnit.MILLISECONDS);
        String value = GlobalVariableUtil.getCache(key).toString();
        log.info("线程【{}】,处理:{},当前的值是:{}", threadName, content, value);
        return time + "-" + value;
    }


    class GlobalVariableUtilTask implements TaskHandler {

        @Getter
        private final Map<String, String> result = Maps.newHashMap();

        @Override
        public Object runTask(Object param) {
            String[] split = param.toString().split("-");
            return handle(split[0], split[1]);
        }

        @Override
        public void callBack(List<Object> callBackList) {
            System.out.println("全部完成,执行情况如下...........");
            for (Object param : callBackList) {
                String[] split = param.toString().split("-");
                result.put(split[0], split[1]);
            }
        }
    }


}