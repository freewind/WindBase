package controllers;

import commons.AdminMenuTree;
import commons.AppResource;
import commons.Check;
import commons.GridOptionsManager;
import commons.Helper;
import commons.PrivilegeButton;
import commons.PrivilegeButtonManager;
import commons.tree.TreeNode;
import models.User;
import play.Play;
import play.cache.CacheFor;
import play.templates.BaseTemplate;
import replay_utils.ModuleCtrlAction;
import replay_utils.ModuleCtrlActionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: freewind
 * Date: 13-2-17
 * Time: 下午8:49
 */
public class Admins extends BaseAdminController {

    @Check("")
    public static void index() {
        request.format = "html";
        renderBinary(Play.getFile("/public/admin.html"));
    }

    @Check("")
    public static void index2() {
        render();
    }

    @Check("")
    public static void defer() {
        render();
    }

    @Check("")
    public static BaseTemplate.RawData commonData() throws IOException {
        List<TreeNode> menuTree = new AdminMenuTree().roots;
        List<String> icons = AppResource.getMenuIcons();
        List<ModuleCtrlAction> actions = ModuleCtrlActionUtils.get(true);
        Map<String, List<PrivilegeButton>> privilegeButtonsMap = new PrivilegeButtonManager().map;
        String rootMenuCode = (String) renderArgs.get(ROOT_MENU_CODE);
        String currentMenuCode = (String) renderArgs.get(MENU_CODE);
        Set<String> currentPrivilegeCodes = null;
        User currentUser = currentUser();
        if (currentUser != null) {
            currentPrivilegeCodes = currentUser.getPrivilegeCodes();
        }
        return renderRaw("Admins/commonData.json", "json",
                menuTree, icons, actions, rootMenuCode, currentMenuCode, privilegeButtonsMap, currentPrivilegeCodes);
    }

    @Check("")
    public static void getModuleCtrlActions() throws IOException {
        List<ModuleCtrlAction> actions = ModuleCtrlActionUtils.get(true);
        renderJSON(Helper.toJson(actions));
    }

    @Check("")
    public static void defaultMain(String menu_code) {
        request.format = "html";
        render(menu_code);
    }

    // TODO CACHE
    @Check("")
    public static void getMenuTree() throws IOException {
        renderJSON(new AdminMenuTree().toJsonTree());
    }

    // TODO CACHE
    @Check("")
    public static void getPrivilegeButtonsMap() throws IOException {
        renderJSON(new PrivilegeButtonManager().toJson());
    }

    @CacheFor
    @Check("")
    public static void getIcons() throws IOException {
        renderJSON(Helper.toJson(AppResource.getMenuIcons()));
    }

    @Check("")
    public static void getCurrentPrivilegeCodes() {
        User current = currentUser();
        if (current != null) {
            Set<String> codes = current.getPrivilegeCodes();
            renderJSON(Helper.toJson(codes));
        } else {
            renderJSON("[]");
        }
    }

    public static void getAllGridOptions() throws IOException {
        renderJSON(Helper.toJson(new GridOptionsManager().getAll()));
    }

    @Check("")
    public static void getCommonData() throws IOException {
        request.format = "json";
        AdminMenuTree menuTree = new AdminMenuTree();
        PrivilegeButtonManager privilegeButtonManager = new PrivilegeButtonManager();
        render(menuTree, privilegeButtonManager);
    }

    @Check("")
    public static void getCurrentUserData() throws IOException {
        request.format = "json";
        User current = currentUser();
        render(current);
    }

}
