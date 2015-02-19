package com.gmail.einsyui.geometry;

public class Index3D {
	int w, h, d;
	int x, y, z;
	public Index3D(int x, int y, int z, int w, int h, int d){
		this.w=w; this.h=h; this.d=d; this.x=x; this.y=y; this.z=z;
	}
	public void inc(){
		x++;
		if(x>=w){
			x=0; y++;
			if(y>=h){
				y=0; z++;
				if(z>=d){
					x=w; y=h; z=d;
				}
			}
		}
	}
	public void dec(){
		x--;
		if(x<0){
			x=w-1; y--;
			if(y<0){
				y=h-1; z--;
				if(z<0){
					x=0; y=0; z=0;
				}
			}
		}
	}
	public boolean isZero(){return x==0 && y==0 && z==0;}
	public boolean isEnd(){return x==w && y==h && z==d;}
	
	public int getX(){return x;}
	public int getY(){return y;}
	public int getZ(){return z;}
}
