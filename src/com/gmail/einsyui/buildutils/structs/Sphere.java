package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;

public class Sphere implements Struct {
	Location center;
	int radiusSquared;
	BlockIndex current;
	ObjectGen generator;
	
	public Sphere(Location center, int radius, ObjectGen generator){
		this.center=center;
		this.radiusSquared = radius*radius;
		this.generator=generator;
		current = BlockIndex.forCuboid(
				center.clone().subtract(radius, radius, radius), 
				center.clone().add(radius, radius, radius)); 
	}

	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks;i++){
			Location l = current.get();
			if(l==null) return;
			if(l.distanceSquared(center)<=radiusSquared)
				generator.generateAt(l);
			current.increment();
		}
	}

	@Override
	public boolean isReady() {
		return current.isAtMax();
	}

}
