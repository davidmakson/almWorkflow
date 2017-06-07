package br.com.alm.workflow.infraestructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {

	public static void close(ResultSet resultSet) {
		if (resultSet != null){
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void close(Connection con) {
		if(con != null){
			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public static void close(Statement st) {
		if(st != null){
			try {
				st.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
