package com.gmail.einsyui.buildutils.geometry;

public abstract class MultiIndex<T> {
	public final int dimensions;
	boolean atMax, atMin;
	
	public MultiIndex(int dimensions){ 
		this.dimensions=dimensions;
	}
	
	protected abstract void incIndex(int i);
	protected abstract void decIndex(int i);
	protected abstract void resetIndexToMax(int i);
	protected abstract void resetIndexToMin(int i);
	protected abstract boolean isIndexAtMin(int i);
	protected abstract boolean isIndexAtMax(int i);
	
	public abstract T get();
	
	public T next(){
		increment(); return get();
	}
	public T prev(){
		decrement(); return get();
	}
	
	public void increment(){
		if(isAtMax()) return;
		atMin=false;
		int i=0;
		incIndex(i);
		while(isIndexAtMax(i)){
			if(i>=dimensions-1){
				atMax=true;
				return;
			}
			resetIndexToMin(i);
			i++;
			incIndex(i);
		}
	}
	public void decrement(){
		if(isAtMin()) return;
		atMax=false;
		int i=0;
		decIndex(i);
		while(isIndexAtMin(i)){
			if(i>=dimensions-1){
				atMin=true;
				return;
			}
			resetIndexToMax(i);
			i++;
			decIndex(i);
		}
	}
	
	public boolean isAtMin(){
		return atMin;
	}
	public boolean isAtMax(){
		return atMax;
	}
	
	public void resetToMin(){
		for(int i=0;i<dimensions;i++)
			resetIndexToMin(i);
	}
	public void resetToMax(){
		for(int i=0;i<dimensions;i++)
			resetIndexToMax(i);
	}
}
