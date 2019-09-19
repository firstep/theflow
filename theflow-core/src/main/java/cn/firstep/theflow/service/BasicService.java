package cn.firstep.theflow.service;

import cn.firstep.theflow.common.Pagings;
import org.flowable.common.engine.api.query.NativeQuery;
import org.flowable.common.engine.api.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Basic Service.
 *
 * @author Alvin4u
 */
public interface BasicService {

    default <T, N> Page<T> doQuery(Query<?, N> query, Pageable pageable, Transform<N, T> transform) {
        long total = query.count();
        List<T> rst = null;
        if (total > 0) {
            List<N> list = query.listPage((int) pageable.getOffset(), pageable.getPageSize());
            rst = transform.trans(list);
        }
        return Pagings.pack(rst, pageable, total);
    }

    default <T, N, Q> Page<T> doQuery(NativeQuery<?, N> query, Pageable pageable, String sqlTemplate, String columnStr, Transform<N, T> transform) {
        long total = query.sql(String.format(sqlTemplate, "count(*)")).count();

        List<T> rst = null;
        if (total > 0) {
            query.sql(String.format(sqlTemplate, columnStr));
            List<N> list = query.listPage((int) pageable.getOffset(), pageable.getPageSize());
            rst = transform.trans(list);
        }
        return Pagings.pack(rst, pageable, total);
    }

    public interface Transform<S, T> {
        List<T> trans(List<S> source);
    }

}
