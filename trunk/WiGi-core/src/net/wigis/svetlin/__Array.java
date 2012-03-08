package net.wigis.svetlin;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

public class __Array
{
	public static double getMin(double [] a)
	{
		double min = Integer.MAX_VALUE;
		
		for (int i=0; i<a.length; i++)
			if (a[i] < min)
				min = a[i];
		
		return min;
	}
	
	public static double getMax(double [] a)
	{
		double max = Integer.MIN_VALUE;
		
		for (int i=0; i<a.length; i++)
			if (a[i] > max)
				max = a[i];
		
		return max;
	}
	
	public static double[] sort (double[] a, boolean ascending)
	{
		Arrays.sort(a);
		
		if (!ascending)
			reverse(a);
		
		return a;
	}
	
	public static double[] reverse (double[] a)
	{
		ArrayUtils.reverse(a);
		
		return a;
	}
}
