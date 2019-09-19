package cn.firstep.theflow.repository;

import cn.firstep.theflow.entity.FormRelationEntity;
import org.flowable.form.api.FormDefinition;
import org.flowable.ui.common.repository.UuidIdGenerator;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alvin4u
 */
@Repository
public class FormRelationRepositoryImpl implements FormRelationRepository {
    private static final String NAMESPACE = "cn.firstep.theflow.entity.FormRelationEntity.";

    @Autowired
    protected SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    protected UuidIdGenerator idGenerator;

    @Override
    public void save(FormRelationEntity formRelation) {
        if (formRelation.getId() == null) {
            formRelation.setId(idGenerator.generateId());
            sqlSessionTemplate.insert(NAMESPACE + "insertFormRelation", formRelation);
        } else {
            sqlSessionTemplate.update(NAMESPACE + "updateFormRelation", formRelation);
        }
    }

    @Override
    public void delete(FormRelationEntity formRelation) {
        sqlSessionTemplate.delete(NAMESPACE + "deleteFormRelation", formRelation);
    }

    @Override
    public void deleteByProcessDefinitionId(String processDefinitionId) {
        sqlSessionTemplate.delete(NAMESPACE + "deleteByProcessDefinitionId", processDefinitionId);
    }

    @Override
    public List<FormDefinition> findFormDefinitionByProcessDefinitionId(String processDefinitionId) {
        return sqlSessionTemplate.selectList(NAMESPACE + "findFormDefinitionByProcessDefinitionId", processDefinitionId);
    }

    @Override
    public FormDefinition findFormDefinitionByKeyAndProcessDefinitionId(String key, String processDefinitionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("processDefinitionId", processDefinitionId);
        return sqlSessionTemplate.selectOne(NAMESPACE + "findFormDefinitionByKeyAndProcessDefinitionId", params);
    }
}
