package com.lml.apitest.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.lml.apitest.exception.BizException;
import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 全局变量管理的工具类
 * @since 2019-08-08
 */
@UtilityClass
public class GlobalVariableUtil {

    private TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(60 * 60);

    /**
     * 获取缓存
     *
     * @param key 缓存的key
     * @return 返回缓存的结果
     */
    public Object getCache(String key) {
        Object val = timedCache.get(key);
        if (val == null) {
            throw new BizException("找不到:" + key + "的缓存!");
        }
        return val;
    }

    /**
     * 设置缓存
     *
     * @param key 缓存的key
     * @param val 缓存的值
     */
    public void setCache(String key, Object val) {
        timedCache.put(key, val);
    }
}
