/**
 * Class Accountant migration from Eclipse to NetBeans IDE.
 * 
 * @author vmta
 * @version 0.1
 * @date 2016/08/17
 */

package accountant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author vmta
 */
public class Accountant {
	
	/**
	 * Define Variables for later use
	 */
	
	/**
	 * DISPLAY and SHELL
	 */
	private static Display display = null;
	private static Shell shell = null;
	
	/**
	 * MENUBAR
	 */
	private static Menu bar = null;
	
	/**
	 * TABFOLDER
	 */
	private static TabFolder folder = null;
	
	/**
	 * LAYOUT within TABFOLDER
	 */
	private static Composite composite = null;
	private static GridLayout gridLayout = null;
	private static GridData gridData = null;
	
	/**
	 * StorageData
	 */
	private static PreferenceData preferences = null;
	
	/**
	 * MENU constructor
	 * @return
	 */
	public static Menu buildMenu() {
		bar = new Menu(shell, SWT.BAR);
		
		/**
		 * Create "FILE" Menu
		 * - Option "Close" disposes the display and shuts down the App.
		 */
		// Add "File" option, set Text, add sub menu
		MenuItem miFile = new MenuItem(bar, SWT.CASCADE);
		miFile.setText("File");
		Menu mFile = new Menu(shell, SWT.DROP_DOWN);
		miFile.setMenu(mFile);
		// Add "Close" option, set Text, add Listener
		MenuItem miClose = new MenuItem(mFile, SWT.PUSH);
		miClose.setText("Close");
		miClose.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				display.dispose();
			}
		});
		// Add "Help" option, set Text, add sub menu
		MenuItem miHelp = new MenuItem(bar, SWT.CASCADE);
		miHelp.setText("Help");
		Menu mHelp = new Menu(shell, SWT.DROP_DOWN);
		miHelp.setMenu(mHelp);
		// Add "About" option, set Text, add Listener
		MenuItem miAbout = new MenuItem(mHelp, SWT.PUSH);
		miAbout.setText("Help");
		miAbout.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				MessageBox mbAbout = new MessageBox(shell, SWT.OK);
				mbAbout.setText("About");
				mbAbout.setMessage("This is an initial version.\n(c) 2016");
				mbAbout.open();
			}
		});
		
		return bar;
	}
	
	/**
	 * TABS Constructor
	 */
	// "Preferences" Tab
	public static void preferencesTab() {
		
		TabItem tab = new TabItem(folder, SWT.CLOSE);
		tab.setText("Preferences");
		tab.setToolTipText("List of Options");
		
		composite = new Composite(folder, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		composite.setLayout(gridLayout);
		
		Group buttons = new Group(composite, SWT.NONE);
		buttons.setText("Directories");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		buttons.setLayout(gridLayout);
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		buttons.setLayoutData(gridData);
		
		Button incomeButton = new Button(buttons, SWT.PUSH);
		incomeButton.setText("Income Source");
		incomeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button expenseButton = new Button(buttons, SWT.PUSH);
		expenseButton.setText("Expense Category");
		expenseButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button savingsButton = new Button(buttons, SWT.PUSH);
		savingsButton.setText("Saving Location");
		savingsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button currencyButton = new Button(buttons, SWT.PUSH);
		currencyButton.setText("Currencies");
		currencyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/**
		 * 
		Button testButton = new Button(buttons, SWT.PUSH);
		testButton.setText("Test");
		testButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		*/
		
		Group options = new Group(composite, SWT.NONE);
		options.setText("Options");
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		options.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		options.setLayoutData(gridData);
		
		Label categoryName = new Label(options, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		categoryName.setLayoutData(gridData);
		categoryName.setText("Income Source");
		
		/**
		 * Build TREE
		 */
		TreeBuilder tree = new TreeBuilder(options);
		tree.updateTree(categoryName.getText());
		
		/**
		 * Build DATA entrance
		 */
		Composite form = new Composite(options, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		form.setLayout(gridLayout);
		gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		gridData.widthHint = 200;
		form.setLayoutData(gridData);
		
		Label nameLabel = new Label(form, SWT.NONE);
		nameLabel.setText("Name");
		Text nameText = new Text(form, SWT.NONE);
		nameText.setText("New Entry");
		nameText.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				nameText.setText("");
			}
		});
		
		Label nestingLabel = new Label(form, SWT.NONE);
		nestingLabel.setText("Nesting");
		ComboBuilder nestingCombo = new ComboBuilder(form);
		nestingCombo.updateCombo(categoryName.getText());
		
		/**
		 * 
		new Label(form, SWT.NONE);
		Button rootCheckButton = new Button(form, SWT.CHECK);
		rootCheckButton.setText("set as root");
		
		*/
		
		Label baseCurrencyLabel = new Label(form, SWT.NONE);
		baseCurrencyLabel.setText("Base");
		baseCurrencyLabel.setVisible(false);
		ComboBuilder baseCurrencyCombo = new ComboBuilder(form);
		baseCurrencyCombo.updateCombo(categoryName.getText());
		baseCurrencyCombo.setVisible(false);
		baseCurrencyCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// Save preferences
				preferences.save("BaseCurrency", baseCurrencyCombo.getText());
				
				// Update Tree
				tree.updateTree(categoryName.getText());
			}
		});
		
		/**
		 * Comments (yet to be considered)
		 */
		/*
		Label commentsLabel = new Label(form, SWT.NONE);
		commentsLabel.setText("Comments");
		Text commentsText = new Text(form, SWT.NONE);
		*/
		
		Button saveButton = new Button(form, SWT.PUSH);
		saveButton.setText("Save");
		gridData = new GridData(GridData.CENTER, GridData.CENTER, false, false);
		gridData.horizontalSpan = 2;
		saveButton.setLayoutData(gridData);
		saveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Initialize vars
				String preference = categoryName.getText();
				String name = nameText.getText();
				String nesting = nestingCombo.getText();
			//	boolean rootCheck = rootCheckButton.getSelection();
	//			String comments = commentsText.getText();
				
			//	name = "." + name;
			//	if(!rootCheck) {
			//		name = " " + name;
			//	}
				
				// Save preferences
				preferences.save(preference, name, nesting);
				
				// Update Tree
				tree.updateTree(categoryName.getText());
				
				// Update Combos
				nestingCombo.updateCombo(categoryName.getText());
				baseCurrencyCombo.updateCombo(categoryName.getText());
			}
		});
		
		// Action Listeners
		incomeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Update Category Name
				categoryName.setText("Income Source");
				
				// Update Tree
				tree.updateTree(categoryName.getText());
				
				// Update Combo
				nestingCombo.updateCombo(categoryName.getText());
				baseCurrencyLabel.setVisible(false);
				baseCurrencyCombo.setVisible(false);
			}
		});
		
		expenseButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Update Category Name
				categoryName.setText("Expense Category");
				
				// Update Tree
				tree.updateTree(categoryName.getText());
				
				// Update Combo
				nestingCombo.updateCombo(categoryName.getText());
				baseCurrencyLabel.setVisible(false);
				baseCurrencyCombo.setVisible(false);
			}
		});
		
		savingsButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Update Category Name
				categoryName.setText("Saving Location");
				
				// Update Tree
				tree.updateTree(categoryName.getText());
				
				// Update Combo
				nestingCombo.updateCombo(categoryName.getText());
				baseCurrencyLabel.setVisible(false);
				baseCurrencyCombo.setVisible(false);
			}
		});
		
		currencyButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Update Category Name
				categoryName.setText("Currencies");
				
				// Update Tree
				tree.updateTree(categoryName.getText());
				
				// Update Combo
				nestingCombo.updateCombo(categoryName.getText());
				baseCurrencyCombo.updateCombo(categoryName.getText());
				baseCurrencyLabel.setVisible(true);
				baseCurrencyCombo.setVisible(true);
				
				String baseCurrency = preferences.get("BaseCurrency").get(0);
				int i = 0;
				for(String s : preferences.get("Currencies")) {
					if(s.equals(baseCurrency)) break;
					i++;
				}
				baseCurrencyCombo.select(i);
			}
		});

		/**
		 * 
		testButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Update Category Name
				categoryName.setText("Test");
				
				// Update Tree
				tree.updateTree(categoryName.getText());
			}
		});
		
		*/
		
		composite.pack();
		tab.setControl(composite);
	}
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setText("Accountant");
		shell.setMenuBar(buildMenu());
		shell.setSize(800, 600);
		shell.setLayout(new FillLayout());

		preferences = new PreferenceData();
		folder = new TabFolder(shell, SWT.BORDER);
		new OperationTab(folder, "Credit");
		new OperationTab(folder, "Debit");
		new OperationTab(folder, "Exchange");
		preferencesTab();
		shell.open();
		while(!shell.isDisposed())
			if(!display.readAndDispatch())
				display.sleep();
		display.dispose();
		System.exit(0);
	}
}