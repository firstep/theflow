package cn.firstep.theflow.service;

import cn.firstep.theflow.model.Task;
import org.flowable.form.api.FormModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Task Service.
 *
 * @author Alvin4u
 */
public interface TaskService extends BasicService {

    /**
     * 查询流程任务
     *
     * @param pageable
     * @param processId
     * @return
     */
    Page<Task> historyTasks(Pageable pageable, String processId);


    /**
     * 查询流程任务
     *
     * @param pageable
     * @param processId
     * @return
     */
    Page<Task> runningTasks(Pageable pageable, String processId);

    /**
     * 处理任务
     *
     * @param id
     * @param opinion
     * @param variables
     */
    void complate(String id, String opinion, Map<String, Object> variables);

    /**
     * 处理表单类型任务
     *
     * @param id
     * @param opinion
     * @param variables
     * @param outcome
     * @param formData
     */
    void complate(String id, String opinion, Map<String, Object> variables, String outcome, Map<String, Object> formData);

    /**
     * 驳回任务
     *
     * @param id
     * @param opinion
     * @param variables
     */
    void back(String id, String opinion, Map<String, Object> variables);

    /**
     * 拾取任务
     *
     * @param id
     */
    void claim(String id);

    /**
     * 分配任务
     *
     * @param id
     * @param assignee
     */
    void assignee(String id, String assignee);

    /**
     * 转交任务
     *
     * @param id
     * @param assignee
     */
    void delegate(String id, String assignee);

    /**
     * 设置任务变量
     *
     * @param id
     * @param variables
     */
    void variable(String id, Map<String, Object> variables);

    /**
     * 获取任务关联表单
     * @param id
     * @return
     */
    FormModel form(String id);
}
