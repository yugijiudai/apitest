package com.lml.core.util;

import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.lml.core.handler.TaskHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote
 * @since 2019-08-22
 */
@Slf4j
@UtilityClass
public class MyThreadUtil {


    /**
     * 使用多线程来消费list
     *
     * @param list        需要消费的list
     * @param threadNum   线程的数量
     * @param taskHandler 消费这个list的处理器
     */
    public void handleConcurrent(List<Object> list, int threadNum, TaskHandler taskHandler) throws Exception {
        int size = list.size();
        if (threadNum > size) {
            throw new IllegalArgumentException("线程数量不能超过列表大小");
        }
        ExecutorService executorService = ThreadUtil.newExecutor(threadNum);
        List<Object> callBackList = Lists.newLinkedList();
        // 把每一个任务放在一个list里面
        List<Callable<Object>> taskList = list.stream().map(obj -> (Callable<Object>) () -> taskHandler.runTask(obj)).collect(Collectors.toList());
        List<Future<Object>> futures = executorService.invokeAll(taskList);
        for (Future<Object> future : futures) {
            callBackList.add(future.get());
        }
        taskHandler.callBack(callBackList);
        executorService.shutdown();
    }

    /**
     * 对list分块,然后使用多线程来消费
     *
     * @param list         需要消费的list
     * @param partitionNum 一组多少个元素
     * @param taskHandler  消费这个list的处理器
     */
    public void handleConcurrentPartition(List<Object> list, int partitionNum, TaskHandler taskHandler) throws Exception {
        if (partitionNum < 1) {
            throw new IllegalArgumentException("分区数量至少要大于1");
        }
        List<List<Object>> partition = ListUtils.partition(list, partitionNum);
        List<Object> callBackList = Lists.newLinkedList();
        ExecutorService executorService = ThreadUtil.newExecutor(partition.size());
        List<Callable<List<Object>>> taskList = Lists.newLinkedList();
        for (List<Object> param : partition) {
            taskList.add(() -> {
                log.info("线程【{}】,需要处理{}", Thread.currentThread().getName(), param);
                return param.stream().map(taskHandler::runTask).collect(Collectors.toList());
            });
        }
        List<Future<List<Object>>> futures = executorService.invokeAll(taskList);
        for (Future<List<Object>> future : futures) {
            callBackList.addAll(future.get());
        }
        taskHandler.callBack(callBackList);
        executorService.shutdown();
    }
}
