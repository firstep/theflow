package cn.firstep.theflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.common.engine.impl.db.SuspensionState;

import java.util.Date;

/**
 * @author Alvin4u
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessEntity {
    private String id;
    private String name;
    private String businessKey;
    private String tenantId;
    private String startUser;
    private Date startTime;
    private String currTaskId;
    private String currTaskName;

    protected int suspensionState = SuspensionState.ACTIVE.getStateCode();
}
