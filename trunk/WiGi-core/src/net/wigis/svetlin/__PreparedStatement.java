package net.wigis.svetlin;

import java.sql.PreparedStatement;

public class __PreparedStatement
{
	public static String getQuery (PreparedStatement ps)
	{
		return ps.toString().substring(ps.toString().indexOf(":") + 2);
	}
}
