package com.gmail.einsyui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.einsyui.argumentreader.CommandLibrary;
import com.gmail.einsyui.commands.LineCommand;
import com.gmail.einsyui.commands.SetCommand;

public class Main extends JavaPlugin{
	
	public Map<String,LocationStack> stacks;
	public CommandLibrary commands;
	public GenerationController generationController;
	
	@Override
	public void onEnable()
	{
		stacks = new HashMap<String,LocationStack>();
		
		getLogger().info( "Loading commands..." );
		commands = new CommandLibrary(this);
		commands.addCommand( new SetCommand( this ) );
		commands.addCommand( new LineCommand () );
		
		generationController = new GenerationController(10, this, 2);
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
	public LocationStack getStackFor( CommandSender cs ){
		if(cs instanceof Player){
			return getStackFor(((Player)cs).getName());
		}
		if(cs instanceof ConsoleCommandSender 
				|| cs instanceof RemoteConsoleCommandSender)
			return getStackFor("#console");
		if(cs instanceof BlockCommandSender)
			return getStackFor("#blocks-"+((BlockCommandSender)cs)
					.getBlock().getWorld());
		return null;
	}

	public void generate(Struct struct) {
		generationController.generate(struct, true);
	}
}
