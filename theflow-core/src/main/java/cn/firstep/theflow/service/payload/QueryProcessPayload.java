package cn.firstep.theflow.service.payload;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.impl.ProcessInstanceQueryProperty;

import java.util.Date;

@Getter
public class QueryProcessPayload {
    private String defId;
    private String defKey;
    private String procName;
    private String startUser;
    private Date startTime;
    private Date endTime;
    private String orderBy;

    public QueryProcessPayload processDefinitionId(String processDefinitionId) {
        if (StringUtils.isNotEmpty(processDefinitionId)) {
            this.defId = processDefinitionId;
        }
        return this;
    }

    public QueryProcessPayload processDefinitionKey(String processDefinitionKey) {
        if(StringUtils.isNotEmpty(processDefinitionKey)) {
            this.defKey = processDefinitionKey;
        }
        return this;
    }

    /**
     * @param processName cannot be null. The string can include the wildcard character '%' to express like-strategy: starts with (string%), ends with (%string) or contains (%string%).
     * @return
     */
    public QueryProcessPayload processName(String processName) {
        if(StringUtils.isNotEmpty(processName)) {
            this.procName = processName;
        }
        return this;
    }

    public QueryProcessPayload startUser(String startUser) {
        if(StringUtils.isNotEmpty(startUser)) {
            this.startUser = startUser;
        }
        return this;
    }

    public QueryProcessPayload date(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return this;
        }
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    public QueryProcessPayload orderByProcessDefinitionId() {
        this.orderBy = ProcessInstanceQueryProperty.PROCESS_DEFINITION_ID.getName();
        return this;
    }

    public QueryProcessPayload orderByProcessDefinitionKey() {
        this.orderBy = ProcessInstanceQueryProperty.PROCESS_DEFINITION_KEY.getName();
        return this;
    }

    public QueryProcessPayload orderById() {
        this.orderBy = ProcessInstanceQueryProperty.PROCESS_INSTANCE_ID.getName();
        return this;
    }

    public QueryProcessPayload orderByStartTim() {
        this.orderBy = ProcessInstanceQueryProperty.PROCESS_START_TIME.getName();
        return this;
    }

    public static QueryProcessPayload create() {
        return new QueryProcessPayload();
    }
}
