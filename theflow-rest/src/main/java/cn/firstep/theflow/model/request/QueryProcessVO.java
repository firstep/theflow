package cn.firstep.theflow.model.request;

import cn.firstep.theflow.service.payload.QueryProcessHistoryPayload;
import cn.firstep.theflow.service.payload.QueryProcessPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryProcessVO {
    @Size(max = 255, message = "{def.id.max}")
    private String defineId;

    @Size(max = 255, message = "{def.key.max}")
    private String defineKey;

    @Size(max = 255, message = "{process.key.max}")
    private String businessKey;

    @Size(max = 255, message = "{process.name.max}")
    private String processName;

    @Size(max = 255, message = "{process.startUser.max}")
    private String startUser;

    private Date startTime;
    private Date endTime;

    public QueryProcessPayload toQueryProcessPayload() {
        return QueryProcessPayload.create()
                .processDefinitionId(StringUtils.trim(defineId))
                .processDefinitionKey(StringUtils.trim(defineKey))
                .processName(StringUtils.trim(processName))
                .startUser(StringUtils.trim(startUser))
                .date(startTime, endTime);
    }

    public QueryProcessHistoryPayload toQueryProcessHistoryPayload() {
        QueryProcessHistoryPayload payload = new QueryProcessHistoryPayload();
        payload.setProcessDefId(StringUtils.trim(defineId));
        payload.setProcessDefKey(StringUtils.trim(defineKey));
        payload.setBusinessKey(StringUtils.trim(businessKey));
        payload.setProcessName(StringUtils.trim(processName));
        payload.setStartUser(StringUtils.trim(startUser));
        if (startTime != null && endTime != null) {
            payload.setStartTime(startTime);
            payload.setEndTime(endTime);
        }

        return payload;
    }
}
