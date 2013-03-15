package commons.tree;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


/**
 * Member: Freewind
 * Date: 13-1-17
 * Time: 下午2:17
 */
public class TreeTest {

    @Test
    public void test() {
        Tree tree = new Tree();
        tree.add(new TreeNode("1", "111", null));
        tree.add(new TreeNode("2", "222", "1"));
        tree.add(new TreeNode("3", "333", "2"));
        tree.add(new TreeNode("4", "444", null));
        assertThat(tree.nodeCount()).isEqualTo(4);
        System.out.println(tree.toJsonTree());
    }

}
