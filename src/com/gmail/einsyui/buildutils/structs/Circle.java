package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;
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
		this.center=l1.clone().add(l2).add(l3).multiply(1/3);
		outerRadiusSquared = (int) Math.ceil(l1.distanceSquared(center));
		innerRadiusSquared = (int) Math.pow(Math.sqrt(outerRadiusSquared)-1, 2); 
		Vector v1=l1.toVector().subtract(center.toVector());
		Vector v2=(l2.toVector().crossProduct(v1)).crossProduct(v1) //TODO: may be a problem for special cases?
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
