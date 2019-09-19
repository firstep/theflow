package cn.firstep.theflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.engine.repository.ProcessDefinition;

import java.io.Serializable;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Definition implements Serializable {
    private static final long serialVersionUID = 413937939758478982L;

    private String tenantId;
    private String id;
    private String name;
    private String key;
    private int version;
    private String deployId;
    private String resourceName;
    private String diagramResourceName;
    private boolean suspended;
    private boolean hasStartForm;

    public static Definition of(ProcessDefinition def) {
        if (def == null) {
            return null;
        }

        Definition rst = new Definition();
        rst.setTenantId(def.getTenantId());
        rst.setId(def.getId());
        rst.setName(def.getName());
        rst.setKey(def.getKey());
        rst.setVersion(def.getVersion());
        rst.setDeployId(def.getDeploymentId());
        rst.setResourceName(def.getResourceName());
        rst.setDiagramResourceName(def.getDiagramResourceName());
        rst.setSuspended(def.isSuspended());
        rst.setHasStartForm(def.hasStartFormKey());

        return rst;
    }
}
