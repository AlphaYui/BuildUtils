package com.gmail.einsyui.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Plane {

	Location origin;
	Vector normal;
	
	public Plane(Location origin, Vector normal){
		this.origin=origin; this.normal=normal;
	}
	public Plane(Location origin, Vector u, Vector v){
		this.origin = origin;
		this.normal = u.crossProduct(v).normalize();
	}
	public Plane(Location origin, Location p, Location q){
		this(origin, p.subtract(origin).toVector(), q.subtract(origin).toVector());
	}
	
	public Vector orthogonalProjection(Vector v){
		return Utils.getComponentOrthogonalTo(v, normal);
	}
	public Location getNearest(Location l){
		return Utils.getComponentOrthogonalTo((l.subtract(origin).toVector()), normal)
				.toLocation(l.getWorld());
	}
	
	public double getSignedDistance(Location l){
		return (l.subtract(origin).toVector()).dot(normal.normalize());
	}
	public double distance(Location l){
		return Math.abs(getSignedDistance(l));
	}
}
