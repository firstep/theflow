package cn.firstep.theflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.Date;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String parentId;
    private String id;
    private String name;
    private String owner;
    private String assignee;
    private String processId;
    private String tenantId;
    private Date createTime;
    private Date claimTime;
    private Date endTime;
    private Long durationMills;
    private String definitionId;
    private boolean suspended;
    private boolean delegated;
    private Date dueDate;
    private String formKey;

    public static Task of(org.flowable.task.api.Task task) {
        Task rst = new Task();
        rst.setParentId(task.getParentTaskId());
        rst.setId(task.getId());
        rst.setName(task.getName());
        rst.setOwner(task.getOwner());
        rst.setAssignee(task.getAssignee());
        rst.setProcessId(task.getProcessInstanceId());
        rst.setTenantId(task.getTenantId());
        rst.setCreateTime(task.getCreateTime());
        rst.setClaimTime(task.getClaimTime());
        rst.setDefinitionId(task.getTaskDefinitionId());
        rst.setSuspended(task.isSuspended());
        rst.setDelegated(DelegationState.PENDING == task.getDelegationState());
        rst.setDueDate(task.getDueDate());
        rst.setFormKey(task.getFormKey());
        return rst;
    }

    public static Task of(HistoricTaskInstance task) {
        Task rst = new Task();
        rst.setParentId(task.getParentTaskId());
        rst.setId(task.getId());
        rst.setName(task.getName());
        rst.setOwner(task.getOwner());
        rst.setAssignee(task.getAssignee());
        rst.setProcessId(task.getProcessInstanceId());
        rst.setTenantId(task.getTenantId());
        rst.setCreateTime(task.getCreateTime());
        rst.setEndTime(task.getEndTime());
        rst.setDurationMills(task.getDurationInMillis());
        rst.setClaimTime(task.getClaimTime());
        rst.setDefinitionId(task.getTaskDefinitionId());
//		rst.setSuspended(Boolean.FALSE);
//		rst.setDelegated(Boolean.FALSE);
        rst.setDueDate(task.getDueDate());
        rst.setFormKey(task.getFormKey());
        return rst;
    }
}
