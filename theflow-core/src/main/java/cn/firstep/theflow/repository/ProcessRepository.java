package cn.firstep.theflow.repository;

import cn.firstep.theflow.repository.criteria.QueryProcessCriteria;
import cn.firstep.theflow.entity.ProcessEntity;

import java.util.List;

/**
 * @author Alvin4u
 */
public interface ProcessRepository {
    List<ProcessEntity> selectProcessByQueryCriteria(QueryProcessCriteria processQueryCriteria);

    long selectProcessCountByQueryCriteria(QueryProcessCriteria processQueryCriteria);
}
