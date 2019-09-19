package cn.firstep.theflow.service;

import cn.firstep.theflow.service.payload.QueryDefinitionPayload;
import cn.firstep.theflow.model.Definition;
import org.flowable.form.api.FormModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;
import java.util.List;

/**
 * Definition Service.
 *
 * @author Alvin4u
 */
public interface DefinitionService extends BasicService {

    /**
     * 根据key获取最新版本的流程定义
     *
     * @param key
     * @return
     */
    Definition last(String key);

    /**
     * 流程定义查询
     *
     * @param pageable
     * @param payload
     * @return
     */
    Page<Definition> list(Pageable pageable, QueryDefinitionPayload payload);

    /**
     * 根据key获取最新版本的bpmn定义文件
     *
     * @param id
     * @return
     */
    byte[] resource(String id);

    /**
     * 根据key获取最新版本流程定义图
     *
     * @param id
     * @return
     */
    byte[] diagram(String id);

    /**
     * 上传bpmn文件部署流程定义
     *
     * @param fileName
     * @param inputStream
     * @return
     * @deprecated 用设计器设计的模型进行导入-发布
     */
    @Deprecated
    List<Definition> deploy(String fileName, InputStream inputStream);

    /**
     * 通过设计器设计的模型ID部署流程定义
     *
     * @param modelId
     * @return
     */
    Definition deploy(String modelId);

    /**
     * 表单部署
     *
     * @param processDefinitionId
     * @param modelId
     * @return
     */
    String deployForm(String processDefinitionId, String modelId);

    /**
     * 获取流程启动表单
     *
     * @param processDefinitionId
     * @return
     */
    FormModel startForm(String processDefinitionId);

    /**
     * 删除流程定义
     *
     * @param id
     * @param delRunningInstance
     */
    void delete(String id, boolean delRunningInstance);

    /**
     * 激活或挂起流程定义
     *
     * @param id
     * @param isActive
     */
    void state(String id, boolean isActive);

}
