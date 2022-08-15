package com.lml.core.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;
import com.lml.core.exception.BizException;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * @author yugi
 * @apiNote 全局变量管理的工具类
 * @since 2019-08-08
 */
@UtilityClass
@Slf4j
public class GlobalVariableUtil {


    @Getter
    private TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(1000 * 60 * 60);

    /**
     * 获取缓存
     *
     * @param key 缓存的key
     * @return 返回缓存的结果
     */
    public Object getCache(String key) {
        Object val = timedCache.get(generateKey(key));
        if (val == null) {
            throw new BizException("找不到:" + key + "的缓存!");
        }
        return val;
    }

    /**
     * 获取当前全部缓存
     *
     * @return 所有缓存详细信息的列表
     */
    public Iterator<CacheObj<String, Object>> getAllCache() {
        return timedCache.cacheObjIterator();
    }

    /**
     * 输出全部缓存
     */
    public void printAllCache() {
        Iterator<CacheObj<String, Object>> cacheObjIterator = getAllCache();
        while (cacheObjIterator.hasNext()) {
            CacheObj<String, Object> next = cacheObjIterator.next();
            log.info("{}={}", next.getKey(), next.getValue());
        }
    }

    /**
     * 设置缓存
     *
     * @param key 缓存的key
     * @param val 缓存的值
     */
    public void setCache(String key, Object val) {
        timedCache.put(generateKey(key), val);
    }

    /**
     * key的策略,用线程id拼接，防止不同现成set同样的key导致覆盖情况
     *
     * @param key 需要设置的key
     * @return 拼接好的key
     */
    private String generateKey(String key) {
        Boolean shareGlobalCache = InitUtil.getSettingDto().getShareGlobalCache();
        return shareGlobalCache ? key : Thread.currentThread().getId() + "-" + key;
    }
}
