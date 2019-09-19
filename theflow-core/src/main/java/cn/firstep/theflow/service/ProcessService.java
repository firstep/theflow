package cn.firstep.theflow.service;

import cn.firstep.theflow.service.payload.QueryProcessHistoryPayload;
import cn.firstep.theflow.service.payload.QueryProcessPayload;
import cn.firstep.theflow.service.payload.StartProcessPayload;
import cn.firstep.theflow.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Process Service.
 *
 * @author Alvin4u
 */
public interface ProcessService extends BasicService {

    /**
     * 查询历史流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> history(Pageable pageable, QueryProcessHistoryPayload payload);

    /**
     * 查询正在执行的流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> running(Pageable pageable, QueryProcessPayload payload);

    /**
     * 查询自己待处理的流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> todo(Pageable pageable, QueryProcessPayload payload);

    /**
     * 查询自己待领取的流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> unclaimed(Pageable pageable, QueryProcessPayload payload);

    /**
     * 查询自己已处理的流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> finished(Pageable pageable, QueryProcessHistoryPayload payload);

    /**
     * 查询自己发起的流程
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Process> started(Pageable pageable, QueryProcessHistoryPayload payload);

    /**
     * 启动流程
     *
     * @param payload
     * @return
     */
    Process start(StartProcessPayload payload);

    /**
     * 删除流程
     *
     * @param processInstanceId
     * @param reason
     */
    void delete(String processInstanceId, String reason);

    void deleteHistory(String processInstanceId);

    /**
     * 更改流程状态，挂机或激活
     *
     * @param processInstanceId
     * @param isActive
     */
    void state(String processInstanceId, boolean isActive);

    /**
     * 获取当前流程执行图示
     *
     * @param processInstanceId
     * @return
     */
    byte[] diagram(String processInstanceId);

}
