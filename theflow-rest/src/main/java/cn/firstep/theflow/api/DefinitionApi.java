package cn.firstep.theflow.api;

import cn.firstep.theflow.common.AppException;
import cn.firstep.theflow.common.Permission;
import cn.firstep.theflow.common.ResponseEntitys;
import cn.firstep.theflow.common.code.DefinitionCode;
import cn.firstep.theflow.model.Definition;
import cn.firstep.theflow.model.request.PagingVO;
import cn.firstep.theflow.model.request.QueryDefinitionVO;
import cn.firstep.theflow.model.response.PagingResponse;
import cn.firstep.theflow.service.DefinitionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.form.api.FormModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Definition API.
 *
 * @author Alvin4u
 */
@RestController
@RequestMapping("flow/definitions")
public class DefinitionApi {
    private static Logger LOGGER = LoggerFactory.getLogger(DefinitionApi.class);

    @Autowired
    private DefinitionService defService;

    private enum RES_TYPE {
        model,
        diagram
    }

    private enum STATE {
        active,
        suspend
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_ACCESS)
    @GetMapping("{id}/start_form")
    public ResponseEntity<?> startForm(@PathVariable String id){
        FormModel formModel = defService.startForm(id);
        return ResponseEntitys.ok(formModel);
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_ACCESS)
    @GetMapping
    public ResponseEntity<?> list(@Validated PagingVO page, @Validated QueryDefinitionVO query) {
        Page<Definition> list = defService.list(page.toPageable(), query.toPayload());
        return ResponseEntitys.ok(PagingResponse.of(list));
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_ACCESS)
    @GetMapping("{id}/resources/{type}")
    public ResponseEntity<?> resource(@PathVariable String id, @PathVariable RES_TYPE type) {
        byte[] bytes = RES_TYPE.model == type ? defService.resource(id)
                : RES_TYPE.diagram == type ? defService.diagram(id) : null;

        if (bytes == null) {
            throw AppException.of(DefinitionCode.NOT_FOUND_MODEL);
        }

        return ResponseEntitys.of(bytes, RES_TYPE.model == type ? id + ".bpmn" : id + ".png");
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_EDIT)
    @PostMapping("models/{id}")
    public ResponseEntity<?> deploy(@PathVariable String id) {
        Definition def = defService.deploy(id);
        return ResponseEntitys.created(def);
    }

    @Deprecated
    @RequiresPermissions(Permission.PERMISSION_DEFINITION_EDIT)
    @PostMapping("{id}/forms/models/{formId}")
    public ResponseEntity<?> deployForm(@PathVariable String id, @PathVariable String formId) {
        defService.deployForm(id, formId);
        return ResponseEntitys.created();
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_EDIT)
    @Deprecated
    @PostMapping(headers = "content-type=multipart/form-data")
    public ResponseEntity<?> deploy(@RequestPart MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntitys.updated();
        }

        List<Definition> deploys;
        try {
            deploys = defService.deploy(file.getOriginalFilename(), file.getInputStream());
        } catch (IOException e) {
            LOGGER.error("deploy failed.", e);
            throw AppException.of(DefinitionCode.DEPLOY_FAILD);
        }
        return ResponseEntitys.created(deploys);
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_EDIT)
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        defService.delete(id, true);
        return ResponseEntitys.updated();
    }

    @RequiresPermissions(Permission.PERMISSION_DEFINITION_EDIT)
    @PutMapping("{id}/{state}")
    public ResponseEntity<?> toggleState(@PathVariable("id") String id, @PathVariable("state") STATE state) {

        defService.state(id, STATE.active == state);

        return ResponseEntitys.updated();
    }

}
