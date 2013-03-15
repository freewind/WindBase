package commons.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commons.Helper;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    public String code;

    public String name;

    public String parentCode;

    @JsonIgnore
    public TreeNode parent;

    public List<TreeNode> children = new ArrayList<TreeNode>();

    // controller.action
    public String link;

    public String icon;

    public TreeNode() {
    }

    public TreeNode(String code, String name, String parentCode) {
        Helper.notBlank(code, "code");
        Helper.notBlank(name, "name");
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public int getLevel() {
        int level = 0;
        TreeNode p = parent;
        while (p != null) {
            level++;
            p = p.parent;
        }
        return level;
    }

//    public String getUrl() {
//        if (isNotBlank(link)) {
//            Map<String, Object> args = new HashMap<String, Object>();
//            args.put("menu_code", this.code);
//            return Router.getFullUrl(link, args);
//        }
//        return null;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode menuItem = (TreeNode) o;
        if (code != null ? !code.equals(menuItem.code) : menuItem.code != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

}
