package com.lml.core.dao;

import cn.hutool.core.convert.Convert;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.lml.core.enums.RequestStatusEnum;
import com.lml.core.exception.BizException;
import com.lml.core.exception.DaoException;
import com.lml.core.po.RequestContent;
import com.lml.core.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote 请求的实体类数据访问层
 * @since 2019-09-20
 */
@Slf4j
public class RequestContentDao {

    private final Db db = DbUtil.getDb();

    private static final String TABLE = "request_content";

    private static final String SELECT = "select rc.id, rc.name, rc.method, rc.start_time, rc.end_time, rc.headers, rc.request_status, rc.content, rc.url, rc.exception_msg, rc.thread_name, rc.request_group";


    /**
     * 插入数据
     *
     * @param requestContent {@link RequestContent}
     */
    public RequestContent add(RequestContent requestContent) {
        try {

            Entity entity = Entity.create(TABLE)
                    .set("name", requestContent.getName())
                    .set("method", requestContent.getMethod().name())
                    .set("start_time", requestContent.getStartTime())
                    .set("url", requestContent.getUrl())
                    .set("thread_name", requestContent.getThreadName())
                    .set("request_group", requestContent.getRequestGroup());
            Map<String, Object> headers = requestContent.getHeaders();
            if (MapUtils.isNotEmpty(headers)) {
                entity.set("headers", JSONUtil.toJsonStr(headers));
            }
            String json = requestContent.getContent();
            if (StringUtils.isNotBlank(json)) {
                entity.set("content", json);
            }
            Long id = db.insertForGeneratedKey(entity);
            return requestContent.setId(Convert.toInt(id));
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }

    /**
     * 更新数据
     *
     * @param requestContent 需要更新的数据
     */
    public void update(RequestContent requestContent) {
        try {
            Entity content = Entity.create()
                    .set("request_status", requestContent.getRequestStatus().name())
                    .set("end_time", requestContent.getEndTime())
                    .set("exception_msg", requestContent.getExceptionMsg());
            Entity where = Entity.create(TABLE).set("id", requestContent.getId());
            db.update(content, where);
        }
        catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DaoException(e);
        }
    }


    /**
     * 查询所有数据
     *
     * @return {@link RequestContent}
     */
    public List<RequestContent> findAll() {
        String sql = SELECT + " from " + TABLE + " rc";
        try {
            List<Entity> list = db.query(sql);
            return list.stream().map(this::transToRequestContent).collect(Collectors.toList());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(e);
        }
    }


    /**
     * 查询结果转换成对应的实体类
     *
     * @param entity 查询结果
     * @return 对应的实体
     */
    @SuppressWarnings("unchecked")
    private RequestContent transToRequestContent(Entity entity) {
        RequestContent requestContent = new RequestContent();
        requestContent.setId(entity.getInt("id"));
        requestContent.setName(entity.getStr("name"));
        requestContent.setMethod(Method.valueOf(entity.getStr("method")));
        requestContent.setStartTime(entity.getDate("start_time"));
        requestContent.setEndTime(entity.getDate("end_time"));
        requestContent.setUrl(entity.getStr("url"));
        requestContent.setExceptionMsg(entity.getStr("exception_msg"));
        String headers = entity.getStr("headers");
        requestContent.setRequestStatus(entity.getEnum(RequestStatusEnum.class, "request_status"));
        requestContent.setRequestGroup(entity.getStr("request_group"));
        requestContent.setThreadName(entity.getStr("thread_name"));
        if (StringUtils.isNotBlank(headers)) {
            requestContent.setHeaders(JSONUtil.toBean(headers, Map.class));
        }
        requestContent.setContent(entity.getStr("content"));
        return requestContent;
    }
}
