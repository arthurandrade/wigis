/******************************************************************************************************
 * Copyright (c) 2010, University of California, Santa Barbara
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of
 *      conditions and the following disclaimer in the documentation and/or other materials 
 *      provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 *****************************************************************************************************/

package net.wigis.svetlin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

// TODO: Auto-generated Javadoc
/**
 * The Class MySQL.
 * 
 * @author Svetlin Bostandjiev
 */
public class __MySQL
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	public static void pe(Object o)
	{
		System.err.println(o);
	}
	
	private static __MySQL ourInstance;
	
	private Connection con = null;
	
	public Connection getCon()
	{
		try
		{
			getConnection();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return con;
	}

	private String dbName;
	private String dbServer;
	private String dbPort;
	private String dbUserName;
	private String dbPassword;
	private String dbExtraParams;
	
	public __MySQL(String databaseName, String databaseServer, String databasePort, String dbUserName, String dbPassword, String dbExtraParams)
	{
		this.dbName = databaseName;
		this.dbServer = databaseServer;
		this.dbPort = databasePort;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
		this.dbExtraParams = dbExtraParams;
		
		try
		{
			getConnection();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public __MySQL()
	{
	}
	
	public static synchronized __MySQL getInstance()
	{
		if (ourInstance == null)
		{
			ourInstance = new __MySQL();
		}
		return ourInstance;
	}
	
	public static synchronized __MySQL getInstance(String databaseName, String databaseServer, String databasePort, String dbUserName, String dbPassword, String dbExtraParams)
	{
		if (ourInstance == null)
		{
			ourInstance = new __MySQL(databaseName, databaseServer, databasePort, dbUserName, dbPassword, dbExtraParams);
		}
		return ourInstance;
	}
	
	public void getConnection() throws SQLException
	{
		getConnection(dbName);
	}
	
	/**
	 * Get connection with database and cache it in a private instance variable.
	 * Upon return from this function, there should be a 99% guarantee that the
	 * connection is still valid. //
	 * 
	 * @param dbName
	 *            the database name
	 * @return the connection
	 * @throws SQLException
	 *             the sQL exception
	 * @todo - should also check to see if cached connection still works
	 */
	public void getConnection(String dbName) throws SQLException
	{
		// System.out.println("getConnection has been called");
		if (con != null && !con.isClosed())
		{
			return;
		}
		else
		{
			// System.out.println("there is a null connection: getting a
			// driver...");
			// String curDir = System.getProperty( "user.dir" );
			// System.out.println("current directory: " + curDir);
			String drivers = "com.mysql.jdbc.Driver";// "org.gjt.mm.mysql.Driver";
			// System.out.println("the driver path..: " +drivers);
			System.setProperty("jdbc.drivers", drivers);
			try
			{
				
				// if i had unpacked my driver jar file, there would have been a
				// directory
				// called org, containing one called gjt, etc etc.
				
				Class.forName(drivers).newInstance();
			}
			catch (Exception cnfex)
			{
				System.out.println("class for driver not found");
				cnfex.printStackTrace();
			}
			// the jdbc url should encode the type of database software, the
			// hostname,
			// and the name of the actual database name in one string.
			
			String url = null;
			String username = null;
			String password = null;
			
			// String machine = System.getProperty( "user.dir" );
			
			// System.out.println("User Directory is: " + machine);
			// if(machine.compareTo("C:\\jboss-3.2.2\\bin")==0){
			if (true)
			{
				
				url = "jdbc:mysql://" + dbServer + ":" + dbPort + "/" + dbName + "?" + dbExtraParams;
				username = dbUserName;
				password = dbPassword;
			}
			con = DriverManager.getConnection(url, username, password);
			
			return;
		}
		
	}
	
	public ResultSet getResults(String query)
	{
		// System.out.println("this is the getRes query: "+query);
		Statement statement;
		ResultSet resultSet = null;
		// System.out.println(query);
		
		try
		{
			statement = getCon().createStatement();
			resultSet = statement.executeQuery(query);
		}
		catch (SQLException sqlEx)
		{
			pe(query);
			pe("in catch of getResults: " + sqlEx);
			sqlEx.printStackTrace();
		}
		
		return resultSet;
		
	}
		
	public boolean updateTable(String update)
	{
		// System.out.println("Updating with: " +update);
		
		Statement statement;
		// ResultSet resultSet = null;
		
		try
		{
			statement = getCon().createStatement();
			statement.executeUpdate(update);
			// System.out.println("update: " + update);
			return true;
		}
		catch (SQLException sqlEx)
		{
			pe("in catch of updateTable: " + sqlEx);
			return false;
		}
	}
	
	/**
	 * this method returns a String result from a query string only to be used
	 * for single line result sets.
	 * 
	 * @param query
	 *            the query
	 * @return the string result
	 */
	public String getStringResult(String query)
	{
		// System.out.println("this is the getRes query: "+query);
		Statement statement;
		ResultSet resultSet = null;
		String result = null;
		// System.out.println(query);
		
		try
		{
			statement = getCon().createStatement();
			resultSet = statement.executeQuery(query);
			resultSet.first();
			result = resultSet.getString(1);
		}
		catch (SQLException sqlEx)
		{
			pe(query);
			pe("in catch of getStringResults: " + sqlEx);
			sqlEx.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Execute update.
	 * 
	 * @param update
	 *            the update
	 * @return true, if successful
	 */
	public boolean executeUpdate(String update)
	{
		// System.out.println("Updating with: " +update);
		
		Statement statement;
		// ResultSet resultSet = null;
		
		try
		{
			statement = getCon().createStatement();
			statement.execute(update);
			// System.out.println("update: " + update);
			return true;
		}
		catch (SQLException sqlEx)
		{
			pe("in catch of updateTable: " + sqlEx);
			return false;
		}
	}
	
	/**
	 * Close connection.
	 */
	public void closeConnection()
	{
		if (con != null)
		{
			try
			{
				con.close();
				con = null;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return ("This is MySQL");
	}
	
	// =================================================
	// alex
	// =================================================
	public boolean execute(String query)
	{
		Statement statement;
		
		try
		{
			statement = getCon().createStatement();
			statement.execute(query);
			return true;
		}
		catch (SQLException ex)
		{
			pe(query);
			pe("\tin catch of execute: " + ex);
			return false;
		}
		
	}
	
	// insert multiple rows
	public boolean insert (String table, ArrayList<String> keys, ArrayList<ArrayList<Object>> aValues)
	{
		String query = "INSERT INTO " + table;
		
		// keys
		query += " ( ";
		
		for (String key : keys)
		{
			query += key;
			
			if (keys.indexOf(key) != keys.size()-1)
				query += ",";
		}
		
		query += " ) ";	

		// values
		PreparedStatement ps;
		
		for (ArrayList<Object> values : aValues)
		{
			query += "SELECT ";
			
			for (Object value : values)
			{
				try
				{
					ps = getCon().prepareStatement("?");
					
					ps.setObject(1, value);
					
					query += __PreparedStatement.getQuery(ps);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
				}
				
				if (values.indexOf(value) != values.size()-1)
					query += ",";
			}
			
			query += " UNION ALL ";
		}
		
		// remove last UNION ALL
		query = query.substring(0, query.lastIndexOf("UNION ALL"));
		
		//pe(query);

		execute(query);

		return true;
	}
	
	public boolean exists(String table, String property, String value)
	{
		try
		{
			PreparedStatement ps;
			
			ps = getCon().prepareStatement("SELECT count(*) " + "FROM `" + table + "` WHERE " + property + " = ?");
			
			ps.setString(1, value);

			ResultSet rs = ps.executeQuery();
			
			rs.next();
			
			if (rs.getInt(1) == 1)
				return true;
			else
				return false;
		}
		//******************************************
		// ADD THIS IF YOU DONT CALL MySql.execute()
		catch (CommunicationsException e)
		{
			try
			{
				getConnection();
				return exists(table, property, value);
			}
			catch (SQLException sqle)
			{
				return false;
			}
		}
		//******************************************
		catch (SQLException e)
		{
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean exists(String table, String property1, String property2, String value1, String value2)
	{
		String query = "SELECT count(*) FROM `" + table + "` WHERE " + property1 + "='" + value1 + "' AND " + property2 + "='" + value2 + "'";
		
		if (getFirstInt(query) > 0)
			return true;
		else
			return false;
	}
	
	public boolean exists(String table, String property1, String property2, String property3, String value1, String value2, String value3)
	{
		String query = "SELECT count(*) FROM `" + table + "` WHERE " + property1 + "='" + value1 + "' AND " + property2 + "='" + value2 + "' AND " + property3 + "='" + value3 + "'";
		
		if (getFirstInt(query) > 0)
			return true;
		else
			return false;
	}
	
	public boolean exists(String table, String property1, String property2, String property3, String property4, String value1, String value2, String value3, String value4)
	{
		String query = "SELECT count(*) FROM `" + table + "` WHERE " + property1 + "='" + value1 + "' AND " + property2 + "='" + value2 + "' AND " + property3 + "='" + value3 + "' AND " + property4 + "='" + value4 + "'";
		
		if (getFirstInt(query) > 0)
			return true;
		else
			return false;
	}
	
	public int getFirstInt(String query)
	{
		String first = getFirst(query);
		if (first == null)
		{
			return Integer.MIN_VALUE;
		}
		
		return Integer.parseInt(first);
	}
	
	public String getFirst(String query)
	{
		ResultSet data = getResults(query);
		if (data != null)
		{
			try
			{
				while (data.next())
				{
					return data.getString(1);
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public ArrayList<String> getResultsInArrayList(String query, int nColumns)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		ResultSet data = getResults(query);
		try
		{
			while (data.next())
			{
				for (int i = 1; i <= nColumns; i++)
					a.add(data.getString(i));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return a;
	}
	
	public ArrayList<String> getAllFrom1Column(String query)
	{
		ArrayList<String> a = new ArrayList<String>();
		
		ResultSet data = getResults(query);
		try
		{
			while (data.next())
			{
				a.add(data.getString(1));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return a;
	}
	
	
	// =================================================
	
}
