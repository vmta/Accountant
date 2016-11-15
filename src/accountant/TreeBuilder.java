package accountant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeBuilder {
	private Tree tree;
	private TreeItem treeItem;
	
	public TreeBuilder(Composite composite) {
		tree = new Tree(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	public void updateTree(String categoryNameText) {
		if(tree.getItemCount() > 0)
			tree.removeAll();
		if(categoryNameText != "Currencies") {
			for(String s : new Data("preferences").get(categoryNameText)) {
				int pos;
				if((pos = s.indexOf(".")) == -1) {
					treeItem = getNode(tree, SWT.NULL);
					treeItem.setText(s);
				} else if(pos == 0) {
					treeItem = getNode(tree, SWT.NULL);
					treeItem.setText(s.split("\\.")[1]);
				} else {
					treeItem = getNode(tree.getItem(tree.getItemCount() - 1), SWT.NULL);
					treeItem.setText(s.split("\\.")[1]);
				}
			}
		} else {
			for(String s : new Data("preferences").get(categoryNameText)) {
				treeItem = getNode(tree, SWT.NULL);
				if(!s.equals(new Data("preferences").get("BaseCurrency").toArray()[0])) {
					treeItem.setText(s);
				} else {
					treeItem.setText("* " + s);
				}
			}
		}
	}
	
	/**
	 * Create a TreeItem. Overloaded function
	 * 
	 * @param parent
	 * @return child
	 */
	public TreeItem getNode(Tree parent, int style) {
		TreeItem child = new TreeItem(parent, style);
		return child;
	}
	public TreeItem getNode(TreeItem parent, int style) {
		TreeItem child = new TreeItem(parent, style);
		return child;
	}
	
	/**
	 * Get item's text.
	 * 
	 * @param item
	 * @return
	 */
	public String getText(int item) {
		return tree.getItem(item).getText();
	}
}