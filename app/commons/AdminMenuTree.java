package commons;

import com.fasterxml.jackson.core.type.TypeReference;
import commons.tree.Tree;
import commons.tree.TreeNode;
import org.apache.commons.io.FileUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuTree extends Tree {

    private static final File ADMIN_MENU_FILE = Play.getFile("conf/admin_menus.json");

    public AdminMenuTree(List<TreeNode> roots) {
        super.roots.addAll(roots);
        resetParents(); // set parents for each item
    }

    public AdminMenuTree() throws IOException {
        if (ADMIN_MENU_FILE.exists()) {
            String content = FileUtils.readFileToString(ADMIN_MENU_FILE);
            List<TreeNode> nodes = Helper.createObjectMapper().readValue(content, new TypeReference<List<TreeNode>>() {
            });
            this.roots.addAll(nodes);
            resetParents(); // set parents for each item
        }
    }


    public void resetParents() {
        List<TreeNode> all = new ArrayList<TreeNode>();
        for (TreeNode root : roots) {
            root.parent = null;
            all.add(root);
        }
        for (int i = 0; i < all.size(); i++) {
            TreeNode item = all.get(i);
            for (TreeNode child : item.children) {
                child.parent = item;
                all.add(child);
            }
        }
    }

    public void save() throws IOException {
        String jsonStr = Helper.toJson(roots, true);
        FileUtils.writeStringToFile(ADMIN_MENU_FILE, jsonStr, "UTF-8");
    }

}
