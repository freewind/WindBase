package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.Helper;
import commons.tree.Tree;
import commons.tree.TreeNode;
import models.Category;
import org.apache.commons.lang.mutable.MutableInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 12-12-17
 * Time: 下午11:06
 */
public class Categories extends BaseAdminController {

    public static void index() {
        request.format = "html";
        render();
    }

    public static void getTree() {
        List<Category> categories = Category.find.order("displayOrder asc").findList();
        Tree tree = new Tree();
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        for (Category category : categories) {
            nodes.add(toTreeNode(category));
        }
        tree.addAll(nodes);
        renderJSON(tree.toJsonTree());
    }

    private static TreeNode toTreeNode(Category category) {
        TreeNode item = new TreeNode();
        item.code = category.id;
        item.name = category.name;
        item.parentCode = category.parent == null ? null : category.parent.getId();
        return item;
    }

    public static void add() {
    }

    public static void create(String name, String parentId) {
        Category category = new Category();
        category.name = name;
        if (isNotBlank(parentId)) {
            category.parent = Category.find.byId(parentId);
        }
        category.save();
        renderJSON(Helper.toJson(toTreeNode(category)));
    }

    public static void show(String id) {
        Category category = Category.find.byId(id);
        render(category);
    }

    public static void edit(String id) {
        Category category = Category.find.byId(id);
        render(category);
    }

    public static void update(String id, String name, String parentId) {
        Category category = Category.find.byId(id);
        category.name = name;
        if (isNotBlank(parentId)) {
            category.parent = Category.find.byId(parentId);
        } else {
            category.parent = null;
        }
        category.save();
        ok();
    }

    public static void remove(String id) {
        Category category = Category.find.byId(id);
        category.delete();
        ok();
    }

    public static void updateOrder(String body) throws IOException {
        List<TreeNode> roots = Helper.createObjectMapper().readValue(body, new TypeReference<List<TreeNode>>() {
        });
        MutableInt order = new MutableInt(0);
        resetOrderAndUpdate(roots, order);
        ok();
    }

    private static void resetOrderAndUpdate(List<TreeNode> nodes, MutableInt order) {
        for (TreeNode node : nodes) {
            Category category = Category.find.byId(node.code);
            category.displayOrder = order.intValue();
            category.save();
            order.increment();
            resetOrderAndUpdate(node.children, order);
        }
    }

    public static void changeParent(String id, String parentId) {
        Category category = Category.find.byId(id);
        if (isNotBlank(parentId)) {
            category.parent = Category.find.byId(parentId);
        } else {
            category.parent = null;
        }
        // 显示在同级的最后
        category.displayOrder = System.currentTimeMillis();
        category.save();
        ok();
    }

}
