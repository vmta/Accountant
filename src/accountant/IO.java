/**
 * Class I(nput)O(utput) provides a low-level common
 * tools for accessing and manipulating final data.
 * 
 * Functions:
 * - read
 * - write
 * - save
 * - delete
 * - edit
 * 
 * @author vmta
 * 
 * @version 0.3 - Minor code cleanup
 * @date 2016/09/06
 * 
 * @version 0.2
 * @date 2016/08/17
 */

package accountant;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class IO {
	
	@SuppressWarnings("unused")
	private static String OS_NAME = System.getProperty("os.name");
	private static String USER_HOME = System.getProperty("user.home");
	@SuppressWarnings("unused")
	private static String USER_NAME = System.getProperty("user.name");
	private static String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static String DEFAULT_FILENAME = USER_HOME + FILE_SEPARATOR + "Documents" + FILE_SEPARATOR + "db";
	private static String fileName = "";
	private static String getFileName() {
		return (fileName == "") ? DEFAULT_FILENAME : fileName;
	}
	@SuppressWarnings("static-access")
	private void setFileName(String fileName) {
		this.fileName = USER_HOME + FILE_SEPARATOR + "Documents" + FILE_SEPARATOR + fileName;
	}
	
	/**
	 * Read whole file "fileName" as a string.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/09
	 * 
	 * @param fileName
	 * @return
	 */
	public static String read() {
		return read(getFileName());
	}
	private static String read(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			try {
				String s;
				while((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
				}
			} finally {
				in.close();
			}
		} catch(IOException e) {
			try {
				File f = new File(fileName);
				if(!f.exists())
					f.createNewFile();
			} catch(IOException ioe) {
				System.err.println("Can't create file " + fileName + ".");
			}
			System.err.println("Can't read file " + fileName + ".");
		}
		return sb.toString();
	}
	
	/**
	 * Write String "text" to a file "fileName".
	 * 
	 * WARNING!
	 * All previous data will be overwritten by parameter
	 * "text", thus to rather add new data to file, use
	 * function "append()".
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/09
	 * 
	 * @param fileName
	 * @param text
	 */
	public static void write(String text) {
		write(getFileName(), text);
	}
	public static void write(String fileName, String text) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			try {
				out.write(text);
			} finally {
				out.close();
			}
		} catch(IOException e) {
			System.err.println("Can't write to file " + fileName + ".");
		}
	}
	
	/**
	 * StringBuilder parameter "operation" contains most recent
	 * operation to be stored in the Storage. Make it the first
	 * operation in the list, then parse the existing data from
	 * StorageData and write it all back down.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/16
	 * 
	 * @param fileName
	 * @param operation
	 */
	public static void save(StringBuilder operation) {
		save(getFileName(), operation);
	}
	public static void save(String fileName, StringBuilder operation) {
		operation.append(read(fileName));
		write(fileName, operation.toString());
	}
	
	/**
	 * Get all data, parse, compare to the param and if it matches
	 * save data back excluding this record.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/16
	 * 
	 * @param fileName
	 * @param record
	 */
	public static void delete(String record) {
		delete(getFileName(), record);
	}
	public static void delete(String fileName, String record) {
		Scanner scan = new Scanner(read(fileName));
		StringBuilder selected = new StringBuilder();
		String s = null;
		
		while(scan.hasNext()) {
			s = scan.nextLine();
			if(s.indexOf(record) == -1) {
				selected.append(s + "\n");
			}
		}
		scan.close();
		write(fileName, selected.toString());
	}
	
	/**
	 * Get all data, parse, compare to the param and if it matches
	 * save data back replacing this record with pre-built operation.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/16
	 * 
	 * @param fileName
	 * @param record
	 * @param operation
	 */
	public static void edit(String record, StringBuilder operation) {
		edit(getFileName(), record, operation);
	}
	public static void edit(String fileName, String record, StringBuilder operation) {
		Scanner scan = new Scanner(read(fileName));
		StringBuilder selected = new StringBuilder();
		String s = null;
		
		while(scan.hasNext()) {
			s = scan.nextLine();
			if(s.indexOf(record) == -1) {
				selected.append(s + "\n");
			} else {
				selected.append(operation);
			}
		}
		scan.close();
		write(fileName, selected.toString());
	}
	
	/**
	 * Empty constructor.
	 * 
	 * @author vmta
	 * @version 0.1
	 * @date 2016/08/12
	 */
	public IO() {
		// Does nothing.
	}
	public IO(String fileName) {
		setFileName(fileName);
	}
}