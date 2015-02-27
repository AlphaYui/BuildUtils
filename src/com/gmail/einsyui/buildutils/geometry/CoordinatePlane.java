package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;


public class CoordinatePlane extends MPlane {

	Vector ex, ey;
	
	public CoordinatePlane(Location origin, Vector x, Vector y) {
		super(origin, x.clone(), y.clone());
		ex = x.clone().normalize();
		ey = Utils.getComponentOrthogonalTo(y.clone(), ex).normalize();
	}
	public CoordinatePlane(Location origin, Location tox, Location toy){
		this(origin, tox.clone().subtract(origin).toVector(), 
				toy.clone().subtract(origin).toVector());
	}
	public double getH(Location l){
		return getSignedDistance(l);
	}
	public double getX(Location l){
		Vector v = orthogonalProjection(l.toVector().subtract(origin.toVector()));
		return Utils.getLengthInDirection(v, ex);
	}
	public double getY(Location l){
		Vector v = orthogonalProjection(l.toVector().subtract(origin.toVector()));
		return Utils.getLengthInDirection(v, ey);
	}
	
	public Location fromXY(double x, double y){
		return origin.clone().add(ex.clone().multiply(x)).add(ey.clone().multiply(y));
	}
	public Location fromXYH(double x, double y, double h){
		return origin.clone().add(ex.clone().multiply(x)).add(ey.clone().multiply(y))
				.add(normal.normalize().multiply(h));
	}

}