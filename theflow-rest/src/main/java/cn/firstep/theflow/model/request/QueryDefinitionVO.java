package cn.firstep.theflow.model.request;

import cn.firstep.theflow.service.payload.QueryDefinitionPayload;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;

/**
 * @author Alvin4u
 */
public class QueryDefinitionVO {
    @Size(max = 255, message = "{def.id.max}")
    private String id;

    @Size(max = 255, message = "{def.name.max}")
    private String name;

    @Size(max = 255, message = "{def.key.max}")
    private String key;

    private Boolean suspended;

    public QueryDefinitionPayload toPayload() {
        QueryDefinitionPayload payload = new QueryDefinitionPayload();
        payload.setId(StringUtils.trim(id));
        payload.setName(StringUtils.trim(name));
        payload.setKey(StringUtils.trim(key));
        payload.setSuspended(suspended);

        return payload;
    }
}
