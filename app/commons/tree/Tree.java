package commons.tree;

import commons.Helper;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Member: Freewind
 * Date: 13-1-17
 * Time: 1:47 PM
 */
public class Tree {

    public final List<TreeNode> roots = new ArrayList<TreeNode>();

    public boolean isEmpty() {
        return roots.isEmpty();
    }

    public int nodeCount() {
        return getAllNodes().size();
    }

    public TreeNode findNode(String code) {
        for (TreeNode item : getAllNodes()) {
            if (Helper.eq(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public List<TreeNode> getAllNodes() {
        List<TreeNode> all = new ArrayList<TreeNode>();
        for (TreeNode root : roots) {
            visitNodes(root, all);
        }
        return all;
    }

    private void visitNodes(TreeNode item, List<TreeNode> result) {
        result.add(item);
        if (!item.children.isEmpty()) {
            for (TreeNode child : item.children) {
                visitNodes(child, result);
            }
        }
    }

    public void removeNode(String code) {
        TreeNode item = findNode(code);
        if (item != null) {
            if (item.parent == null) {
                roots.remove(item);
            } else {
                item.parent.children.remove(item);
            }
        }
    }

    public String toJsonTree() {
        return Helper.toJson(roots);
    }

    public void add(TreeNode newNode) {
        if (findNode(newNode.code) != null) {
            throw new IllegalArgumentException("Duplicate node code: " + newNode.code);
        }
        if (isNotBlank(newNode.parentCode)) {
            TreeNode parent = findNode(newNode.parentCode);
            if (parent == null) {
                throw new ParentNotFoundException(newNode);
            }
            newNode.parent = parent;
            parent.addChild(newNode);
        } else {
            roots.add(newNode);
        }
    }

    public void addAll(List<TreeNode> nodes) {
        while (nodes.size() > 0) {
            TreeNode node = nodes.remove(0);
            try {
                add(node);
            } catch (ParentNotFoundException e) {
                nodes.add(e.getNode());
            }
        }
    }

    public String toJsonArray() {
        return Helper.toJson(getAllNodes());
    }

    private static class ParentNotFoundException extends RuntimeException {
        private final TreeNode node;

        public ParentNotFoundException(TreeNode node) {
            super("Parent node not found: " + node.parentCode);
            this.node = node;
        }

        public TreeNode getNode() {
            return this.node;
        }
    }

    public List<TreeNode> getRoots() {
        return this.roots;
    }

}
