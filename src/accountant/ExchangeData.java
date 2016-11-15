/**
 * Class ExchangeData extends class Data with several specific
 * functions, applicable exclusively to the Exchanges.
 * 
 * @author vmta
 * @version 0.1
 * @date 2016/08/17
 */

package accountant;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExchangeData extends Data {
	
	static final long serialVersionUID = 1;
	private static String fileName = "exchangerates";
	
	/**
	 * Default constructor.
	 * 
	 *  @author vmta
	 *  @version 0.1
	 *  @date 2016/08/17
	 */
	public ExchangeData() {
		super(fileName);
	}
	
	/**
	 * Retrieve the date of the last available exchange operation,
	 * which will become a first key.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getFirstKey() {
		String s = new IO(fileName).read();
		return s.substring(0, s.indexOf("="));
	}
	
	/**
	 * StringBuilder parameter "operation" contains most recent
	 * operation to be stored in the Storage. Make it the first
	 * operation in the list, then parse the existing data from
	 * StorageData and write it all back down save the most recent
	 * record of current date shall be unique (i.e. no matter
	 * how many exchanges during one day is performed, the record
	 * shall contain only the most recent ratios).
	 * 
	 * @author vmta
	 * @version 0.2
	 * @date 2016/09/29
	 * 
	 * @param operation
	 */
	@SuppressWarnings("static-access")
	public static void save(StringBuilder operation) {
		IO io = new IO(fileName);
		Scanner scan = new Scanner(io.read());
		StringBuilder selected = new StringBuilder();
		String s = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		while(scan.hasNext()) {
			s = scan.nextLine();
			if(s.indexOf(df.format(new Date())) < 0) {
				selected.append(s + "\n");
			}
		}
		scan.close();
		io.write(operation.append(selected).toString());
	}
}