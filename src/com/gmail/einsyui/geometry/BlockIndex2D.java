package com.gmail.einsyui.geometry;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BlockIndex2D {
	Location start;
	Location current, currentY;
	Vector dirX, dirY;
	int w,h; 
	int x,y;
	
	public BlockIndex2D(Location start, Location endX, Location endY) {
		this.start=start;
		current=start.clone(); currentY=start.clone();
		dirX=endX.subtract(start).toVector();
		dirY=endY.subtract(start).toVector();
		x=-1; y=0;
		w=1+(int) Math.ceil(dirX.length());
		h=1+(int) Math.ceil(dirY.length());
		dirX=dirX.normalize();
		dirY=dirY.normalize();
	}
	
	public Location next(){
		if(x==-1){
			x=0;
			return current;
		}
		x++;
		if(x>=w){
			y++;
			if(y>=h){
				x=w; y=h;
				return null;
			}else{
				currentY=currentY.add(dirY);
				current=currentY.clone(); x=0;
			}
		}else{
			current=current.add(dirX);
		}
		return current;
	}
	public boolean atEnd(){
		return x==w && y==h;
	}
}
