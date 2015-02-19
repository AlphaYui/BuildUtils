package com.gmail.einsyui.structs;

import org.bukkit.Location;

import com.gmail.einsyui.ObjectGen;
import com.gmail.einsyui.Struct;
import com.gmail.einsyui.geometry.BlockIndex2D;

public class Plane implements Struct {
	BlockIndex2D index;
	ObjectGen generator;
	
	public Plane(Location start, Location to1, Location to2, ObjectGen generator){
		index = new BlockIndex2D(start, to1, to2);
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
		return index.atEnd();
	}

}
