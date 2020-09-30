package com.lml.core.handler;

import java.util.List;

/**
 * @author yugi
 * @apiNote 任务处理器, 给多线程使用
 * @see com.lml.core.util.MyThreadUtil
 * @since 2019-08-22
 */
public interface TaskHandler {


    /**
     * 需要执行的任务
     *
     * @param param 执行这个任务所需要的参数
     * @return 执行完后需要返回的参数
     */
    Object runTask(Object param);

    /**
     * 全部任务执行完后的回调列表
     *
     * @param callBackList 每个任务执行完返回的参数列表
     */
    void callBack(List<Object> callBackList);
}
