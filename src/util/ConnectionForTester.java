package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionForTester {
	

	public Connection getB2BEDIQA1_DEV_DBConn() throws Exception {
		String envname = "ediqa1";
		String dbip = "b2bdbqa4";
		String port = "1521";
		String sid = "b2bqa4";
		String user = "b2b_app";
		String psw = "b2bapp";
		
		return getConnection(dbip, port, sid, user, psw, envname);
	}
	
	public Connection getB2BEDIQA1_DEVPLSQL_DBConn() throws Exception {
		String envname = "ediqa1";
		String dbip = "b2bdbqa3";
		String port = "1521";
		String sid = "b2bqa3";
		String user = "b2b_plsql";
		String psw = "b2bplsql";
		
		return getConnection(dbip, port, sid, user, psw, envname);
	}
	
	private Connection getConnection(String dbip, String port, String sid, String user, String psw, String envname) throws Exception {
		Connection conn=null;
		String url = "jdbc:oracle:thin:@"+dbip+":"+port+":"+sid;
		String className="oracle.jdbc.driver.OracleDriver";
		Class.forName(className);
		conn=DriverManager.getConnection(url, user, psw);
		
		System.out.println(envname+" DB connection init.");
		
		return conn;
	}
}
