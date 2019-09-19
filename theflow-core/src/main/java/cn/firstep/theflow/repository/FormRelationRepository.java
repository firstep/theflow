package cn.firstep.theflow.repository;

import cn.firstep.theflow.entity.FormRelationEntity;
import org.flowable.form.api.FormDefinition;

import java.util.List;

/**
 * @author Alvin4u
 */
public interface FormRelationRepository {

    void save(FormRelationEntity formRelation);

    void delete(FormRelationEntity formRelation);

    void deleteByProcessDefinitionId(String processDefinitionId);

    List<FormDefinition> findFormDefinitionByProcessDefinitionId(String processDefinitionId);

    FormDefinition findFormDefinitionByKeyAndProcessDefinitionId(String key, String processDefinitionId);
}
