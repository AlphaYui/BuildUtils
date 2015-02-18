package com.gmail.einsyui.structs;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.gmail.einsyui.ObjectGen;
import com.gmail.einsyui.Struct;

public class Line implements Struct {
	Location current;
	Location end;
	Vector direction;
	ObjectGen generator;
	int todo;
	
	public Line(Location from, Location to, ObjectGen generator){
		current=from;
		end=to;
		direction=to.subtract(from).toVector();
		todo=(int) Math.ceil(direction.length());
		direction=direction.normalize();
	}

	@Override
	public void generate(int numberOfBlocks) {
		for(int i=0;i<numberOfBlocks && todo>0;i++){
			generator.generateAt(current);
			current=current.add(direction); 
			todo--;
		}
	}

	@Override
	public boolean isReady() {
		return todo<=0;
	}

}
