package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;
import com.gmail.einsyui.buildutils.geometry.CoordinatePlane;

public class Triangle implements Struct {
	CoordinatePlane plane;
	BlockIndex index;
	double x2, y2, x3, y3, y2Ox2, n2;
	ObjectGen generator;
	
	public Triangle(Location p1, Location p2, Location p3, ObjectGen generator){
		plane = new CoordinatePlane(p1, p2, p3);
		// x1=y1=0
		x2=plane.getX(p2);
		y2=plane.getY(p2);
		x3=plane.getX(p3);
		y3=plane.getY(p3);
		y2Ox2=y2/x2;
		n2=y3-y2Ox2*x3;
		index = new BlockIndex(p1, p2, p3);
	}
	
	public boolean contains(Location p){
		double x=plane.getX(p);
		double y=plane.getY(p);
		double l3 =(y-y2Ox2*x)/n2;
		double l2=(x-l3*x3)/x2;
		return l2>=0 && l3>=0;
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
