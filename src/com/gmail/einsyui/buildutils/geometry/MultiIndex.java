package com.gmail.einsyui.buildutils.geometry;

public abstract class MultiIndex<T> {
	public final int dimensions;
	
	public MultiIndex(int dimensions){ this.dimensions=dimensions; }
	
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
		int i=0;
		incIndex(i);
		while(isIndexAtMax(i)){
			resetIndexToMin(i);
			i++;
			if(i>=dimensions) return;
			incIndex(i);
		}
	}
	public void decrement(){
		int i=0;
		decIndex(i);
		while(isIndexAtMin(i)){
			resetIndexToMax(i);
			i++;
			if(i>=dimensions) return;
			decIndex(i);
		}
	}
	
	public boolean isAtMin(){
		for(int i=0;i<dimensions;i++)
			if(!isIndexAtMin(i)) return false;
		return true;
	}
	public boolean isAtMax(){
		for(int i=0;i<dimensions;i++)
			if(!isIndexAtMax(i)) return false;
		return true;
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
