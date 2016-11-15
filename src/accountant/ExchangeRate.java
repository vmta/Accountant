/**
 * Class ExchangeRate has to be initiated with buying and selling
 * currency identifiers along with the amounts of exchange.
 * 
 * The base currency (regardless of user preferences) is USDollar,
 * therefore in the easiest situation check if any of the trading
 * currencies is USD and proceed with a direct conversion
 * (Currency/USD).
 * 
 * If none of the trading currencies is USD it gets
 * a bit more complicated. Check if Buying currency has an exchange
 * rate (past conversions) to USD, and if it does, then apply
 * formula: (Selling Currency / Buying Currency) * Buying Currency
 * Exchange Rate to USD, i.e. (UAH / RUR) * RUR/USD, which shall
 * produce an UAH/USD exchange rate. In case Buying currency hasn't
 * been traded before, try to figure out if Selling currency has,
 * and apply same formula.
 * 
 * Once the exchange rates are defined, the data may be stored in
 * appropriate storage. Only one record will be stored for the single
 * day. The most recent exchanged pair rates will be replaced, while
 * the other rates will be transferred from the previous exchange.
 * Initially, the first exchange for the current date will be based
 * on the rates from the previous date (the last date the exchange
 * had place).
 * 
 * IN NO WAY THE RESULT OF SUCH CONVERSION/EXCHANGING RATING SHALL
 * BE CONSIDERED AN OFFICIAL (LEGAL) EXCHANGE RATE. IT ONLY SERVES
 * FOR THIS CURRENT SOFTWARE APPLICATION TO REPRESENT CERTAIN DATA
 * (BALANCES, INCOME/EXPENSE RESULTS, CHARTS). NO RESPOSIBILITY IS
 * TAKEN, NOR ASSURANCE IS PROVIDED BY THE AUTHORS HEREIN AS TO THE
 * ACCURACY OF THE EXCHANGE RATE DEFINITION/CALCULATION. FOR OFFICIAL
 * EXCHANGE RATES, PLEASE, VISIT APPROPRIATE ONLINE RESOURCES (I.E.
 * CENTRAL BANKS WEB SITES, EXCHANGE MARKETS, ETC.).
 * 
 * @author vmta
 * @version 0.2
 * @date 2016/08/17
 */

package accountant;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExchangeRate {
	
	private String sCur;
	private String bCur;
	double exchangeRateS = 1.00;
	double exchangeRateB = 1.00;
	
	private ExchangeData exch;
	private List<String> data;
	
	@SuppressWarnings("static-access")
	public ExchangeRate(String sCur, String bCur, double sAmount, double bAmount) {
		
		this.sCur = sCur;
		this.bCur = bCur;
		
		exch = new ExchangeData();
		data = exch.get(exch.getFirstKey());
		
		/**
		 * If Buying currency is USD (the base currency)
		 * then it's a straightforward formula of (Selling
		 * Currency)/USD ratio.
		 */
		if(bCur.equals("USD")) {
			exchangeRateS = calculateExchangeRate(sAmount, bAmount);
		}
		
		/**
		 * If Selling currency is USD, then apply a reverse
		 * formula of (Buying Currency)/USD.
		 */
		else if(sCur.equals("USD")) {
			exchangeRateB = calculateExchangeRate(bAmount, sAmount);
		}
		
		/**
		 * Otherwise, get the last available exchange rates.
		 * Check to see if the Selling and/or Buying Currency
		 * has a defined exchange ratio to USD. 
		 */
		else {
			for(String rate : data) {
				/**
				 * If EITHER bCur or sCur have no record!
				 */
				if(rate.indexOf(bCur) > -1) {
					exchangeRateS = calculateExchangeRate(sAmount, bAmount) * Validator.toDouble(rate.substring(3));
				} else {
					if(rate.indexOf(sCur) > -1) {
						exchangeRateB = calculateExchangeRate(bAmount, sAmount) * Validator.toDouble(rate.substring(3));
				//	} else {
				//		/*
				//		 * NO Previous DATA Available
				//		 * Shall we fetch data from National Bank???
				//		 */
				//		System.out.println("No data available");
					}
				}
			}
		}
	}
	
	private double calculateExchangeRate(double a, double b) {
		double var = new BigDecimal(a / b)
				.setScale(6, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return var;
	}
	
	@SuppressWarnings("static-access")
	public void save() {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder result = new StringBuilder();
		
		/**
		 * Append a today's date
		 */
		result.append(df.format(new Date()) + "=");
		
		/**
		 * Cycle through recorded Exchange Ratios
		 */
		for(String s : data) {
			
			/**
			 * Check if Exchange Rate Currency is any
			 * of sCur or bCur. If it is, change the
			 * corresponding ratio, otherwise save as
			 * it is.
			 */
			if(s.indexOf(sCur) < 0 && s.indexOf(bCur) < 0) {
				result.append(s);
			} else if(s.indexOf(sCur) > -1) {
				result.append(sCur + exchangeRateS);
			} else if(s.indexOf(bCur) > -1) {
				result.append(bCur + exchangeRateB);
			}
			result.append(";");
		}
		
		/**
		 * Do the Actual Save
		 */
		exch.save(result.append("\n"));
	}
}