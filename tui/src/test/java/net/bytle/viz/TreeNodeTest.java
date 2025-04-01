package net.bytle.viz;

import org.junit.Test;

public class TreeNodeTest {

  @Test
  public void test() {
    TreeNode aa = new TreeNode("aa");
    TreeNode a = new TreeNode("a",aa);
    TreeNode b = new TreeNode("b");
    TreeNode root = new TreeNode("root",a,b);
    System.out.println(root.toString());

  }
}
