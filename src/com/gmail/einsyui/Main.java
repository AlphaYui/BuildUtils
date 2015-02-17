package com.gmail.einsyui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.einsyui.commands.SetCommand;
import com.gmail.marzipankaiser.argumentreader.CommandLibrary;

public class Main extends JavaPlugin{
	
	public Map<String,LocationStack> stacks;
	public CommandLibrary commands;
	
	@Override
	public void onEnable()
	{
		stacks = new HashMap<String,LocationStack>();
		
		getLogger().info( "Loading commands..." );
		commands = new CommandLibrary();
		commands.addCommand( new SetCommand( this ) );
	}
	
	public boolean onCommand( CommandSender cs, org.bukkit.command.Command cmd, String label, String[] args )
	{
		if( !cmd.getName().equalsIgnoreCase( "bu" ) )
			return true;
		commands.handleCommand(cs, cmd, label, args);
		
		return true;
	}
	
	public LocationStack getStackFor( String s )
	{
		LocationStack locStack = stacks.get( s );
		
		if( locStack == null )
		{
			stacks.put( s, new LocationStack() );
			return stacks.get( s );
		}
		else return locStack;
	}
}
