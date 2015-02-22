package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockIndex extends MultiIndex<Location> {
	Location start;
	Vector[] directions;
	Vector[] maxs;
	Vector[] currents;

	public BlockIndex(Location start, Location...locations) {
		super(locations.length);
		this.start=start;
		this.maxs=new Vector[locations.length];
		this.currents=new Vector[locations.length];
		this.directions=new Vector[locations.length];
		for(int i=0;i<locations.length;i++){
			maxs[i]=locations[i].subtract(start).toVector();
			directions[i]=maxs[i].clone().normalize();
			currents[i]=new Vector(0,0,0);
		}
	}
	public BlockIndex(Location start, Vector...directions) {
		super(directions.length);
		this.start=start;
		this.maxs=new Vector[directions.length];
		this.currents=new Vector[directions.length];
		this.directions=new Vector[directions.length];
		for(int i=0;i<directions.length;i++){
			maxs[i]=directions[i].clone();
			this.directions[i]=maxs[i].clone().normalize();
			currents[i]=new Vector(0,0,0);
		}
	}

	@Override
	protected void incIndex(int i) {
		currents[i]=currents[i].add(directions[i]);
	}

	@Override
	protected void decIndex(int i) {
		currents[i]=currents[i].subtract(directions[i]);
	}

	@Override
	protected void resetIndexToMax(int i) {
		currents[i]=maxs[i].clone();
	}

	@Override
	protected void resetIndexToMin(int i) {
		currents[i]=new Vector(0,0,0);
	}

	@Override
	protected boolean isIndexAtMin(int i) {
		return currents[i].dot(directions[i])<0;
	}

	@Override
	protected boolean isIndexAtMax(int i) {
		return currents[i].dot(directions[i])>maxs[i].dot(directions[i]);
	}

	@Override
	public Location get() {
		Location res = start.clone();
		for(Vector current:currents)
			res=res.add(current);
		return res;
	}

}
