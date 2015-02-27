package com.gmail.einsyui.buildutils.structs;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.Struct;

public class PointsStruct implements Struct {
	List<Location> points;
	Iterator<Location> it;
	ObjectGen generator;
	public PointsStruct(List<Location> points, ObjectGen generator){
		this.points=points;
		this.it=points.iterator();
		this.generator=generator;
	}

	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks && it.hasNext();i++){
			generator.generateAt(it.next());
		}
	}

	@Override
	public boolean isReady() {
		return it.hasNext();
	}

}
