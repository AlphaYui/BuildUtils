package com.gmail.einsyui.geometry;

public class Index2D {
	int w, h;
	int x, y;
	public Index2D(int x, int y, int w, int h){
		this.w=w; this.h=h; this.x=x; this.y=y;
	}
	public void inc(){
		x++;
		if(x>=w){
			x=0; y++;
			if(y>=h){
				x=w; y=h;
			}
		}
	}
	public void dec(){
		x--;
		if(x<0){
			x=w-1; y--;
			if(y<0){
				x=0; y=0;
			}
		}
	}
	public boolean isZero(){return x==0 && y==0;}
	public boolean isEnd(){return x==w && y==h;}
	
	public int getX(){return x;}
	public int getY(){return y;}
}
