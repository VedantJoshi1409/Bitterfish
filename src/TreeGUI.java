import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class TreeGUI extends JFrame {
    private JTree tree;

    public TreeGUI(DefaultMutableTreeNode rootNode) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the tree
        tree = new JTree(rootNode);
        tree.setCellRenderer(new TreeCellRenderer());
        JScrollPane scrollPane = new JScrollPane(tree);

        getContentPane().add(scrollPane);
        pack();
        setLocationRelativeTo(null); // Center the frame
    }

    public static void displayTree(SearchNode startNode) {
        SwingUtilities.invokeLater(() -> {
            TreeGUI treeGUI = new TreeGUI(convertNode(startNode));
            treeGUI.setVisible(true);
        });
    }

    private static DefaultMutableTreeNode convertNode(SearchNode startNode) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(startNode);
        for (SearchNode child : startNode.childNodes) {
            treeNode.add(convertNode(child));
        }
        return treeNode;
    }
}