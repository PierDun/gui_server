import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

class DynamicTree extends JPanel {
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    DynamicTree(String ROOT) {
        super(new GridLayout(1, 0));

        rootNode = new DefaultMutableTreeNode(ROOT);
        treeModel = new DefaultTreeModel(rootNode);

        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane);
    }

    void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection
                    .getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        toolkit.beep();
    }

    void remove(Chest removedChest){ }

    Chest getCurrentChest() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        return (Chest) selectedNode.getUserObject();
    }

    void addObject(Chest child) {
        DefaultMutableTreeNode parentNode = rootNode;
        addObject(parentNode, child, true);
    }

    private void addObject(DefaultMutableTreeNode parent, Chest child, boolean shouldBeVisible) {

        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
    }

    void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }
}
