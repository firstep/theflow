package cn.firstep.theflow.service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.repository.ProcessDefinitionQuery;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDefinitionPayload {
    private String id;
    private String name;
    private String key;
    private Boolean suspended;

    public void aplay(ProcessDefinitionQuery query) {
        if (StringUtils.isNotEmpty(id)) {
            query.processDefinitionId(id);
            return;
        }
        if (StringUtils.isNotEmpty(key)) {
            query.processDefinitionKey(key);
        }
        if (StringUtils.isNotEmpty(name)) {
            query.processDefinitionNameLike(name);
        }
        if (suspended != null) {
            if (suspended) {
                query.suspended();
            } else {
                query.active();
            }

        }
    }
}
