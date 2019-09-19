package cn.firstep.theflow.repository.criteria;

import cn.firstep.theflow.service.payload.QueryProcessPayload;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.db.ListQueryParameterObject;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

@Getter
public class QueryProcessCriteria extends ListQueryParameterObject {

    private String defId;
    private String defKey;
    private String procName;
    private String startUser;
    private Date startTime;
    private Date endTime;

    private String tenant;

    @Setter
    private String user;

    @Setter
    private List<String> groups;

    private QueryProcessCriteria(){

    }

    public static QueryProcessCriteria of(Pageable pageable, QueryProcessPayload payload, String tenantId) {
        QueryProcessCriteria criteria = new QueryProcessCriteria();
        if(payload != null) {
            criteria.defId = payload.getDefId();
            criteria.defKey = payload.getDefKey();
            criteria.procName = payload.getProcName();
            criteria.startUser = payload.getStartUser();
            criteria.startTime = payload.getStartTime();
            criteria.endTime = payload.getEndTime();
            if(StringUtils.isNotEmpty(payload.getOrderBy())) {
                criteria.orderByColumns = payload.getOrderBy();
            }
        }

        criteria.firstResult = (int) pageable.getOffset();
        criteria.maxResults = pageable.getPageSize();

        criteria.tenant = tenantId;

        return criteria;
    }
}
