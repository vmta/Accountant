package accountant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ComboBuilder {
	private Combo combo;
	public ComboBuilder(Composite composite) {
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
	}
	public void updateCombo(String categoryNameText) {
		if(combo.getItemCount() > 0)	// If number of items is non-zero
			combo.removeAll();			// remove all items
		for(String s : new Data("preferences").get(categoryNameText)) {
			if(s.indexOf(".") == -1) {	// Work with non-nesting structure
				combo.add(s);
			} else {					// Work with nesting (remove unnecessary data)
				combo.add(s.split("\\.")[1]);
			}
		}
		select(0);
		combo.pack();
	}
	public String getText() {
		return combo.getText();
	}
	public void select(int item) {
		combo.select(item);
	}
	public boolean isVisible() {
		return combo.isVisible();
	}
	public void setVisible(boolean flag) {
		combo.setVisible(flag);
	}
	public void addSelectionListener(SelectionAdapter arg0) {
		combo.addSelectionListener(arg0);
	}
}