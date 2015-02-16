package com.gmail.marzipankaiser.buildutils.geometry;

import java.util.Map;

import org.bukkit.util.Vector;



public class Utils {

	public static Vector getComponentInDirection(Vector v, Vector direction){
		direction = direction.normalize();
		return direction.multiply(direction.dot(v));
	}
	public static Vector getComponentOrthogonalTo(Vector v, Vector normal){
		return v.subtract(getComponentInDirection(v, normal));
	}
	public static double getLengthInDirection(Vector v, Vector direction) {
		direction = direction.normalize();
		return direction.dot(v);
	}
	
	public static Object getWithDefault( Map<String,Object>args, String name, Object def )
	{
		Object o = args.get( name );
		if( o == null )
			return def;
		else
			return o;
	}
}
