package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;
import com.gmail.einsyui.buildutils.geometry.CoordinatePlane;
import com.gmail.einsyui.buildutils.geometry.Utils;

public class Circle implements Struct {
	Location center; int outerRadiusSquared, innerRadiusSquared;
	BlockIndex index;
	ObjectGen fillGenerator, lineGenerator;
	
	public Circle(Location center, int radius, Vector normal,
			ObjectGen fillGenerator, ObjectGen lineGenerator){
		this.center=center;
		outerRadiusSquared = radius*radius;
		innerRadiusSquared = (radius-1)*(radius-1);
		Vector v1=Utils.getAnOrthogonalVector(normal).multiply(radius);
		Vector v2=v1.clone().crossProduct(normal).normalize().multiply(radius);
		index = new BlockIndex(center.clone().subtract(v1).subtract(v2),
							v1.multiply(2), v2.multiply(2));
		this.fillGenerator=fillGenerator;
		this.lineGenerator=lineGenerator;
	}
	public Circle(Location l1, Location l2, Location l3,
			ObjectGen fillGenerator, ObjectGen lineGenerator){
		CoordinatePlane cp = new CoordinatePlane(l1, l2, l3);
		double x2 = cp.getX(l2);
		double y2 = cp.getY(l2);
		double x3 = cp.getX(l3);
		double y3 = cp.getY(l3);
		double y2x3Ox2 = y2*x3/x2;
		double y3Ox3 = y3/x3;
		double ym = -(x2*x3+y2*y2x3Ox2)/(y2x3Ox2-y3)/2;
		double xm = -x3/2-y3*y3Ox3/2 -ym*y3Ox3;
		this.center=cp.fromXY(xm, ym);
		
		outerRadiusSquared = (int) Math.ceil(l1.distanceSquared(center));
		innerRadiusSquared = (int) Math.pow(Math.sqrt(outerRadiusSquared)-1, 2); 
		Vector v1=l1.toVector().subtract(center.toVector());
		Vector v2=cp.normal().clone().crossProduct(v1)
				.normalize().multiply(Math.sqrt(outerRadiusSquared));
		index = new BlockIndex(center.clone().subtract(v1).subtract(v2),
							v1.multiply(2), v2.multiply(2));
		this.fillGenerator=fillGenerator;
		this.lineGenerator=lineGenerator;
	}

	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks;i++){
			Location l = index.get();
			if(l==null) return;
			double dsq = l.distanceSquared(center);
			if(dsq<=outerRadiusSquared){
				if(dsq<=innerRadiusSquared){
					fillGenerator.generateAt(l);
				}else{
					lineGenerator.generateAt(l);
				}
			}
			index.increment();
		}
	}

	@Override
	public boolean isReady() {
		return index.isAtMax();
	}

}
