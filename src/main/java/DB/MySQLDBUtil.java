package DB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class MySQLDBUtil {
  private String INSTANCE;
  private String PORT_NUM;
  public String DB_NAME;
  private String USERNAME;
  private String PASSWORD;
  public String URL;

  public MySQLDBUtil() {

	try {
		  InputStream inputStream = getClass().getClassLoader().getResourceAsStream("DBconfig.properties");

		  Properties prop = new Properties();
		  prop.load(inputStream);
	      INSTANCE=  prop.getProperty("INSTANCE");
	      PORT_NUM=  prop.getProperty("PORT_NUM");      
	      DB_NAME=  prop.getProperty("DB_NAME");	
	      USERNAME=  prop.getProperty("USERNAME");
	      PASSWORD=  prop.getProperty("PASSWORD");	

	      URL=  "jdbc:mysql://"+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME+ "?user=" + USERNAME + "&password=" + PASSWORD + "&autoReconnect=true&serverTimezone=UTC";	

	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.out.println("Working Directory = " + System.getProperty("env", "dev"));
		System.out.println("config file error");
		e.printStackTrace();
	}

  }
}