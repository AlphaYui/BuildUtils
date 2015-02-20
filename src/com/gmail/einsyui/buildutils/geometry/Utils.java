package com.gmail.einsyui.buildutils.geometry;

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
}
