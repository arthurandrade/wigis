package net.wigis.yun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class hashTableSort {
	
	public static List sortByKeyDesc( final Hashtable map){
		List keys = new ArrayList();
		keys.addAll(map.keySet());
		Collections.sort(keys, new Comparator(){

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				//Object v1 = map.get(arg0);
				//Object v2 = map.get(arg1);
				if(arg0 == null){
					return (arg1 == null) ? 0 : 1;
				}else if(arg0 instanceof Comparable){
					return ((Comparable) arg1).compareTo(arg0);
				}else{
					return 0;
				}
			}
			
		});
		return keys;
	}
	
	public static List sortKeyDesc( final HashSet hashset){
		List keys = new ArrayList();
		keys.addAll(hashset);
		Collections.sort(keys, new Comparator(){

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				//Object v1 = map.get(arg0);
				//Object v2 = map.get(arg1);
				if(arg0 == null){
					return (arg1 == null) ? 0 : 1;
				}else if(arg0 instanceof Comparable){
					return ((Comparable) arg1).compareTo(arg0);
				}else{
					return 0;
				}
			}
			
		});
		return keys;
	}
	 
	public static List sortKeyAsc( final HashSet hashset){
		List keys = new ArrayList();
		keys.addAll(hashset);
		Collections.sort(keys, new Comparator(){

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				//Object v1 = map.get(arg0);
				//Object v2 = map.get(arg1);
				if(arg0 == null){
					return (arg1 == null) ? 0 : 1;
				}else if(arg0 instanceof Comparable){
					return ((Comparable) arg0).compareTo(arg1);
				}else{
					return 0;
				}
			}
			
		});
		return keys;
	}
	
	public static List sortByValueDesc(final Hashtable map){
		List keys = new ArrayList();
		keys.addAll(map.keySet());
		Collections.sort(keys, new Comparator(){

			@Override
			public int compare(Object arg0, Object arg1) {
				// TODO Auto-generated method stub
				Object v1 = map.get(arg0);
				Object v2 = map.get(arg1);
				if(v1 == null){
					return (v2 == null) ? 0 : 1;
				}else if(v2 instanceof Comparable){
					return ((Comparable) v2).compareTo(v1);
				}else{
					return 0;
				}
			}
			
		});
		return keys;
	}
}
