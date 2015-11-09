package shared;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//@@author A0131188H
public class SharedLogger {
	
	private static Logger logger = Logger.getLogger(SharedLogger.class.getName());

	public Logger getLogger() {
		return logger;
	}
	
	private static SharedLogger instance = null;
	
	public static SharedLogger getInstance() {
		try {
			if(instance == null) {
				prepareLogger();
        instance = new SharedLogger();
  		 }
  		 return instance;
		 } catch (SecurityException e) {
				System.out.println("A security violation has occurred: " + e.getMessage());
				return null;
			} catch (IOException e) {
				System.out.println("The log file is not found: " + e.getMessage());
				return null;
			}
   }
  
   private static void prepareLogger() throws SecurityException, IOException {
  	 FileHandler fileHandler = new FileHandler("log.txt");
  	 fileHandler.setFormatter(new SimpleFormatter());
  	 logger.addHandler(fileHandler);
  	 logger.setUseParentHandlers(false);
   }
   
}
