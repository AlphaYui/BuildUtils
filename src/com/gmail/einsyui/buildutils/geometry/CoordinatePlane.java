package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;


public class CoordinatePlane extends MPlane {

	Vector ex, ey;
	
	public CoordinatePlane(Location origin, Vector x, Vector y) {
		super(origin, x, y);
		ex = x.normalize();
		ey = Utils.getComponentOrthogonalTo(y, ex).normalize();
	}
	public CoordinatePlane(Location origin, Location tox, Location toy){
		this(origin, tox.clone().subtract(origin).toVector(), 
				toy.clone().subtract(origin).toVector());
	}
	public double getH(Location l){
		return getSignedDistance(l);
	}
	public double getX(Location l){
		Location n=getNearest(l);
		return Utils.getLengthInDirection(n.subtract(origin).toVector(), ex);
	}
	public double getY(Location l){
		Location n=getNearest(l);
		return Utils.getLengthInDirection(n.subtract(origin).toVector(), ey);
	}
	
	public Location fromXY(double x, double y){
		return origin.add(ex.multiply(x)).add(ey.multiply(y));
	}
	public Location fromXYH(double x, double y, double h){
		return origin.add(ex.multiply(x)).add(ey.multiply(y))
				.add(normal.normalize().multiply(h));
	}

}