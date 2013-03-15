package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.AdminMenuTree;
import commons.App;
import commons.AppResource;
import commons.Helper;
import commons.tree.TreeNode;
import play.data.validation.Required;
import replay_utils.ModuleCtrlAction;
import replay_utils.ModuleCtrlActionUtils;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

public class AdminMenus extends BaseAdminController {

    public static void showTree() throws IOException {
        request.format = "html";
        AdminMenuTree tree = new AdminMenuTree();
        String icons = Helper.toJson(AppResource.getMenuIcons());
        List<ModuleCtrlAction> actions = ModuleCtrlActionUtils.get(true);
        render(tree, icons, actions);
    }

    public static void create(@Required String name, String parentCode) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        TreeNode item = new TreeNode();
        item.code = App.newId();
        item.name = name;
        item.parentCode = parentCode;
        tree.add(item);
        tree.save();
        renderJSON(Helper.toJson(item));
    }

    public static void remove(@Required String code) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        tree.removeNode(code);
        tree.save();
        ok();
    }

    public static void setIcon(String code, String icon) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        TreeNode item = tree.findNode(code);
        item.icon = icon;
        tree.save();
        ok();
    }

    public static void setLink(String code, String link) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        TreeNode item = tree.findNode(code);
        item.link = link;
        tree.save();
        ok();
    }


    public static void updateTree(String body) throws IOException {
        List<TreeNode> items = Helper.createObjectMapper().readValue(body, new TypeReference<List<TreeNode>>() {
        });
        AdminMenuTree tree = new AdminMenuTree(items);
        tree.save();
        ok();
    }

    public static void updateItem(@Required String code, @Required String name) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        TreeNode item = tree.findNode(code);
        item.name = name;
        tree.save();
        ok();
    }

    public static void resetParent(String code, String parentCode) throws IOException {
        AdminMenuTree tree = new AdminMenuTree();
        TreeNode item = tree.findNode(code);
        if (item.parent == null) {
            tree.roots.remove(item);
        } else {
            item.parent.children.remove(item);
        }
        if (isBlank(parentCode)) {
            tree.roots.add(item);
        } else {
            TreeNode parent = tree.findNode(parentCode);
            parent.children.add(item);
        }
        tree.save();
        tree.resetParents();
        renderJSON(tree.toJsonTree());
    }

}


