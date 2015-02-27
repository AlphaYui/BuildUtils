package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;
import com.gmail.einsyui.buildutils.geometry.CoordinatePlane;

public class Triangle implements Struct {
	CoordinatePlane plane;
	BlockIndex index;
	double darea;
	double x2, y2, x3, y3;
	ObjectGen generator;
	
	public Triangle(Location p1, Location p2, Location p3, ObjectGen generator){
		plane = new CoordinatePlane(p1.clone(), p2.clone(), p3.clone());
		this.generator=generator;
		// x1=y1=0
		x2=plane.getX(p2);
		y2=plane.getY(p2);
		x3=plane.getX(p3);
		y3=plane.getY(p3);
		darea = (-y2*x3 + x2*y3); // doubled, signed area of the triangle
		
		index = new BlockIndex(p1, p2, p3);
	}
	
	public boolean contains(Location p){
		double x=plane.getX(p);
		double y=plane.getY(p);
		double s = (y3*x - x3*y);
		double t = (-y2*x + x2*y);
		if(darea<0)
			return s<=0 && t<=0 && s+t>=darea;
		else
			return s>=0 && t>=0 && s+t<=darea;
	}

	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks;i++){
			Location n = index.get();
			if(n==null) return;
			while(!contains(n)){
				index.increment(); 
				n=index.get();
				if(n==null) return;
			}
			generator.generateAt(n);
			index.increment();
		}
	}

	@Override
	public boolean isReady() {
		return index.isAtMax();
	}

}
