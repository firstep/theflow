package cn.firstep.theflow.service.impl;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.code.DefinitionCode;
import cn.firstep.theflow.common.code.FormCode;
import cn.firstep.theflow.provider.UserProvider;
import cn.firstep.theflow.repository.FormRelationRepository;
import cn.firstep.theflow.service.payload.QueryDefinitionPayload;
import cn.firstep.theflow.entity.FormRelationEntity;
import cn.firstep.theflow.model.Definition;
import cn.firstep.theflow.service.DefinitionService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.form.api.*;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

/**
 * Definition Service Implement.
 *
 * @author Alvin4u
 */
@Service
public class DefinitionServiceImpl implements DefinitionService {

    private static Logger LOGGER = LoggerFactory.getLogger(DefinitionServiceImpl.class);

    @Autowired
    private UserProvider userProvider;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repoService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ModelRepository modeRepo;

    @Autowired
    private FormRepositoryService formRepoService;

    @Autowired
    private FormRelationRepository formRelRepo;

    @Autowired
    private FormService formService;

    @Override
    public Definition last(String key) {
        return Definition.of(getLastByKey(key));
    }

    @Override
    public Page<Definition> list(Pageable pageable, QueryDefinitionPayload payload) {
        ProcessDefinitionQuery query = getQuery().orderByProcessDefinitionKey().asc();
        if (payload != null) {
            payload.aplay(query);
        }

        return doQuery(query, pageable, (s) -> s.stream().map(Definition::of).collect(Collectors.toList()));
    }

    @Override
    public byte[] resource(String id) {
//		repoService.getProcessModel()
        return getResource(id, Boolean.FALSE);
    }

    @Override
    public byte[] diagram(String id) {
//		repoService.getProcessDiagram()
        return getResource(id, Boolean.TRUE);
    }

    private byte[] getResource(String id, boolean isDiagram) {
        ProcessDefinition procDef = get(id, null);
        InputStream is = repoService.getResourceAsStream(procDef.getDeploymentId(), isDiagram ? procDef.getDiagramResourceName() : procDef.getResourceName());
        try {
            return IoUtil.readInputStream(is, "DEFINITION#"+(isDiagram ? "DIAGRAM#" : "RESOURCE#") + id);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        } finally {
            IoUtil.closeSilently(is);
        }
    }

    @Transactional
    @Override
    public List<Definition> deploy(String fileName, InputStream inputStream) {
        DeploymentBuilder builder = repoService.createDeployment().tenantId(userProvider.getTenantId());
        List<Definition> rst;
        try {
            if (fileName.endsWith(".zip")) {
                builder.addZipInputStream(new ZipInputStream(inputStream));
            } else {
                builder.addInputStream(fileName, inputStream);
            }

            Deployment deploy = builder.deploy();

            List<ProcessDefinition> defs = repoService.createProcessDefinitionQuery().deploymentId(deploy.getId()).list();

            defs.forEach(def -> {
                repoService.suspendProcessDefinitionById(def.getId());
            });

            rst = defs.stream().map(Definition::of).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("deploy process except.", e);
            throw AppException.of(DefinitionCode.DEPLOY_FAILD);
        }

        if (rst.isEmpty()) {
            throw AppException.of(DefinitionCode.DEPLOY_FAILD);
        }
        return rst;
    }

    @Transactional
    @Override
    public Definition deploy(String modelId) {
        Model model = modeRepo.get(modelId);
        if (model == null || Model.MODEL_TYPE_BPMN != model.getModelType()) {
            throw AppException.of(DefinitionCode.NOT_FOUND_MODEL);
        }

        BpmnModel bpmn = modelService.getBpmnModel(model);
        if (bpmn.getProcesses().isEmpty()) {
            throw AppException.of(DefinitionCode.NO_PROCESS_DEFINE);
        }

        try {
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmn);
            if (bpmnBytes == null || bpmnBytes.length == 0) {
                throw AppException.of(DefinitionCode.NO_PROCESS_DEFINE);
            }
            DeploymentBuilder deploymentBuilder = repoService.createDeployment().tenantId(userProvider.getTenantId())
                    .name(model.getName())
                    .addBytes(model.getName() + ".bpmn20.xml", bpmnBytes);

            //attach form resource
            List<Model> formModels = modeRepo.findByParentModelId(modelId)
                    .stream().filter(item -> Model.MODEL_TYPE_FORM == item.getModelType())
                    .collect(Collectors.toList());
            formModels.forEach(form -> {
                deploymentBuilder.addString(form.getKey(), form.getModelEditorJson());
            });

            Deployment deploy = deploymentBuilder.deploy();
            ProcessDefinition def = repoService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
            if (def == null) {
                throw AppException.of(DefinitionCode.DEPLOY_FAILD);
            }

            repoService.suspendProcessDefinitionById(def.getId());

            return Definition.of(def);
        } catch (Exception e) {
            LOGGER.error("deploy process except.", e);
            throw AppException.of(DefinitionCode.DEPLOY_ERROR);
        }
    }

    private FormDeploymentQuery getDefaultFormDeploymentQuery(String processDeploymentId) {
        FormDeploymentQuery deploymentQuery = formRepoService.createDeploymentQuery()
                .parentDeploymentId(processDeploymentId);
        if (StringUtils.isNotEmpty(userProvider.getTenantId())) {
            deploymentQuery.deploymentTenantId(userProvider.getTenantId());
        }
        return deploymentQuery;
    }

    private FormDefinition deployForm(Model model, ProcessDefinition processDef) {
        String resName = model.getName();
        FormDeployment deploy = formRepoService.createDeployment()
                .addFormDefinition(resName.endsWith(".form") ? resName : resName + ".form", model.getModelEditorJson())
                .parentDeploymentId(processDef.getDeploymentId())
                .tenantId(userProvider.getTenantId())
                .deploy();

        FormDefinition formDef = formRepoService.createFormDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        if (formDef == null) {
            throw AppException.of(FormCode.NOT_FOUND_DEF);
        }

        formRelRepo.save(new FormRelationEntity(processDef.getId(), formDef.getId()));

        return formDef;
    }

    @Transactional
    public String deployForm(String processDefinitionId, String modelId) {
        Model model = modelService.getModel(modelId);
        if (model == null || Model.MODEL_TYPE_FORM != model.getModelType()) {
            throw AppException.of(FormCode.NOT_FOUND_MODEL);
        }

        //Process and form can only be associated once
        FormDefinition formDefinition = formRelRepo.findFormDefinitionByKeyAndProcessDefinitionId(model.getKey(), processDefinitionId);
        if (formDefinition != null) {
            throw AppException.of(FormCode.NOT_FOUND_DEF);
        }

        ProcessDefinition processDefinition = repoService.getProcessDefinition(processDefinitionId);
        if (processDefinition == null) {
            throw AppException.of(DefinitionCode.NOT_FOUND);
        }

        FormDefinition def;
        try {
            def = deployForm(model, processDefinition);
            return def.getId();
        } catch (Exception e) {
            if (e instanceof AppException) {
                throw e;
            } else {
                LOGGER.error("deploy form error.", e);
                throw AppException.of(FormCode.DEPLOY_ERROR);
            }
        }
    }

    @Override
    public FormModel startForm(String processDefinitionId) {
        ProcessDefinition processDefinition = get(processDefinitionId, null);
        if (!processDefinition.hasStartFormKey()) {
            throw AppException.of(DefinitionCode.NO_START_FORM);
        }

        BpmnModel bpmnModel = repoService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcessById(processDefinition.getKey());
        FlowElement startElement = process.getInitialFlowElement();
        if (startElement instanceof StartEvent) {
            String formKey = ((StartEvent) startElement).getFormKey();
            FormDefinition formDefinition = formRelRepo.findFormDefinitionByKeyAndProcessDefinitionId(formKey, processDefinition.getId());
            if (formDefinition == null) {
                throw AppException.of(FormCode.NOT_FOUND_DEF);
            }
            FormInfo formInfo = formRepoService.getFormModelById(formDefinition.getId());
            return formInfo.getFormModel();
        }
        return null;
    }

    @Transactional
    @Override
    public void delete(String id, boolean delRunningInstance) {
        ProcessDefinition def = get(id, null);

        repoService.deleteDeployment(def.getDeploymentId(), delRunningInstance);
        if (delRunningInstance) {
            getDefaultFormDeploymentQuery(def.getDeploymentId()).list().forEach(deploy -> {
                formRepoService.deleteDeployment(deploy.getId());
                formRelRepo.deleteByProcessDefinitionId(def.getId());
            });
        }
    }

    @Transactional
    @Override
    public void state(String id, boolean isActive) {
        ProcessDefinition def = get(id, null);

        if (isActive) {
            checkForm(def);
            repoService.activateProcessDefinitionById(id, true, null);
        } else {
            repoService.suspendProcessDefinitionById(id, true, null);
        }
    }

    private ProcessDefinitionQuery getQuery() {
        ProcessDefinitionQuery processDefinitionQuery = repoService.createProcessDefinitionQuery();
        if (StringUtils.isEmpty(userProvider.getTenantId())) {
            processDefinitionQuery.processDefinitionWithoutTenantId();
        } else {
            processDefinitionQuery.processDefinitionTenantId(userProvider.getTenantId());
        }
        return processDefinitionQuery;
    }

    private ProcessDefinition getLastByKey(String key) {
        ProcessDefinitionQuery query = getQuery();
        query.processDefinitionKey(key).latestVersion();
        return query.singleResult();
    }

    private ProcessDefinition get(String id, String key) {
        ProcessDefinition processDefinition = null;
        if (StringUtils.isNotEmpty(id)) {
            processDefinition = repoService.getProcessDefinition(id);
            //olny current tenant accessable.
            if (StringUtils.isNotEmpty(userProvider.getTenantId())
                    && !userProvider.getTenantId().equals(processDefinition.getTenantId())) {
                processDefinition = null;
            }
        } else if (StringUtils.isNotEmpty(key)) {
            processDefinition = getLastByKey(key);
        }
        if (processDefinition == null) {
            throw AppException.of(DefinitionCode.NOT_FOUND);
        }
        return processDefinition;
    }

    private List<String> formKeys(ProcessDefinition def) {
        BpmnModel bpmnModel = repoService.getBpmnModel(def.getId());
        Process process = bpmnModel.getMainProcess();
        List<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class, true);
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class, true);

        return Stream.of(startEvents, userTasks).flatMap(list -> list.stream().map(node -> {
            if (node instanceof StartEvent) {
                return ((StartEvent) node).getFormKey();
            } else {
                return ((UserTask) node).getFormKey();
            }
        })).filter(item -> StringUtils.isNotEmpty(item)).distinct().collect(Collectors.toList());
    }

    private void checkForm(ProcessDefinition def) {
        List<String> formKeys = formKeys(def);
        List<String> keys = formRelRepo.findFormDefinitionByProcessDefinitionId(def.getId()).stream().map(item -> item.getKey()).collect(Collectors.toList());
        if (formKeys.size() != keys.size()) {
            throw AppException.of(DefinitionCode.NO_FORM_RELATION);
        }
    }
}
