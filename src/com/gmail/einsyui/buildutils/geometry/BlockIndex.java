package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockIndex extends LocationIndex {

	public BlockIndex(Location start, Location...locations){
		super(start, locations);
	}
	public BlockIndex(Location start, Vector...directions){
		super(start, directions);
	}
	
	@Override
	public Location get(){
		return super.get().getBlock().getLocation();
	}
	
	public static BlockIndex forCuboid(Location a, Location b){
		Vector d=b.subtract(a).toVector();
		return new BlockIndex(a,
				Utils.getComponentInDirection(d, Utils.ex),
				Utils.getComponentInDirection(d, Utils.ey),
				Utils.getComponentInDirection(d, Utils.ez));
	}
}
