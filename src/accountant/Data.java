/**
 * Class Data is the base point for accessing data in a form
 * of a HashMap with String as key and List<String> as value.
 * 
 * @author vmta
 * @version 0.1
 * @date 2016/08/11
 */

package accountant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Data extends HashMap<String, List<String>> {
	
	static final long serialVersionUID = 1;
	private String fileName = "preferences";
	
	/**
	 * Default constructor.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 */
	public Data() {
		new Data(fileName);
	}
	
	/**
	 * Reloaded constructor.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/17
	 * 
	 * @param fileName
	 */
	@SuppressWarnings("static-access")
	public Data(String fileName) {
		super();
		String data = new IO(fileName).read();
		Scanner scanner = new Scanner(data);
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			if(!line.equals("")) {
				String heading = line.split("=")[0];
				String[] options = (line.split("=")[1]).split(";");
				this.put(heading, Arrays.asList(options));
			}
		}
		scanner.close();
	}
}