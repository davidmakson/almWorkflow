package br.com.alm.workflow.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import br.com.alm.workflow.exception.BusinesException;
import br.com.alm.workflow.util.Constants;
/**
 * @author David Makson do Nascimento Tavares
 */
public class ConnectionFactory {

	Properties prop = new Properties();
	private static ConnectionFactory instance = new ConnectionFactory();
	public String URL = "";
	public String USER = "";
	public String PASSWORD = "";
	public String DRIVER_CLASS = "";
	
	private ConnectionFactory() {

		try (InputStream inputStream = new FileInputStream(new File("C:/workflowAlm/config/" + Constants.APP_FILE))) {
			prop.load(inputStream);
		
			this.URL = prop.getProperty("URL");
			this.USER = prop.getProperty("USER");
			this.PASSWORD = prop.getProperty("PASSWORD");
			this.DRIVER_CLASS = prop.getProperty("DRIVER_CLASS");
		
		} catch (Exception e) {
			System.out.println("Erro ao carregar arquivo properties: " + e.getMessage());
		}
		
		try {
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	} 
	
	private Connection createConnection() throws BusinesException, SQLException{
		Connection connection = null;
		connection = DriverManager.getConnection(URL, USER, PASSWORD);
		return connection;
	}
	
	public static Connection getConnection(){
		try {
			return instance.createConnection();
		} catch (BusinesException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
