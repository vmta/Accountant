/**
 * Class BalanceBuilder holds and operates with data in
 * order to calculate balances corresponding to Location
 * and Currency.
 * 
 * @author vmta
 * @version 0.1
 * @date 2016/08/17
 */

package accountant;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BalanceBuilder {
	
	/**
	 * General Data holder.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 */
	private Data preferences = null;
	private Data exchangerates = null;
	
	private List<String> incomes;
	private List<String> expenses;
	private List<String> locations;
	private List<String> currencies;
	private List<String> exchanges;
	
	/**
	 * Balance holders.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 */
	private Map<String, Double> balanceIncome;
	private Map<String, Double> balanceExpense;
	private Map<String, Map<String, Double>> balanceCurrencyLocation;
	private Map<String, Double> balanceCurrency;
	
	/**
	 * Get arrays of currencies and locations from the StorageData,
	 * then calculate and put into corresponding Maps balances as
	 * values for specific keys. Currency balances are simple map
	 * holding pairs of currencies/balances as key/values, whereas
	 * Currencies/Locations map is a bit more complicated and holds
	 * locations as keys and another map object as a value. The inner
	 * map consists of currencies/balances as keys/values.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 */
	public BalanceBuilder() {
		preferences = new Data("preferences");
		exchangerates = new ExchangeData();
		
		locations = preferences.get("Saving Location");
		currencies = preferences.get("Currencies");
		exchanges = exchangerates.values().iterator().next();
		
		balanceCurrencyLocation = new HashMap<String, Map<String, Double>>();
		for(String location : locations) {
			Map<String, Double> tmp = new HashMap<String, Double>();
			for(String currency : currencies) {
				tmp.put(currency, setBalance(location, currency));
			}
			balanceCurrencyLocation.put(location, tmp);
		}
		
		balanceCurrency = new HashMap<String, Double>();
		for(String currency : currencies) {
			balanceCurrency.put(currency, setBalance(currency));
		}
	}
	
	/**
	 * Get total for specific Location/Currency/Operation.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/16
	 * 
	 * @param location
	 * @param currency
	 * @param operation
	 * @return
	 */
	@SuppressWarnings("static-access")
	public double getOperationTotal(String location, String currency, String operation) {
		double var = 0.00;
		Scanner scan = new Scanner(new IO("db").read());
		while(scan.hasNext()) {
			String str = scan.nextLine();
			if(location != null) {
				if(str.indexOf(location) > 0 && str.indexOf(currency) > 0 && str.indexOf(operation) > 0) {
					int beginIndex = str.indexOf(currency) + currency.length() + 1;
					str = str.substring(beginIndex);
					int endIndex = str.indexOf(";");
					str = str.substring(0, endIndex);
					var += Double.parseDouble(str);
				}
			} else {
				if(str.indexOf(currency) > 0 && str.indexOf(operation) > 0) {
					int beginIndex = str.indexOf(currency) + currency.length() + 1;
					str = str.substring(beginIndex);
					int endIndex = str.indexOf(";");
					str = str.substring(0, endIndex);
					var += Double.parseDouble(str);
				}
			}
		}
		scan.close();
		return var;
	}
	
	/**
	 * Set balance for specific Date/Currency/Source(Category)/Operation.
	 * 
	 * @author vmta
	 * 
	 * @version 0.2
	 * @date 2016/09/06
	 * Added LocalDateTime in order to get balance
	 * for operations made only during certain period.
	 * 
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param source
	 * @param operation
	 * @return
	 */
	@SuppressWarnings("static-access")
	public double setBalance(LocalDateTime argDate, String currency, String inout, String operation) {
		double tmp = 0.00;
		
		Scanner scan = new Scanner(new IO("db").read());
		while(scan.hasNext()) {
			String str = scan.nextLine();
			
			LocalDateTime dbDate = LocalDateTime.parse(
					str.substring(0, str.indexOf(";")),
					DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
			
			if(dbDate.isAfter(argDate) && str.indexOf(operation) > -1 && str.indexOf(currency) > -1 && str.indexOf(inout) > -1) {
				int beginIndex = str.indexOf(currency) + currency.length() + 1;
				str = str.substring(beginIndex);
				int endIndex = str.indexOf(";");
				str = str.substring(0, endIndex);
				for(String exchange : exchanges) {
					if(exchange.indexOf(currency) > -1) {
						tmp += Double.parseDouble(str) / Double.parseDouble(exchange.substring(3));
					}
				}
			}
		}
		scan.close();
		double var = new BigDecimal(tmp)
			    .setScale(3, BigDecimal.ROUND_HALF_UP)
			    .doubleValue();
		return var;
	}
//	@SuppressWarnings("static-access")
//	public double setBalance(int i, String currency, String inout, String operation) {
//		double tmp = 0.00;
//		Scanner scan = new Scanner(new IO("db").read());
//		while(scan.hasNext()) {
//			String str = scan.nextLine();
//			if(str.indexOf(operation) > -1 && str.indexOf(currency) > -1 && str.indexOf(inout) > -1) {
//				int beginIndex = str.indexOf(currency) + currency.length() + 1;
//				str = str.substring(beginIndex);
//				int endIndex = str.indexOf(";");
//				str = str.substring(0, endIndex);
//				for(String exchange : exchanges) {
//					if(exchange.indexOf(currency) > -1) {
//						tmp += Double.parseDouble(str) / Double.parseDouble(exchange.substring(3));
//					}
//				}
//			}
//		}
//		scan.close();
//		double var = new BigDecimal(tmp)
//			    .setScale(3, BigDecimal.ROUND_HALF_UP)
//			    .doubleValue();
//		return var;
//	}
	
	/**
	 * Set balance for specific Location/Currency.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param location
	 * @param currency
	 * @return
	 */
	public double setBalance(String location, String currency) {
		double var = new BigDecimal(getOperationTotal(location, currency, "Debit") - getOperationTotal(location, currency, "Credit"))
		    .setScale(3, BigDecimal.ROUND_HALF_UP)
		    .doubleValue();
		return var;
	}
	
	/**
	 * Get balance for specific Location/Currency.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param location
	 * @param currency
	 * @return
	 */
	public double getBalance(String location, String currency) {
		Map<String, Double> tmp = balanceCurrencyLocation.get(location);
		return tmp.get(currency);
	}
	
	/**
	 * Set balance for specific Currency.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param currency
	 * @return
	 */
	public double setBalance(String currency) {
		double var = new BigDecimal(getOperationTotal(null, currency, "Debit") - getOperationTotal(null, currency, "Credit"))
				.setScale(3, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return var;
	}
	
	/**
	 * Get balance for specific Currency.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param currency
	 * @return
	 */
	public double getBalance(String currency) {
		return balanceCurrency.get(currency);
	}
	
	/**
	 * Set income balances.
	 * 
	 * @author vmta
	 * 
	 * @version 0.2
	 * @date 2016/09/06
	 * Added LocalDateTime in order to get balance
	 * for operations made only during this month.
	 * 
	 * @version 0.1
	 * @date 2016/08/17
	 */
	public void setIncomeBalance() {
		incomes = preferences.get("Income Source");
		balanceIncome = new HashMap<String, Double>();
		for(String income : incomes) {
			double var = 0.0;
			for(String currency : currencies) {
				//var += setBalance(0, currency, income, "Debit");
				
				// Calculate Income Balance for 1 month
//				var += setBalance(LocalDateTime.now().minusMonths(1), currency, income, "Debit");
				
				// Calculate Income Balance for current month
				var += setBalance(LocalDateTime.now().withDayOfMonth(1), currency, income, "Debit");
			}
			balanceIncome.put(income, var);
		}
	}
	
	/**
	 * Get balance for specific Income Source.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param source
	 * @return
	 */
	public double getIncomeBalance(String source) {
		if(balanceIncome == null)
			setIncomeBalance();
		return balanceIncome.get(source);
	}
	
	/**
	 * Set expense balances.
	 * 
	 * @author vmta
	 * 
	 * @version 0.2
	 * @date 2016/09/06
	 * Added LocalDateTime in order to get balance
	 * for operations made only during this month.
	 * 
	 * @version 0.1
	 * @date 2016/08/17
	 */
	public void setExpenseBalance() {
		expenses = preferences.get("Expense Category");
		balanceExpense = new HashMap<String, Double>();
		for(String expense : expenses) {
			double var = 0.0;
			for(String currency : currencies) {
				//var += setBalance(1, currency, expense, "Credit");
				
				// Calculate Expense Balance for 1 month
//				var += setBalance(LocalDateTime.now().minusMonths(1), currency, expense, "Credit");
				
				// Calculate Expense Balance for current month
				var += setBalance(LocalDateTime.now().withDayOfMonth(1), currency, expense, "Credit");
			}
			balanceExpense.put(expense, var);
		}
	}
	
	/**
	 * Get balance for specific Expense Category.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param category
	 * @return
	 */
	public double getExpenseBalance(String category) {
		if(balanceExpense == null)
			setExpenseBalance();
		return balanceExpense.get(category);
	}
}