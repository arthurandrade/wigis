package net.wigis.svetlin;

import java.awt.Color;

import net.wigis.graph.dnv.utilities.Vector3D;

public class __Color
{
	public static String getColorHtmlFromVector3D(Vector3D v)
	{
		// DOESNT WORK FOR 0.0 ?
		
		String s = "#";
		
		Color c = new Color(v.getX(), v.getY(), v.getZ());
    	s += Integer.toHexString(c.getRGB() & 0x00ffffff);
    	
//    	System.out.println(v.getX());
//    	System.out.println(v.getY());
//    	System.out.println(v.getZ());
//    	System.out.println(s);
		
		return s;
	}
}
