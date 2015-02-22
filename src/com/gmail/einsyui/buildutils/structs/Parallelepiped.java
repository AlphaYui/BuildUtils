package com.gmail.einsyui.buildutils.structs;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;
import com.gmail.einsyui.buildutils.geometry.BlockIndex;

public class Parallelepiped implements Struct {
	BlockIndex index;
	ObjectGen generator;
	
	public Parallelepiped(Location start, Location to1, Location to2, Location to3, 
			ObjectGen generator){
		index = new BlockIndex(start, to1, to2, to3);
		this.generator = generator;
	}
	
	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks;i++){
			Location n = index.get();
			if(n==null) return;
			generator.generateAt(n);
			index.increment();
		}
	}

	@Override
	public boolean isReady() {
		return index.isAtMax();
	}
}
