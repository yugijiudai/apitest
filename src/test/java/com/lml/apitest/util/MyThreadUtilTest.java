package com.lml.apitest.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lml.apitest.handler.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
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


    @Test
    public void testConcurrent() throws Exception {
        MyThreadUtil.handleConcurrent(list, num, taskHandler);
    }

    @Test
    public void testHandleConcurrentPartition() throws Exception {
        MyThreadUtil.handleConcurrentPartition(list, num, taskHandler);
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
}
