package cn.firstep.theflow.api;

import cn.firstep.theflow.common.Permission;
import cn.firstep.theflow.common.ResponseEntitys;
import cn.firstep.theflow.model.Process;
import cn.firstep.theflow.model.Task;
import cn.firstep.theflow.model.request.PagingVO;
import cn.firstep.theflow.model.request.QueryProcessVO;
import cn.firstep.theflow.model.request.StartProcessVO;
import cn.firstep.theflow.model.response.PagingResponse;
import cn.firstep.theflow.service.ProcessService;
import cn.firstep.theflow.service.TaskService;
import cn.firstep.theflow.service.payload.QueryProcessHistoryPayload;
import cn.firstep.theflow.service.payload.StartProcessPayload;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Process API.
 *
 * @author Alvin4u
 */
@RestController
@RequestMapping("flow/process")
public class ProcessApi {
    @Autowired
    private ProcessService processService;

    @Autowired
    private TaskService taskService;

    private enum STATE {
        active,
        suspend
    }

    @RequiresPermissions(Permission.PERMISSION_PROCESS_ACCESS)
    @GetMapping("history")
    public PagingResponse<?> history(@Validated PagingVO paging, @Validated QueryProcessVO payload) {
        QueryProcessHistoryPayload query = payload.toQueryProcessHistoryPayload();
        Page<Process> page = processService.history(paging.toPageable(), query);
        return PagingResponse.of(page);
    }

    @RequiresPermissions(Permission.PERMISSION_PROCESS_ACCESS)
    @GetMapping("running")
    public PagingResponse<?> running(@Validated PagingVO paging, @Validated QueryProcessVO payload) {
        Page<Process> page = processService.running(paging.toPageable(), payload.toQueryProcessPayload());
        return PagingResponse.of(page);
    }

    @GetMapping("todo")
    public PagingResponse<?> getTodo(@Validated PagingVO paging, @Validated QueryProcessVO payload) {
        Page<Process> page = processService.todo(paging.toPageable(), payload.toQueryProcessPayload());
        return PagingResponse.of(page);
    }

    @GetMapping("unclaimed")
    public PagingResponse<?> getUnclaimed(@Validated PagingVO paging, @Validated QueryProcessVO payload) {
        Page<Process> page = processService.unclaimed(paging.toPageable(), payload.toQueryProcessPayload());
        return PagingResponse.of(page);
    }

    @GetMapping("started")
    public PagingResponse<?> getStarted(@Validated PagingVO paging) {
        Page<Process> page = processService.started(paging.toPageable(), null);
        return PagingResponse.of(page);
    }

    @GetMapping("involved")
    public PagingResponse<?> getFinished(@Validated PagingVO paging) {
        Page<Process> page = processService.finished(paging.toPageable(), null);
        return PagingResponse.of(page);
    }

    @GetMapping("{id}/tasks/running")
    public PagingResponse<?> runningTasks(@PathVariable String id, @Validated PagingVO paging) {
        Page<Task> page = taskService.runningTasks(paging.toPageable(), id);

        return PagingResponse.of(page);
    }

    @GetMapping("{id}/tasks/history")
    public PagingResponse<?> historyTasks(@PathVariable String id, @Validated PagingVO paging) {
        Page<Task> page = taskService.historyTasks(paging.toPageable(), id);

        return PagingResponse.of(page);
    }

    @GetMapping("{id}/diagram")
    public ResponseEntity<byte[]> diagram(@PathVariable String id) {
        byte[] rst = processService.diagram(id);
        return ResponseEntitys.of(rst, id + ".png");
    }

    @PostMapping
    public ResponseEntity<?> start(@RequestBody @Validated StartProcessVO payload) {

        Process process = processService.start(StartProcessPayload.create()
                .processDefinitionId(payload.getProcessDefId())
                .processDefinitionKey(payload.getProcessDefKey())
                .processName(payload.getProcessName())
                .businessKey(payload.getBusinessKey())
                .variables(payload.getVariables())
                .form(payload.getOutcome(), payload.getFormData()));

        return ResponseEntitys.created(process);
    }

    @RequiresPermissions(Permission.PERMISSION_PROCESS_EDIT)
    @DeleteMapping("{id}")
    public ResponseEntity<?> delProcessInst(@PathVariable String id, @RequestParam String reason) {
        processService.delete(id, reason);
        return ResponseEntitys.updated();
    }

    @RequiresPermissions(Permission.PERMISSION_PROCESS_EDIT)
    @DeleteMapping("{id}/history")
    public ResponseEntity<?> delHistory(@PathVariable String id) {
        processService.deleteHistory(id);
        return ResponseEntitys.updated();
    }

    @RequiresPermissions(Permission.PERMISSION_PROCESS_EDIT)
    @PutMapping("{id}/{state}")
    public ResponseEntity<?> toggleInstState(@PathVariable String id, @PathVariable STATE state) {
        processService.state(id, STATE.active == state);
        return ResponseEntitys.updated();
    }

}
