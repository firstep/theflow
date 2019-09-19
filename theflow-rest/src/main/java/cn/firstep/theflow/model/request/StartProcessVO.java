package cn.firstep.theflow.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartProcessVO {
    @Size(max = 255, message = "{def.id.max}")
    private String processDefId;

    @Size(max = 255, message = "{def.key.max}")
    private String processDefKey;

    @Size(max = 255, message = "{process.key.max}")
    private String businessKey;

    @Size(max = 255, message = "{process.name.max}")
    private String processName;

    @Size(max = 255, message = "{form.outcome.max}")
    private String outcome;

    private Map<String, Object> variables;
    private Map<String, Object> formData;
}
