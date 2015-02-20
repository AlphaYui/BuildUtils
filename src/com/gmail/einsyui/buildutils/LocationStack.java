package com.gmail.einsyui.buildutils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class LocationStack {

	List<Location> stack;
	
	LocationStack()
	{
		stack = new ArrayList<Location>();
	}
	
	public void pushToStack( Location location )
	{
		stack.add( location );
	}
	
	public Location popLast()
	{
		if( stack.isEmpty() ) return null;
		
		Location last = stack.get( stack.size() - 1 );
		stack.remove( stack.size() - 1 );
		
		return last;
	}
	
	public List<Location> popLast( int amount )
	{
		List<Location> toReturn = new ArrayList<Location>();
		
		for( int i = 0; i < amount; ++i )
			toReturn.add( popLast() );
		
		return toReturn;
	}
	
	public List<Location> getLast( int amount )
	{
		List<Location> toReturn = new ArrayList<Location>();
		
		for( int i = stack.size() - 1; amount >= stack.size() - i; --i )
			toReturn.add( stack.get( i ) );
		
		return toReturn;
	}
	
}
