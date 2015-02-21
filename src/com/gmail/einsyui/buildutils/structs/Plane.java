package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;

public class Plane implements Struct {
	BlockIndex index;
	ObjectGen generator;
	
	public Plane(Location start, Location to1, Location to2, ObjectGen generator){
		index = new BlockIndex(start, to1, to2);
		this.generator = generator;
	}
	
	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks;i++){
			Location n = index.next();
			if(n==null) return;
			generator.generateAt(n);
		}
	}

	@Override
	public boolean isReady() {
		return index.isAtMax();
	}

}
