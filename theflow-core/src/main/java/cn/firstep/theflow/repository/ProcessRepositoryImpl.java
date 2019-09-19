package cn.firstep.theflow.repository;

import cn.firstep.theflow.repository.criteria.QueryProcessCriteria;
import cn.firstep.theflow.entity.ProcessEntity;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alvin4u
 */
@Repository
public class ProcessRepositoryImpl implements ProcessRepository{
    private static final String NAMESPACE = "cn.firstep.theflow.entity.ProcessEntity.";

    @Autowired
    protected SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List<ProcessEntity> selectProcessByQueryCriteria(QueryProcessCriteria processQueryCriteria) {
        return sqlSessionTemplate.selectList(NAMESPACE + "selectProcessByQueryCriteria", processQueryCriteria);
    }

    @Override
    public long selectProcessCountByQueryCriteria(QueryProcessCriteria processQueryCriteria) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "selectProcessCountByQueryCriteria", processQueryCriteria);
    }
}
