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

package net.wigis.graph.data.citeseer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// TODO: Auto-generated Javadoc
/**
 * The Class DataManager.
 * 
 * @author johno
 */
public class DataManager
{

	/** The our instance. */
	private static DataManager ourInstance;

	/** The conn. */
	private Connection conn = null;
	// the jdbc url should encode the type of database software, the
	// hostname,
	// and the name of the actual database name in one string.

	/** The DATABAS e_ url. */
	private final String DATABASE_URL = "jdbc:mysql://visual.mat.ucsb.edu/citeseer?";

	/** The DATABAS e_ username. */
	private final String DATABASE_USERNAME = "johno";

	/** The DATABAS e_ password. */
	private final String DATABASE_PASSWORD = "uhdhqk";

	/**
	 * Logger.
	 */
	// // private static Log logger = LogFactory.getLog( DataManager.class );

	/**
	 * Creates a new instance of DBManager
	 */
	public DataManager()
	{
	// try {
	// getConnection();
	// }
	// catch(Exception ex){
	// Logger.write( "in catch of constr in DataManager: " + ex );
	// }
	}

	/**
	 * this returns the only instance of this class. (implements the singleton
	 * design pattren)
	 * 
	 * @return single instance of DataManager
	 */
	public static synchronized DataManager getInstance()
	{
		if( ourInstance == null )
		{
			ourInstance = new DataManager();
		}
		return ourInstance;
	}

	/**
	 * Get connection with database and cache it in a private instance variable.
	 * Upon return from this function, there should be a 99% guarantee that the
	 * connection is still valid.
	 * 
	 * @return the connection
	 * @throws SQLException
	 *             the sQL exception
	 * @todo - should also check to see if cached connection still works
	 */
	public void getConnection() throws SQLException
	{
		// System.out.println("getConnection has been called");
		if( conn != null && conn.isValid( 1 ) )
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
			System.setProperty( "jdbc.drivers", drivers );
			try
			{

				// if i had unpacked my driver jar file, there would have been a
				// directory
				// called org, containing one called gjt, etc etc.

				Class.forName( drivers ).newInstance();
			}
			catch( Exception cnfex )
			{
				System.out.println( "class for driver not found" );
				cnfex.printStackTrace();
			}
			conn = DriverManager.getConnection( DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD );

			return;
		}

	}

	/**
	 * this method returns a ResultSet from a query string.
	 * 
	 * @param query
	 *            the query
	 * @return the results
	 */
	public ResultSet getResults( String query )
	{
		// System.out.println("this is the getRes query: "+query);
		Statement statement;
		ResultSet resultSet = null;
		// System.out.println(query);

		try
		{
			getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery( query );
		}
		catch( Exception ex )
		{
			System.out.println( "in catch of getResults: " + ex );
		}

		return resultSet;

	}

	/**
	 * Update table.
	 * 
	 * @param update
	 *            the update
	 * @return true, if successful
	 */
	public boolean updateTable( String update )
	{
		// System.out.println("Updating with: " +update);

		Statement statement;
		// ResultSet resultSet = null;

		try
		{
			// getConnection();
			statement = conn.createStatement();
			statement.executeUpdate( update );
			// System.out.println("update: " + update);
			return true;
		}
		catch( Exception ex )
		{
			System.out.println( "in catch of updateTable: " + ex );
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
	public String getStringResult( String query )
	{
		// System.out.println("this is the getRes query: "+query);
		Statement statement;
		ResultSet resultSet = null;
		String result = null;
		// System.out.println(query);

		try
		{
			getConnection();
			statement = conn.createStatement();
			resultSet = statement.executeQuery( query );
			resultSet.first();
			result = resultSet.getString( 1 );
		}
		catch( Exception ex )
		{
			System.out.println( "in catch of getResults: " + ex );
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
	public boolean executeUpdate( String update )
	{
		// System.out.println("Updating with: " +update);

		Statement statement;
		// ResultSet resultSet = null;

		try
		{
			// getConnection();
			statement = conn.createStatement();
			statement.execute( update );
			// System.out.println("update: " + update);
			return true;
		}
		catch( Exception ex )
		{
			System.out.println( "in catch of updateTable: " + ex );
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{

		return ( "This is MyDBManager" );
	}

	/*
	 * public boolean closeConnection(){ if(conn == null){ return false; } else{
	 * try{ conn.close(); } catch(Exception e){e.printStackTrace();} return
	 * true; }
	 * 
	 * }
	 */

}