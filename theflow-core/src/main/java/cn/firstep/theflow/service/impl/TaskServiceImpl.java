package cn.firstep.theflow.service.impl;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.FormCode;
import cn.firstep.theflow.common.code.TaskCode;
import cn.firstep.theflow.provider.UserProvider;
import cn.firstep.theflow.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Task Service Implement.
 *
 * @author Alvin4u
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private org.flowable.engine.TaskService taskService;

    @Autowired
    private RepositoryService repoService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Transactional
    @Override
    public void complate(String id, String opinion, Map<String, Object> variables) {
        this.complate(id, opinion, variables, null, null);
    }

    @Transactional
    @Override
    public void complate(String id, String opinion, Map<String, Object> variables, String outcome, Map<String, Object> formData) {
        Task task = getTask(id, userProvider.getId());

        DelegationState state = task.getDelegationState();
        Map<String, Object> params = variables == null || variables.isEmpty() ? null : variables;
        if (StringUtils.isNotEmpty(opinion)) {
            taskService.addComment(id, null, opinion);
        }
        try {
            if (state == DelegationState.PENDING) {
                taskService.resolveTask(id, params);
            } else if (StringUtils.isNotEmpty(task.getFormKey()) && formData != null) {
                this.complateWithForm(task, variables, outcome, formData);
            } else {
                taskService.complete(id, params);
            }
        } catch (Exception e) {
            LOGGER.error("complate task error.", e);
            throw AppException.of(TaskCode.PROCESS_ERROR, e.getMessage());
        }
    }

    private void complateWithForm(Task task, Map<String, Object> variables, String outcome, Map<String, Object> formData) {
        FormInfo form = taskService.getTaskFormModel(task.getId(), Boolean.TRUE);

        if (form == null) {
            throw AppException.of(FormCode.NOT_FOUND_DEF);
        }

        taskService.completeTaskWithForm(task.getId(), form.getId(), outcome, formData, variables);
    }

    @Transactional
    @Override
    public void back(String id, String opinion, Map<String, Object> variables) {
        Task task = getTask(id, userProvider.getId());
        BpmnModel bpmnModel = repoService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode node = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());

        Map<String, Object> vars = taskService.getVariables(id);
        ExpressionFactory expFactiory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();

        vars.keySet().forEach(k -> {
            context.setVariable(k, expFactiory.createValueExpression(vars.get(k), vars.get(k).getClass()));
        });
        List<String> activitys = new ArrayList<>();
        getPrevNodes(node, expFactiory, context, activitys);
        if (activitys.isEmpty()) {
            return;
        }

        taskService.addComment(id, null, opinion);
        if (activitys.size() > 1) {
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveSingleActivityIdToActivityIds(task.getTaskDefinitionKey(), activitys)
                    .processVariables(variables == null ? Collections.EMPTY_MAP : variables)
                    .changeState();
        } else {
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(task.getProcessInstanceId()).list();
            List<String> execIds = executions.stream().map(exec -> exec.getId()).collect(Collectors.toList());
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveExecutionsToSingleActivityId(execIds, activitys.get(0))
                    .processVariables(variables == null ? Collections.EMPTY_MAP : variables)
                    .changeState();
        }

    }

    private void getPrevNodes(FlowNode node, ExpressionFactory expFactiory, SimpleContext context, List<String> activites) {
        node.getIncomingFlows().forEach(flow -> {
            FlowElement sourceFlow = flow.getSourceFlowElement();
            if (sourceFlow instanceof UserTask) {
                String condExp = ((UserTask) sourceFlow).getIncomingFlows().get(0).getConditionExpression();
                if (condExp == null || (Boolean) expFactiory.createValueExpression(context, condExp, boolean.class).getValue(context)) {
                    activites.add(sourceFlow.getId());
                }
            } else if (!(sourceFlow instanceof StartEvent)) {
                getPrevNodes((FlowNode) sourceFlow, expFactiory, context, activites);
            }
        });
    }

    private void getNextNodes(FlowNode node, ExpressionFactory expFactiory, SimpleContext context, List<FlowNode> nodes) {
        node.getOutgoingFlows().forEach(flow -> {
            if (flow.getConditionExpression() != null) {
                if (!(Boolean) expFactiory.createValueExpression(context, flow.getConditionExpression(), boolean.class).getValue(context)) {
                    return;
                }
            }
            FlowElement targetFlow = flow.getTargetFlowElement();
            if (targetFlow instanceof UserTask) {
                nodes.add((FlowNode) targetFlow);
            } else if (!(targetFlow instanceof EndEvent)) {
                getNextNodes((FlowNode) targetFlow, expFactiory, context, nodes);
            }
        });
    }

    @Transactional
    @Override
    public void claim(String id) {
        Task task = getTask(id, null);
        if (task.getAssignee() == null) {
            taskService.claim(id, userProvider.getId());
        } else {
            throw AppException.of(TaskCode.HAS_CLAIMED, task.getAssignee());
        }
    }

    @Transactional
    @Override
    public void assignee(String id, String assignee) {
        Task task = getTask(id, null);
        if (StringUtils.isEmpty(assignee)) {
            taskService.setAssignee(id, null);
        } else {
            if (task.getOwner() == null && task.getAssignee() != null) {
                taskService.setOwner(id, userProvider.getId());
            }
            taskService.setAssignee(id, assignee);
        }
    }

    @Transactional
    @Override
    public void delegate(String id, String assignee) {
        Task task = getTask(id, userProvider.getId());
        if (DelegationState.PENDING == task.getDelegationState()) {
            throw AppException.of(TaskCode.HAS_DELEGATED);
        }

        taskService.delegateTask(id, assignee);

    }

    @Transactional
    @Override
    public void variable(String id, Map<String, Object> variables) {
        Task task = getTask(id, userProvider.getId());
        Map<String, Object> target = task.getTaskLocalVariables();
        target.putAll(variables);
        taskService.setVariablesLocal(id, target);
    }

    private Task getTask(String id, String assignee) {
        TaskQuery query = taskService.createTaskQuery().taskId(id);
        if (userProvider.hasTenantId()) {
            query.taskTenantId(userProvider.getTenantId());
        } else {
            query.taskWithoutTenantId();
        }
        if (StringUtils.isNotEmpty(assignee) && !userProvider.getUser().isManager()) {
            query.taskAssignee(assignee);
        }
        Task task = query.singleResult();
        if (task == null) {
            throw AppException.of(TaskCode.NOT_FOUND);
        }
        return task;
    }

    @Override
    public Page<cn.firstep.theflow.model.Task> historyTasks(Pageable pageable, String processId) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().processInstanceId(processId).orderByTaskCreateTime().asc();
        if (StringUtils.isNotEmpty(userProvider.getTenantId())) {
            query.taskTenantId(userProvider.getTenantId());
        }

        return doQuery(query, pageable, (list) -> list.stream().map(cn.firstep.theflow.model.Task::of).collect(Collectors.toList()));
    }

    @Override
    public Page<cn.firstep.theflow.model.Task> runningTasks(Pageable pageable, String processId) {
        TaskQuery query = taskService.createTaskQuery().processInstanceId(processId).orderByTaskCreateTime().asc();
        if (StringUtils.isNotEmpty(userProvider.getTenantId())) {
            query.taskTenantId(userProvider.getTenantId());
        }
        return doQuery(query, pageable, (list) -> list.stream().map(cn.firstep.theflow.model.Task::of).collect(Collectors.toList()));
    }

    @Override
    public FormModel form(String id) {
        Task task = getTask(id, userProvider.getId());

        if(StringUtils.isEmpty(task.getFormKey())) {
            return null;
        }

        FormInfo form = taskService.getTaskFormModel(task.getId());

        return form.getFormModel();
    }
}
