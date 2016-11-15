/**
 * Class Validator has no particular meaning and contains helper
 * functions for Comparing/Validating/Parsing/Converting various
 * variables.
 * 
 * @author vmta
 * @version 0.2
 * @date 2016/07/18
 *
 */

package accountant;

public class Validator {
	
	/**
	 * Check if param String s has "," and change it to "." 
	 * Check if param String s has " " and remove it
	 * in order to effectively parse and return double value.
	 * 
	 * If non-the-less a NumberFormatException occurs, then
	 * return "0.0" value.
	 * 
	 * @author vmta
	 * @date 2016/07/15
	 * 
	 * @param s
	 * @return double
	 */
	public static double toDouble(String s) {
		int i;
		if((i = s.indexOf(",")) > 0)
			s = s.substring(0, i) + "." + s.substring(i + 1);
		else if(i == 0)
			s = "." + s.substring(i + 1);
		
		if((i = s.indexOf(" ")) > 0)
			s = s.substring(0, i) + s.substring(i + 1);
		else if(i == 0)
			s = s.substring(i + 1);
		
		try {
			return Double.parseDouble(s);
		} catch(NumberFormatException e) {
			return 0.0;
		}
	}
	
	/**
	 * Check if param String s contains param String exception,
	 * remove it and return resulting value.
	 * 
	 * @author vmta
	 * @date 2016/07/18
	 * 
	 * @param s
	 * @param exception
	 * @return String
	 */
	public static String remove(String s, String exception) {
		int i;
		while((i = s.indexOf(exception)) > -1)
			s = (i == 0) ? s.substring(i + 1) : s.substring(0, i) + s.substring(i + 1);
		return s;
	}
	
	/**
	 * Check if param String s contains any of param String
	 * exceptions, remove it and return resulting value.
	 * 
	 * @author vmta
	 * @date 2016/07/18
	 * 
	 * @param s
	 * @param exceptions
	 * @return String
	 */
	public static String remove(String s, String[] exceptions) {
		for(String exception : exceptions)
			s = remove(s, exception);
		return s;
	}
	
	/**
	 * Check if param String s contains param String searchFor, do
	 * the replacement with param replaceWith and return resulting
	 * value.
	 * 
	 * @author vmta
	 * @date 2016/07/22
	 * 
	 * @param s
	 * @param searchFor
	 * @param replaceWith
	 * @return String
	 */
	public static String replace(String s, String searchFor, String replaceWith) {
		int i;
		while((i = s.indexOf(searchFor)) > -1)
			s = (i == 0) ? replaceWith + s.substring(i + searchFor.length()) : s.substring(0, i) + replaceWith + s.substring(i + searchFor.length());
		return s;
	}
}