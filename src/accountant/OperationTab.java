package accountant;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class OperationTab {
	
	/**
	 * Define Variables for later use
	 */
	// GENERAL Purpose
	private TabItem tab = null;
	private String tabName;
	private Composite composite = null;
	private GridLayout gridLayout = null;
	private GridData gridData = null;
	private PreferenceData preferences;
	private BalanceBuilder bb = null;
	
	// Group specific variables for Data Entrance
	private Group operationGroup;
	private Label label;
	private Text operationSum;
	private ComboBuilder currency;
	private ComboBuilder operationSource;
	private ComboBuilder locationSource;
	private Text operationComment;
	private Button operationDelete;
	private Button operationEdit;
	private Button operationEnter;
	private Text sellAmount;
	private Text buyAmount;
	private ComboBuilder sellCurrency;
	private ComboBuilder buyCurrency;
	private ComboBuilder sellLocation;
	private ComboBuilder buyLocation;
	
	// Group specific variables for Result
	private Group resultGroup;
	private Table resultTable;
	
	// Group specific variables for Log
	private Group logGroup;
	private Table logTable;
	
	// Group specific variables for Graph
	private Group graphGroup;
	private Canvas canvas;
	final Map<String, Rectangle> rectangleMap = new HashMap<String, Rectangle>();
	
	/**
	 * GENERAL CONSTRUCTOR
	 * 
	 * @param folder
	 * @param tabName
	 */
	public OperationTab(TabFolder folder, String tabName) {
		this.tabName = tabName;
		
		tab = new TabItem(folder, SWT.CLOSE);
		tab.setText(getTabName());
		tab.setToolTipText("List of " + getTabName() + " operations");
		
		preferences = new PreferenceData();
		bb = new BalanceBuilder();
		 
		/**
		 * Create COMPOSITE consisting of 3 columns
		 * There will be 3 (or 2) row layout which produces
		 * a 3 x 3 (or 2) matrix.
		 */
		composite = new Composite(folder, SWT.NONE);
		gridLayout = new GridLayout();
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		
		/**
		 * First Group (DATA Entrance)
		 * Takes space of cells 1 and 2
		 */
		operationGroupBuilder(composite, getTabName());
		
		/**
		 * Second Group RESULT
		 * Takes space of cells 3 and 6
		 */
		resultGroupBuilder(composite, getTabName());
		
		/**
		 * Third Group LOG
		 * Takes space of cells 4 and 5
		 */
		logGroupBuilder(composite, getTabName());
		
		/**
		 * Fourth Group GRAPH
		 * Takes space of cells 7 through 9
		 */
		graphGroupBuilder(composite, getTabName());
		
		/**
		 * Transfer control for composite to current tab
		 */
		tab.setControl(composite);
	}
	
	public String getTabName() { return this.tabName; }
	
	/**
	 * Fill specified table at specified tab with corresponding data.
	 * int tableSwitch corresponds to: 0 - RESULT table; 1 - LOG table. 
	 * 
	 * @param table
	 * @param tabName
	 * @param tableSwitch
	 */
	@SuppressWarnings("static-access")
	public void fillTable(Table table, String tabName, int tableSwitch) {
		tabName = (tabName != null) ? tabName : this.getTabName();
		table.setRedraw(false);
		table.removeAll();
		
		while(table.getColumnCount() > 0)
			table.getColumns()[0].dispose();

		switch(tableSwitch) {
			/**
			 * RESULT TABLE
			 */
			case 0: {
				List<String> currencies = preferences.get("Currencies");
				List<String> locations = preferences.get("Saving Location");
				
				int numberOfColumns = 2;
				
				table.setFont(new Font(table.getDisplay(), new FontData("Arial", 10, SWT.NONE)));
				for(int i = 0; i < numberOfColumns; i++) {
					new TableColumn(table, SWT.NONE).setWidth(100);
				}
				
				for(String location : locations) {
					boolean first = true;
					for(String currency : currencies) {
						double balance = bb.getBalance(location, currency);
						if(balance > 0) {
							if(first) {
								TableItem tI1 = new TableItem(table, SWT.NONE);
								tI1.setText(0, location);
								first = false;
							}
							TableItem tI2 = new TableItem(table, SWT.NONE);
							tI2.setText(1, balance + " " + currency);
						}
					}
				}
				break;
			}
			
			/**
			 * LOG TABLE
			 */
			case 1: {
				Scanner scan = new Scanner(new IO("db").read());
				
				int numberOfColumns = 5; //4;
				int numberOfRows = 0;
				for(int i = 0; i < numberOfColumns; i++) {
					new TableColumn(table, SWT.NONE);
				}
				while(scan.hasNext()) {
					String str = scan.nextLine();
					String[] fields = str.split(";");
					String recordTimestamp = fields[0];
					
					String location = fields[4].substring(fields[4].indexOf("Location: ") + "Location: ".length());
					String source = fields[3].substring(fields[3].indexOf("Source: ") + "Source: ".length());
					String sum = fields[2].substring(fields[2].indexOf("Sum: ") + "Sum: ".length());
					String datetime = recordTimestamp.substring(recordTimestamp.indexOf(" ") + 1, recordTimestamp.length() - 3) + 
							"\n" + 
							recordTimestamp.substring(0, recordTimestamp.indexOf(" "));
					String comment = fields[5].substring(fields[5].indexOf("Comment: ") + "Comment: ".length());
					if(comment.length() > 0) {
						int chars = 50;
						StringBuilder commentSB = new StringBuilder();
						while(comment.length() > chars) {
							commentSB.append(comment.substring(0, chars + (comment.substring(chars).indexOf(" "))) + "\n");
							comment = comment.substring(chars + (comment.substring(chars).indexOf(" ") + 1), comment.length());
						}
						commentSB.append(comment);
						source = source + "\n" + commentSB.toString();
					}
					
					if(str.indexOf(tabName) > -1) {
						/**
						 * If tabName is not NULL and is either Credit or Debit
						 * display corresponding operations (Credit or Debit)
						 */
						TableItem tI = new TableItem(table, SWT.NONE);
						tI.setText(0, recordTimestamp + "; " + tabName); // Mark for possible deletion
						tI.setText(1, location);
						tI.setText(2, source);
						tI.setText(3, sum);
						tI.setText(4, datetime);
						tI.setFont(4, new Font(table.getDisplay(), new FontData("Arial", 11, SWT.NONE)));
						
						if(numberOfRows % 2 == 0) {
							tI.setBackground(table.getDisplay().getSystemColor(19));
						}
						numberOfRows++;
						
					} else if (tabName != "Credit" && tabName != "Debit") {
						/**
						 * If tabName is not Credit or Debit
						 * display all operations (Credit and Debit)
						 */
						TableItem tI = new TableItem(table, SWT.NONE);
						tI.setText(1, location);
						tI.setText(2, source);
						tI.setText(3, sum);
						tI.setText(4, datetime);
						tI.setFont(4, new Font(table.getDisplay(), new FontData("Arial", 11, SWT.NONE)));
						
						if(numberOfRows % 2 == 0) {
							tI.setBackground(table.getDisplay().getSystemColor(19));
						}
						numberOfRows++;
					}
				}
				scan.close();
				
				for(int i = 0; i < table.getColumns().length; i++) {
					table.getColumn(i).pack();
				}
				table.getColumn(0).setWidth(0);
				table.getColumn(2).setWidth(200);
				
				break;
			}
		}
		
		if(!table.isListening(SWT.MeasureItem))
			table.addListener(SWT.MeasureItem, tableListener);
		if(!table.isListening(SWT.PaintItem))
			table.addListener(SWT.PaintItem, tableListener);
		if(!table.isListening(SWT.EraseItem))
			table.addListener(SWT.EraseItem, tableListener);
	    if(!table.isListening(SWT.Selection))
	    	table.addListener(SWT.Selection, tableSelectionListener);
		table.setRedraw(true);
	}
	
	/**
	 * Build First GROUP (data entrance)
	 * Takes space of cells 1 and 2
	 * 
	 * @param composite
	 * @param tabName
	 */
	public void operationGroupBuilder(Composite composite, String tabName) {
		operationGroup = new Group(composite, SWT.NONE);
		operationGroup.setText(tabName);
		
		if (tabName == "Exchange") {
			
			gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
			operationGroup.setLayout(gridLayout);
			gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
			gridData.horizontalSpan = 2;
			operationGroup.setLayoutData(gridData);
			
			// Line 1
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Sell:");
			sellAmount = new Text(operationGroup, SWT.NONE);
			sellAmount.setText("Amount to Sell");
			sellAmount.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					sellAmount.setText("");
				}
			});
			sellCurrency = new ComboBuilder(operationGroup);
			sellCurrency.updateCombo("Currencies");
			sellLocation = new ComboBuilder(operationGroup);
			sellLocation.updateCombo("Saving Location");
			
			// Line 2
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Buy:");
			buyAmount = new Text(operationGroup, SWT.NONE);
			buyAmount.setText("Amount to Buy");
			buyAmount.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					buyAmount.setText("");
				}
			});
			buyCurrency = new ComboBuilder(operationGroup);
			buyCurrency.updateCombo("Currencies");
			buyLocation = new ComboBuilder(operationGroup);
			buyLocation.updateCombo("Saving Location");
			
			// Line 3
			operationEnter = new Button(operationGroup, SWT.PUSH);
			operationEnter.setText("Enter");
			operationEnter.addListener(SWT.Selection, operationButtonListener("Enter"));
			gridData = new GridData(GridData.END, GridData.CENTER, false, false);
			gridData.horizontalSpan = 4;
			operationEnter.setLayoutData(gridData);
			
		} else {
			
			gridLayout = new GridLayout();
			gridLayout.numColumns = 4;
			operationGroup.setLayout(gridLayout);
			gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
			gridData.horizontalSpan = 2;
			operationGroup.setLayoutData(gridData);
			
			// Line 1
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Sum:");
			label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			operationSum = new Text(operationGroup, SWT.SINGLE | SWT.BORDER);
			operationSum.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			currency = new ComboBuilder(operationGroup);
			currency.updateCombo("Currencies");
			currency.select(0);
			
			label = new Label(operationGroup, SWT.NONE);
			label.setText(" ");
			
			// Line 2
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Source:");
			label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			operationSource = new ComboBuilder(operationGroup);
			operationSource.updateCombo((tab.getText() == "Credit" ? "Expense Category" : "Income Source"));
			operationSource.select(0);
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Location:");
			label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			locationSource = new ComboBuilder(operationGroup);
			locationSource.updateCombo("Saving Location");
			locationSource.select(0);
			
			// Line 3
			label = new Label(operationGroup, SWT.NONE);
			label.setText("Comment:");
			label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
			gridData.horizontalSpan = 3;
			operationComment = new Text(operationGroup, SWT.SINGLE | SWT.BORDER);
			operationComment.setLayoutData(gridData);
			
			// Line 4
			operationEnter = new Button(operationGroup, SWT.PUSH);
			operationEnter.setText("Enter");
			if(!operationEnter.isListening(SWT.Selection))
				operationEnter.addListener(SWT.Selection, operationButtonListener("Enter"));
			
			operationEdit = new Button(operationGroup, SWT.PUSH);
			operationEdit.setText("Edit");
			operationEdit.setVisible(false);
			if(!operationEdit.isListening(SWT.Selection))
				operationEdit.addListener(SWT.Selection, operationButtonListener("Edit"));
			
			operationDelete = new Button(operationGroup, SWT.PUSH);
			operationDelete.setText("Delete");
			operationDelete.setVisible(false);
			if(!operationDelete.isListening(SWT.Selection))
				operationDelete.addListener(SWT.Selection, operationButtonListener("Delete"));
		}
	}
	
	/**
	 * Build Second GROUP (result)
	 * Takes space of cells 3 and 6
	 * 
	 * @param composite
	 * @param tabName
	 */
	public void resultGroupBuilder(Composite composite, String tabName) {
		resultGroup = new Group(composite, SWT.V_SCROLL);
		resultGroup.setText("Balance");
		gridLayout = new GridLayout();
		resultGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL, GridData.FILL, false, true);
		gridData.verticalSpan = 2;
		gridData.widthHint = 200;
		resultGroup.setLayoutData(gridData);
				
		/**
		 * FOR each item in Preference Saving Location
		 * get sum of all DEBITS and subtract sum of all CREDITS
		 * and then display it all here!!! 
		 */
		resultTable = new Table(resultGroup, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 200;
		resultTable.setLayoutData(gridData);
		fillTable(resultTable, tabName, 0);
	}
	
	/**
	 * Build Third GROUP (log)
	 * Takes space of cells 4 of 5
	 * 
	 * @param composite
	 * @param tabName
	 */
	public void logGroupBuilder(Composite composite, String tabName) {
		logGroup = new Group(composite, SWT.NONE);
		logGroup.setText("Log");
		gridLayout = new GridLayout();
		logGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		logGroup.setLayoutData(gridData);
		
		logTable = new Table(logGroup, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		logTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fillTable(logTable, tabName, 1);
	}
	
	/**
	 * Build Fourth GROUP (graph)
	 * Takes space of cells 7 through 9
	 * 
	 * @param composite
	 * @param tabName
	 */
	public void graphGroupBuilder(Composite composite, String tabName) {
		graphGroup = new Group(composite, SWT.NONE);
		graphGroup.setText("Graph");
		gridLayout = new GridLayout();
		graphGroup.setLayout(gridLayout);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 110;
		graphGroup.setLayoutData(gridData);
		
		/**
		 * DISPLAY GRAPHS
		 */
		canvas = new Canvas(graphGroup, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		canvas.addPaintListener(new PaintListener() {
			
			public void populateData() {
				/**
				 * GENERATE GRAPH DATA
				 */
				if(tabName == "Credit") {
					List<String> sources = preferences.get("Expense Category");
					int[] sourcesBal = new int[sources.size()];
					int maxBal = 0;
					int i = 0;			
					for(String source : sources) {
						int var = (int) bb.getExpenseBalance(source);
						maxBal = (maxBal < var) ? var : maxBal;
						sourcesBal[i] = var;
						i++;
					}
					int count = 0;
					for(i = 0; i < sources.size(); i++) {
						if(sourcesBal[i] > 0) {
							rectangleMap.put(sources.get(i), new Rectangle(count++ * 30, 99, 20, 1 - (sourcesBal[i] * 100 / maxBal)));
						}
					}
					
				} else if(tabName == "Debit") {
					List<String> sources = preferences.get("Income Source");
					int[] sourcesBal = new int[sources.size()];
					int maxBal = 0;
					int i = 0;
					for(String source : sources) {
						int var = (int) bb.getIncomeBalance(source);
						maxBal = (maxBal < var) ? var : maxBal;
						sourcesBal[i] = var;
						i++;
					}
					int count = 0;
					for(i = 0; i < sources.size(); i++) {
						if(sourcesBal[i] > 0) {
							rectangleMap.put(sources.get(i), new Rectangle(count++ * 30, 99, 20, 1 - (sourcesBal[i] * 100 / maxBal)));
						}
					}
				}
			}
			
			/**
			 * DRAW GRAPHS
			 */
			public void paintControl(PaintEvent e) {
				populateData();
				rectangleMap.forEach((k,v) -> {
					e.gc.drawRectangle(v);
					e.gc.fillRectangle(v);
					
					int x = 700;
					int y = v.x/4;
					Font font = new Font(e.display,"Arial",8,SWT.ITALIC);
					e.gc.setFont(font);
					e.gc.drawText(k, x = (x + y > 800) ? 600 : x, y);
					font.dispose();
				});
			}
		});
	}
	
	/**
	 * Table Listener
	 */
	Listener tableListener = new Listener() {
		public void handleEvent(Event event) {
			switch(event.type) {
				case SWT.MeasureItem: {
					TableItem item = (TableItem) event.item;
					String text = getText(item, event.index);
					Point size = event.gc.textExtent(text);
					event.width = size.x;
					event.height = Math.max(event.height, size.y);
					break;
		        }
		        case SWT.PaintItem: {
		        	TableItem item = (TableItem) event.item;
		        	String text = getText(item, event.index);
		        	Point size = event.gc.textExtent(text);
		        	int offset2 = event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0;
		        	event.gc.drawText(text, event.x, event.y + offset2, true);
		        	break;
		        }
		        case SWT.EraseItem: {
		        	event.detail &= ~SWT.FOREGROUND;
		        	break;
		        }
			}
		}
		String getText(TableItem item, int column) {
			return item.getText(column);
		}
	};
	
	/**
	 * Table Selection Listener
	 */
	TableItem selectedTI;
	public void setSelectedTI(TableItem ti) { selectedTI = ti; }
	public void clearSelectedTI() { selectedTI = null; }
	public boolean isSetSelectedTI() {
		return (selectedTI == null) ? false : true;
	}
	public boolean equalsSelectedTI(TableItem ti) {
		return (isSetSelectedTI()) ? selectedTI.equals(ti) : false;
	}
	
	Listener tableSelectionListener = new Listener() {
		@SuppressWarnings("static-access")
		public void handleEvent(Event event) {
			if(tabName.equals("Exchange"))
				return;
			
			if(!equalsSelectedTI((TableItem) event.item)) {
				setSelectedTI((TableItem) event.item);
				operationEdit.setVisible(true);
				operationDelete.setVisible(true);
				
				Scanner scan = new Scanner(new IO("db").read());
				while(scan.hasNext()) {
					String str = scan.nextLine();
					if(str.indexOf(selectedTI.getText()) > -1) {
						
						String[] fields = str.split(";");
						String sum = fields[2].substring(fields[2].indexOf("Sum: ") + "Sum: ".length());
						
						// Set Amount Field (Text)
						operationSum.setText(sum.substring(3));
						
						// Set Currency Field (Combo)
						int i = 0;
						for(String c : preferences.get("Currencies")) {
							if(c.equals(sum.substring(0, 3)))
								break;
							i++;
						}
						currency.select(i);
						
						// Set Source Field (Combo)
						i = 0;
						List<String> cat;
						if(tabName.equals("Credit"))
							cat = preferences.get("Expense Category");
						else
							cat = preferences.get("Income Source");
						for(String s : cat) {
							if(s.equals(fields[3].substring(fields[3].indexOf("Source: ") + "Source: ".length())))
								break;
							i++;
						}
						operationSource.select(i);
						
						// Set Location Field (Combo)
						i = 0;
						for(String l : preferences.get("Saving Location")) {
							if(l.equals(fields[4].substring(fields[4].indexOf("Location: ") + "Location: ".length())))
								break;
							i++;
						}
						locationSource.select(i);
						
						// Set Comment Field (Text)
						operationComment.setText(
								fields[5].substring(
										fields[5].indexOf("Comment: ") + "Comment: ".length()));
						
						break;
					}
				}
				scan.close();
			} else {
				clearSelectedTI();
				operationEdit.setVisible(false);
				operationDelete.setVisible(false);
				
				operationSum.setText("");
				currency.select(0);
				operationSource.select(0);
				locationSource.select(0);
				operationComment.setText("");
			}
		}
	};
	
	/**
	 * Button listener (Delete/Edit/Enter)
	 * 
	 * @param action
	 * @return
	 */
	public Listener operationButtonListener(String action) {
		Listener listener;
		
		switch(action) {
		
		case "Delete":
			listener = new Listener() {
				@SuppressWarnings("static-access")
				public void handleEvent(Event e) {
					if(!isSetSelectedTI())
		        		return;
		        	
		        	MessageBox mb = new MessageBox(e.display.getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		        	mb.setText("Warning!");
		        	mb.setMessage("Are you sure to delete the record?");
		        	
		        	switch(mb.open()) {
		        	case SWT.OK:
		        		
		        		// DELETE THE RECORD
		        		new IO("db").delete(selectedTI.getText());
		        		
		        		// Update BalanceBuilder after data was saved in previous step
		        		bb = new BalanceBuilder();
		        		
		        		// Update RESULT section
		        		fillTable(resultTable, tabName, 0);
		        		
		        		// Update LOG section
		        		fillTable(logTable, tabName, 1);
		        		
		        		// Update GRAPH section
		        		canvas.redraw();
		        		
		        		// Clear selectedTI
		        		clearSelectedTI();
		        		
		        		// Hide Buttons
		        		operationEdit.setVisible(false);
		        		operationDelete.setVisible(false);
		        		
		        		break;
		        	case SWT.CANCEL:
		        		// DO NOTHING
		        		break;
		        	}
				}
			};
			break;
		
		case "Edit":
			listener = new Listener() {
				@SuppressWarnings("static-access")
				public void handleEvent(Event event) {
					if(!isSetSelectedTI())
						return;
					
					// WORK HERE!
					
					StringBuilder sb = new StringBuilder(selectedTI.getText() +
							"; Sum: " + currency.getText() + " " + Validator.toDouble(operationSum.getText()) +
							"; Source: " + operationSource.getText() +
							"; Location: " + locationSource.getText() +
							"; Comment: " + operationComment.getText() +
							"\n");
					
					// Update FILE container
					new IO("db").edit(selectedTI.getText(), sb);
					
					// Update BalanceBuilder after data was saved in previous step
					bb = new BalanceBuilder();
					
					// Update RESULT section
					fillTable(resultTable, tabName, 0);
					
					// Update LOG section
					fillTable(logTable, tabName, 1);
					
					// Update GRAPH section
					canvas.redraw();
				}
			};
			break;
		
		default:
			listener = new Listener() {
				@SuppressWarnings("static-access")
				public void handleEvent(Event event) {
					
					SimpleDateFormat sdf = new SimpleDateFormat("Y-MM-dd H:mm:ss");
					StringBuilder sb = new StringBuilder();
					
					if(tabName == "Credit" || tabName == "Debit") {
						/**
						 * Add a corresponding operation (Credit or Debit)
						 * in the following format:
						 * 
						 * 2016-07-01 0:00:01;
						 * Credit;
						 * Sum: USD 0.00;
						 * Source: sss;
						 * Location: lll;
						 * Comment: ccc
						 */
						sb.append(sdf.format(new Date()) + "; " + tabName +
								"; Sum: " + currency.getText() + " " + Validator.toDouble(operationSum.getText()) +
								"; Source: " + operationSource.getText() +
								"; Location: " + locationSource.getText() +
								"; Comment: " + operationComment.getText() +
								"\n");
					} else if(tabName == "Exchange") {
						/**
						 * Add three operations:
						 * 
						 * First - make a credit operation from the sellLocation of
						 * the sellCurrency in the amount of sellAmount
						 * 
						 * Second - make a deposit operation to the buyLocation with
						 * the buytCurrency in the amount of buyAmount
						 * 
						 * Third - add an exchange rate at which currencies were traded
						 * to the StorageData("exchangerates.txt") provided that:
						 * - if there already is data for current date, parse it and
						 * add/change only the data for specific currency (USD shall
						 * always be considered a base currency)
						 * - if there are no data for current date, parse the most
						 * recent one, copy data and change data for the current pair
						 * - if there are no data at all, get list of currencies
						 * and set data to 1 for all pairs, save for the recently
						 * traded pair, for which actual data shall be stored
						 * 
						 * The proposed format is:
						 * 
						 * 2016-07-01 0:00:01;
						 * UAH1.01;
						 * USD1;
						 */
						String sCur = sellCurrency.getText();
						String bCur = buyCurrency.getText();
						double sAmount = Validator.toDouble(sellAmount.getText());
						double bAmount = Validator.toDouble(buyAmount.getText());
						String sLocation = sellLocation.getText();
						String bLocation = buyLocation.getText();
						sb.append(sdf.format(new Date()) + "; Credit; Sum: " + sCur + " " + sAmount + "; Source: None; Location: " + sLocation + "; Comment: Exchange\n");
						sb.append(sdf.format(new Date()) + "; Debit; Sum: " + bCur + " " + bAmount + "; Source: None; Location: " + bLocation + "; Comment: Exchange\n");
						
						/**
						 * Check to see if exchange currencies are different then
						 * proceed calculating exchange rates and save them.
						 * 
						 * Otherwise it's a pure relocation of the same currency
						 * between Saving Locations.
						 */
						if(!sCur.equals(bCur)) {
							ExchangeRate er = new ExchangeRate(sCur, bCur, sAmount, bAmount);
							er.save();
						}
					}
					
					// Update FILE container
					new IO("db").save(sb);
					
					// Update BalanceBuilder after data was saved in previous step
					bb = new BalanceBuilder();
					
					// Update RESULT section
					fillTable(resultTable, tabName, 0);
					
					// Update LOG section
					fillTable(logTable, tabName, 1);
					
					// Update GRAPH section
					canvas.redraw();
				}
			};
			break;
		}
		
		return listener;
	}
}