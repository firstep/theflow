package cn.firstep.theflow.api;

import cn.firstep.theflow.common.Permission;
import cn.firstep.theflow.model.request.TaskVO;
import cn.firstep.theflow.model.request.TaskWithFormVO;
import cn.firstep.theflow.service.TaskService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.form.api.FormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Task API.
 *
 * @author Alvin4u
 */
@RestController
@RequestMapping("flow/tasks")
public class TaskApi {

    @Autowired
    private TaskService taskService;

    @PutMapping("{id}/complate")
    public void complateTask(@PathVariable String id, @Validated  @RequestBody TaskWithFormVO payload) {
        if(payload.getFormData() == null || payload.getFormData().isEmpty()) {
            taskService.complate(id, payload.getOpinion(), payload.getVariables());
        } else {
            taskService.complate(id, payload.getOpinion(), payload.getVariables(), payload.getOutcome(), payload.getFormData());
        }
    }

    @PutMapping("{id}/claim")
    public void claimTask(@PathVariable String id) {
        taskService.claim(id);
    }

    @PutMapping("{id}/assignee")
    public void assigneeTask(@PathVariable String id, @RequestParam(required = false) String userId) {
        taskService.assignee(id, userId);
    }

    @PutMapping("{id}/delegate")
    public void delegateTask(@PathVariable String id, @RequestParam String userId) {
        taskService.delegate(id, userId);
    }

    @PutMapping("{id}/variable")
    public void setVar(@PathVariable String id, @RequestBody Map<String, Object> params) {
        taskService.variable(id, params);
    }

    @RequiresPermissions(Permission.PERMISSION_TASK_EDIT)
    @DeleteMapping("{id}")
    public void deleteTask(@PathVariable String id) {

    }

    @PutMapping("{id}/back")
    public void backTask(@PathVariable String id, @Validated @RequestBody TaskVO vo) {
        taskService.back(id, vo.getOpinion(), vo.getVariables());
    }

    @GetMapping("{id}/form")
    public FormModel form(@PathVariable String id) {
        return taskService.form(id);
    }
}
