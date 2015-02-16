package com.gmail.einsyui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
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
		return stacks.get( s );
	}
	
	public static boolean isInt( String s )
	{
		try {
		Integer.parseInt( s );
		} catch( IllegalArgumentException e )
		{
			return false;
		}
		
		return true;
	}
	
	public static int parseInt( String s )
	{
		int i=0;
		
		try {
		i = Integer.parseInt( s );
		} catch( IllegalArgumentException e )
		{
			return -1;
		}
		
		return i;
	}
	
	public static boolean isPlayer( CommandSender cs )
	{
		return (cs instanceof Player);
	}
	
	public static boolean isBlock( CommandSender cs )
	{
		return (cs instanceof BlockCommandSender);
	}
	
	public static boolean isConsole( CommandSender cs )
	{
		return ( cs instanceof ConsoleCommandSender || cs instanceof RemoteConsoleCommandSender );
	}
	
	public static Location getCmdLocation( CommandSender cs )
	{
		if( isConsole( cs ) ) return null;
		if( isBlock( cs ) )
		{
			BlockCommandSender bcs = (BlockCommandSender) cs;
			return bcs.getBlock().getLocation();
		}
		if( isPlayer( cs ) )
			return castPlayer( cs ).getLocation();
		
		return null;
	}
	
	public static Player castPlayer( CommandSender cs )
	{
		return (Player) cs;
	}
	
	public void showGeneralHelp()
	{
		//TODO
	}
	
	public List<String> getHelpFor( String search )
	{
		Map<String,List<String>> help = getHelpMap();
		List<String> result = new ArrayList<String>();
		
		if( !help.containsKey( search.toLowerCase() ) )
			result.add( ChatColor.RED + "There's no help for that command. Was it written correctly?" );
		
		return help.get( search.toLowerCase() );
	}
	
	public Map<String,List<String>> getHelpMap()
	{
		Map<String,List<String>> help = new HashMap<String,List<String>>();
		
		return help;
	}
}
