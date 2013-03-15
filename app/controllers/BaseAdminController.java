package controllers;

import commons.AdminMenuTree;
import commons.Check;
import commons.PrivilegeButton;
import commons.PrivilegeButtonManager;
import commons.tree.TreeNode;
import play.mvc.Before;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

/**
 * User: Freewind
 * Date: 12-12-17
 * Time: 下午10:43
 */
@Check("user")
public class BaseAdminController extends BaseController {

    protected static final String MENU_CODE = "menu_code";
    protected static final String ROOT_MENU_CODE = "root_menu_code";

    @Before
    static void prepareData() throws IOException {
        String format = params.get("format");
        if (isNotBlank(format)) {
            request.format = format;
        }

        AdminMenuTree adminMenuTree = new commons.AdminMenuTree();
        renderArgs.put("adminMenuTree", adminMenuTree);

        String menuCode = params.get(MENU_CODE);
        renderArgs.put(MENU_CODE, menuCode);

        TreeNode currentRootMenuNode = getRootMenuCode(adminMenuTree, menuCode);
        renderArgs.put("currentRootMenuNode", currentRootMenuNode);

        if (currentRootMenuNode != null) {
            String rootMenuCode = currentRootMenuNode.code;
            if (isNotBlank(rootMenuCode)) {
                renderArgs.put(ROOT_MENU_CODE, rootMenuCode);
            } else {
                if (isNotBlank(menuCode)) {
                    TreeNode node = adminMenuTree.findNode(menuCode);
                    while (node.parent != null) {
                        node = node.parent;
                    }
                    renderArgs.put(ROOT_MENU_CODE, node.code);
                }
            }
        }

        PrivilegeButtonManager buttonManager = new PrivilegeButtonManager();
        renderArgs.put("buttonManager", buttonManager);

        if (isNotBlank(menuCode)) {
            List<PrivilegeButton> buttons = buttonManager.findButtons(menuCode);
            if (buttons != null) {
                renderArgs.put("currentButtons", buttons);
            }
        }
    }

    private static TreeNode getRootMenuCode(AdminMenuTree adminMenuTree, String menuCode) {
        TreeNode node = adminMenuTree.findNode(menuCode);
        while (node != null && node.parent != null) {
            node = node.parent;
        }
        return node;
    }

    public static final List<PrivilegeButton> getCurrentButtons() {
        return (List<PrivilegeButton>) renderArgs.get("currentButtons");
    }

}
