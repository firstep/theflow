package cn.firstep.theflow.model;

import cn.firstep.theflow.entity.ProcessEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Process implements Serializable {
    private static final long serialVersionUID = 3776542751619628964L;

    private String id;
    private String name;
    private String businessKey;
    private String tenantId;
    private String startUser;
    private Date startTime;
    private Date endTime;
    private Long durationMills;
    private String deleteReason;
    private String currTaskId;
    private String currTaskName;

//	private String defineId;
//	private String defineKey;

    private boolean ended;
    private boolean suspended;

    public static Process of(ProcessEntity entity) {
        if(entity == null) {
            return null;
        }

        Process rst = new Process();
        rst.setId(entity.getId());
        rst.setName(entity.getName());
        rst.setBusinessKey(entity.getBusinessKey());
        rst.setTenantId(entity.getTenantId());
        rst.setStartUser(entity.getStartUser());
        rst.setStartTime(entity.getStartTime());
        rst.setSuspended(SuspensionState.SUSPENDED.getStateCode() == entity.getSuspensionState());
        rst.setCurrTaskId(entity.getCurrTaskId());
        rst.setCurrTaskName(entity.getCurrTaskName());
        return rst;
    }

    public static Process of(ProcessInstance inst) {
        if (inst == null) {
            return null;
        }

        Process rst = new Process();
        rst.setId(inst.getProcessInstanceId());
        rst.setName(inst.getName());
        rst.setBusinessKey(inst.getBusinessKey());
        rst.setTenantId(inst.getTenantId());
        rst.setStartUser(inst.getStartUserId());
        rst.setStartTime(inst.getStartTime());
        rst.setEnded(inst.isEnded());
        rst.setSuspended(inst.isSuspended());
        rst.setCurrTaskId(inst.getActivityId());
        rst.setCurrTaskName(((ExecutionEntityImpl) inst).getActivityName());
        return rst;
    }

    public static Process of(HistoricProcessInstance inst) {
        if (inst == null) {
            return null;
        }
        Process rst = new Process();
        rst.setId(inst.getId());
        rst.setName(inst.getName());
        rst.setBusinessKey(inst.getBusinessKey());
        rst.setTenantId(inst.getTenantId());
        rst.setStartUser(inst.getStartUserId());
        rst.setStartTime(inst.getStartTime());
        rst.setEndTime(inst.getEndTime());
        rst.setDurationMills(inst.getDurationInMillis());
        rst.setDeleteReason(inst.getDeleteReason());
        rst.setEnded(inst.getEndTime() != null);
        rst.setSuspended(Boolean.FALSE);

        return rst;
    }
}
