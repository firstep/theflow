package cn.firstep.theflow.api;

import cn.firstep.theflow.common.ResponseEntitys;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Overwrite IDM REST API.
 * @author Alvin4u
 */
@RestController
@RequestMapping("app")
public class ModelerApi {

    /**
     * Overwrite {@link org.flowable.ui.common.rest.idm.remote.RemoteAccountResource}
     * @return
     */
    @GetMapping("rest/account")
    public ResponseEntity<?> account() {
        RemoteUser remoteUser = (RemoteUser) SecurityUtils.getCurrentUserObject();
        UserRepresentation user = new UserRepresentation(remoteUser);
        user.setPrivileges(remoteUser.getPrivileges());
        return ResponseEntitys.ok(user);
    }

    /**
     * Overwrite {@link org.flowable.ui.modeler.rest.app.EditorUsersResource}
     * @param filter
     * @return
     */
    @GetMapping("rest/editor-users")
    public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter) {
        return null;
    }

    /**
     * Overwrite {@link org.flowable.ui.modeler.rest.app.EditorGroupsResource}
     * @param filter
     * @return
     */
    @GetMapping("rest/editor-groups")
    public ResultListDataRepresentation getGroups(@RequestParam(value = "filter", required = false) String filter) {
        return null;
    }
}
