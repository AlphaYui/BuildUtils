package com.gmail.einsyui.buildutils.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockIndex2D extends BlockIndex {
	Location start;
	Vector x, y;
	Vector dirX, dirY;
	Vector maxX, maxY;
	
	public BlockIndex2D(Location start, Location endX, Location endY) {
		super(start, endX, endY);
	}
}
