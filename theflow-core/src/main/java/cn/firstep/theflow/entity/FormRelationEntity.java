package cn.firstep.theflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormRelationEntity {
    private String id;

    private String processDefinitionId;

    private String formDefinitionId;

    public FormRelationEntity(String processDefinitionId, String formDefinitionId) {
        this.processDefinitionId = processDefinitionId;
        this.formDefinitionId = formDefinitionId;
    }
}
