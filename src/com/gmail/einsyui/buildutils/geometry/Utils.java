package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.util.Vector;



public class Utils {

	public static Vector getComponentInDirection(Vector v, Vector direction){
		Vector direction2 = direction.clone().normalize();
		return direction2.multiply(direction2.dot(v));
	}
	public static Vector getComponentOrthogonalTo(Vector v, Vector normal){
		return v.clone().subtract(getComponentInDirection(v, normal));
	}
	public static double getLengthInDirection(Vector v, Vector direction) {
		Vector direction2 = direction.clone().normalize();
		return direction2.dot(v);
	}
	public static Vector getAnOrthogonalVector(Vector v){
		return v.clone().add(ex).crossProduct(v).normalize();
	}
	
	public static final Vector ex=new Vector(1,0,0);
	public static final Vector ey=new Vector(0,1,0);
	public static final Vector ez=new Vector(0,0,1);
}
