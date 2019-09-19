package cn.firstep.theflow.service.payload;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.ProcessCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author Alvin4u
 */
@Getter
public class StartProcessPayload {
    private String processDefId;
    private String processDefKey;
    private String businessKey;
    private String processName;
    private String outcome;
    private Map<String, Object> formData;
    private Map<String, Object> variables;

    public StartProcessPayload form(String outcome, Map<String, Object> formData) {
        this.outcome = outcome;
        this.formData = formData == null || formData.isEmpty() ? null : formData;
        return this;
    }

    public StartProcessPayload processDefinitionId(String processDefinitionId) {
        this.processDefId = processDefinitionId;
        this.processDefKey = null;
        return this;
    }

    public StartProcessPayload processDefinitionKey(String processDefinitionKey) {
        if (StringUtils.isEmpty(this.processDefId)) {
            this.processDefKey = processDefinitionKey;
        }
        return this;
    }

    public StartProcessPayload businessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public StartProcessPayload processName(String processName) {
        this.processName = processName;
        return this;
    }

    public StartProcessPayload variables(Map<String, Object> variables) {
        this.variables = variables == null || variables.isEmpty() ? null : variables;
        return this;
    }

    public StartProcessPayload validate() {
        if (StringUtils.isAllEmpty(processDefId, processDefKey)) {
            throw AppException.of(ProcessCode.ILLEGAL_ARGS);
        }

        return this;
    }

    public static StartProcessPayload create() {
        return new StartProcessPayload();
    }
}
