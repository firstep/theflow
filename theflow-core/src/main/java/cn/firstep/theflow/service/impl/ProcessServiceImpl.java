package cn.firstep.theflow.service.impl;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.Pagings;
import cn.firstep.theflow.common.code.ProcessCode;
import cn.firstep.theflow.provider.UserProvider;
import cn.firstep.theflow.repository.ProcessRepository;
import cn.firstep.theflow.repository.criteria.QueryProcessCriteria;
import cn.firstep.theflow.service.payload.QueryProcessHistoryPayload;
import cn.firstep.theflow.service.payload.QueryProcessPayload;
import cn.firstep.theflow.service.payload.StartProcessPayload;
import cn.firstep.theflow.entity.ProcessEntity;
import cn.firstep.theflow.model.Process;
import cn.firstep.theflow.service.ProcessService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormService;
import org.flowable.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Process Service Implement.
 *
 * @author Alvin4u
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    private static Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repoService;

    @Autowired
    private ProcessEngineConfiguration engineConfig;

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private FormService formService;

    @Autowired
    private ProcessRepository processRepo;

    @Override
    public Page<Process> history(Pageable pageable, QueryProcessHistoryPayload payload) {
        if (payload == null) {
            payload = new QueryProcessHistoryPayload();
        }
        payload.setFinished(Boolean.TRUE);
        return queryHistory(pageable, payload);
    }

    @Override
    public Page<Process> finished(Pageable pageable, QueryProcessHistoryPayload payload) {
        if (payload == null) {
            payload = new QueryProcessHistoryPayload();
        }
        payload.setInvolvedUser(userProvider.getId());
        return queryHistory(pageable, payload);
    }

    @Override
    public Page<Process> started(Pageable pageable, QueryProcessHistoryPayload payload) {
        if (payload == null) {
            payload = new QueryProcessHistoryPayload();
        }
        payload.setStartUser(userProvider.getId());
        return queryHistory(pageable, payload);
    }

    private Page<Process> queryHistory(Pageable pageable, QueryProcessHistoryPayload payload) {
        HistoricProcessInstanceQuery query = getTenantHistoricQuery().orderByProcessInstanceStartTime().desc();
        if (payload != null) {
            payload.aplay(query);
        }
        return doQuery(query, pageable, (s) -> s.stream().map(Process::of).collect(Collectors.toList()));
    }

    private HistoricProcessInstanceQuery getTenantHistoricQuery() {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (userProvider.hasTenantId()) {
            query.processInstanceTenantId(userProvider.getTenantId());
        } else {
            query.processInstanceWithoutTenantId();
        }
        return query;
    }

    @Override
    public Page<Process> running(Pageable pageable, QueryProcessPayload payload) {
        QueryProcessCriteria criteria = QueryProcessCriteria.of(pageable, payload, userProvider.getTenantId());
        return doQuery(pageable, criteria);
    }

    @Override
    public Page<Process> todo(Pageable pageable, QueryProcessPayload payload) {
        QueryProcessCriteria criteria = QueryProcessCriteria.of(pageable, payload, userProvider.getTenantId());
        criteria.setUser(userProvider.getId());
        return doQuery(pageable, criteria);
    }

    @Override
    public Page<Process> unclaimed(Pageable pageable, QueryProcessPayload payload) {
        QueryProcessCriteria criteria = QueryProcessCriteria.of(pageable, payload, userProvider.getTenantId());
        criteria.setUser(userProvider.getId());
        criteria.setGroups(Arrays.asList(userProvider.getRoles()));
        return doQuery(pageable, criteria);
    }

    private Page<Process> doQuery(Pageable pageable, QueryProcessCriteria criteria) {
        long total = processRepo.selectProcessCountByQueryCriteria(criteria);
        List<Process> rst = null;
        if (total > 0) {
            List<ProcessEntity> processes = processRepo.selectProcessByQueryCriteria(criteria);
            rst = processes.stream().map(Process::of).collect(Collectors.toList());
        }
        return Pagings.pack(rst, pageable, total);
    }

    @Transactional
    @Override
    public Process start(StartProcessPayload payload) {
        payload.validate();
        try {
            Authentication.setAuthenticatedUserId(userProvider.getId());
            ProcessInstance inst = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionId(payload.getProcessDefId())
                    .processDefinitionKey(payload.getProcessDefKey())
                    .tenantId(userProvider.getTenantId())
                    .name(payload.getProcessName())
                    .businessKey(payload.getBusinessKey())
                    .variables(payload.getVariables())
                    .outcome(payload.getOutcome())
                    .startFormVariables(payload.getFormData())
                    .start();

            return Process.of(inst);
        } catch (Exception e) {
            LOGGER.error("Process start error.", e);
            throw AppException.of(ProcessCode.START_ERROR, e.getMessage());
        } finally {
            Authentication.setAuthenticatedUserId(null);
        }
    }

    @Transactional
    @Override
    public void delete(String processInstanceId, String reason) {
        HistoricProcessInstance inst = getTenantHistoricQuery().processInstanceId(processInstanceId).singleResult();
        if (inst == null) {
            return;
        }

        runtimeService.deleteProcessInstance(inst.getId(), reason);
    }

    @Transactional
    @Override
    public void deleteHistory(String processInstanceId) {
        HistoricProcessInstance inst = getTenantHistoricQuery().processInstanceId(processInstanceId).singleResult();
        if (inst == null) {
            return;
        }
        historyService.deleteHistoricProcessInstance(processInstanceId);
        formService.createFormInstanceQuery().processInstanceId(inst.getId()).list().forEach(form -> {
            formService.deleteFormInstance(form.getId());
        });
    }

    @Transactional
    @Override
    public void state(String processInstanceId, boolean isActive) {
        if (isActive) {
            runtimeService.activateProcessInstanceById(processInstanceId);
        } else {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }
    }

    @Override
    public byte[] diagram(String processInstanceId) {
        HistoricProcessInstance inst = getTenantHistoricQuery().processInstanceId(processInstanceId).singleResult();
        if (inst == null) {
            throw AppException.of(ProcessCode.NOT_FOUND);
        }
        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<String> highLightedActivitis = highLightedActivitList.stream().map(item -> item.getActivityId()).collect(Collectors.toList());
        List<String> flows = new ArrayList<>();
        BpmnModel bpmnModel = repoService.getBpmnModel(inst.getProcessDefinitionId());

        ProcessDiagramGenerator diagramGenerator = engineConfig.getProcessDiagramGenerator();
        InputStream is = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, flows, engineConfig.getActivityFontName(), engineConfig.getLabelFontName(), engineConfig.getAnnotationFontName(), engineConfig.getClassLoader(), 1.0, true);
        try {
            return IoUtil.readInputStream(is, "PROCESS#DIAGRAM#" + processInstanceId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw AppException.of(ProcessCode.DIAGRAM_IS_EMPTY);
        } finally {
            IoUtil.closeSilently(is);
        }
    }

}
