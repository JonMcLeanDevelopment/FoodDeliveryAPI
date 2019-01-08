package com.fiverr.fdapi;

import java.security.SecureRandom;

import com.zaxxer.hikari.HikariDataSource;

public class API {

	public static HikariDataSource ds; // Data Source
	public static SecureRandom rand = new SecureRandom(); // Secure Random
	
	static{
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // Driver Retrieval
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String address = "127.0.0.1";
		String username = "root";
		String password = ""; // 1bdEjRfyZw8d7x7
		String name = "fooddelivery";
		
		ds = new HikariDataSource();
		ds.setMaximumPoolSize(10);
		ds.setJdbcUrl("jdbc:mysql://localhost:3306/fooddelivery?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.addDataSourceProperty("serverName", address);
		ds.addDataSourceProperty("port", "3306");
		ds.addDataSourceProperty("databaseName", name);
		ds.addDataSourceProperty("user", username);
		ds.addDataSourceProperty("password", password);
	}
	
}
