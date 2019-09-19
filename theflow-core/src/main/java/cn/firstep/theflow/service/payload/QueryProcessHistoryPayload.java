package cn.firstep.theflow.service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.history.HistoricProcessInstanceQuery;

import java.util.Date;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryProcessHistoryPayload {
    private String processDefId;
    private String processDefKey;
    private String businessKey;
    private String processName;
    private Date startTime;
    private Date endTime;
    private String startUser;
    private String involvedUser;
    private boolean finished;

    public void aplay(HistoricProcessInstanceQuery query) {
        if (StringUtils.isNotEmpty(processDefId)) {
            query.processDefinitionId(processDefId);
        }
        if (StringUtils.isNotEmpty(processDefKey)) {
            query.processDefinitionKey(processDefKey);
        }
        if (StringUtils.isNotEmpty(businessKey)) {
            query.processInstanceBusinessKey(businessKey);
        }
        if (StringUtils.isNotEmpty(processName)) {
            query.processInstanceNameLike(processName);
        }
        if (StringUtils.isNotEmpty(involvedUser)) {
            query.involvedUser(involvedUser);
        }
        if (StringUtils.isNotEmpty(startUser)) {
            query.startedBy(startUser);
        }
        if (finished) {
            query.finished();
        }
        if (startTime != null && endTime != null) {
            query.startedAfter(startTime);
            query.finishedBefore(endTime);
        }
    }
}
