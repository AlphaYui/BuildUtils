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
