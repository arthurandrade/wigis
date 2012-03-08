package net.wigis.svetlin;

import java.text.DecimalFormat;

public class __Math
{
	public static void p(Object o)
	{
		System.out.println(o);
	}
	public static void pe(Object o)
	{
		System.err.println(o);
	}

	
	//  ".##", 123456.7811119			2 digits
	//	"###,###.###", 123456.789
	//	"###.##", 123456.789
	//	"000000.000", 123.78
	//	"$###,###.###", 12345.67
	public static double setPrecision(String pattern, double value)
	{
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		
		return Double.parseDouble(myFormatter.format(value));
	}
	
	
	public static void main (String[] args)
	{
		p(setPrecision(".##", 123456.7811119));
		
	}

}
