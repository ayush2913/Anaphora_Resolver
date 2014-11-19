package com.prabal.anaphoraResolution;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
 
public class TreeDemo extends JFrame
{
    private JTree tree;
    public TreeDemo()
    {
        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
        DefaultMutableTreeNode newnode = new DefaultMutableTreeNode("newNode");
        //DefaultMutableTreeNode[] nodes= {vegetableNode,fruitNode};
 
        //add the child nodes to the root node
        
        root.add(vegetableNode);
        root.add(fruitNode);
        vegetableNode.add(newnode);
//        for(int i=0;i<nodes.length;i++){
//        	root.add(nodes[i]);
//        }
         
        //create the tree by passing in the root node
        tree = new JTree(root);
        add(tree);
         
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");       
        this.pack();
        this.setVisible(true);
    }
     
//    public static void main(String[] args)
//    {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new TreeDemo();
//            }
//        });
//    }       
}