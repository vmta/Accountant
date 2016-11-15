/**
 * Class PreferenceData extends class Data with several specific
 * functions, applicable exclusively to the Preferences.
 * 
 * @author vmta
 * @version 0.1
 * @date 2016/08/17
 */

package accountant;
import java.util.Scanner;

public class PreferenceData extends Data {
	
	static final long serialVersionUID = 1;
	private static String fileName = "preferences";
	
	/**
	 * Default constructor.
	 * 
	 *  @author vmta
	 *  @version 0.1
	 *  @date 2016/08/17
	 */
	public PreferenceData() {
		super(fileName);
	}
	
	/**
	 * Save a newly defined preference.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param preference
	 * @param name
	 */
	public void save(String preference, String name) {
		save(preference, name, null);
	}
	
	/**
	 * Reloaded Save function.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param preference
	 * @param name
	 * @param nesting
	 */
	public void save(String preference, String name, String nesting) {
		save(preference, name, nesting, null);
	}
	
	/**
	 * Reloaded Save function.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param preference
	 * @param name
	 * @param nesting
	 * @param comments
	 */
	@SuppressWarnings("static-access")
	public void save(String preference, String name, String nesting, String comments) {
		IO io = new IO(fileName);
		Scanner scan = new Scanner(io.read());
		String s;
		String str;
		StringBuilder sb1 = new StringBuilder();
		while(scan.hasNext()) {
			s = scan.nextLine();
			if(s.indexOf(preference) > -1) {
				if (nesting == null) {
					s = s.substring(0, s.indexOf(preference) + preference.length() + 1) + name + ";";
				} else if(s.indexOf(nesting) > -1) {
					str = s.substring(0, s.indexOf(nesting) + nesting.length() + 1);
					str += name + ";";
					str += s.substring(s.indexOf(nesting) + nesting.length() + 1);
					s = str;
				} else {
					s = s + name + ";";
				}
			}
			sb1.append(s + "\n");
		}
		scan.close();
		io.write(sb1.toString());
	}
}