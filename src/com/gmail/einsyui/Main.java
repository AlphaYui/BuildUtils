package com.gmail.einsyui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.einsyui.commands.SetCommand;
import com.gmail.marzipankaiser.argumentreader.Command;
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
	
	
	
	//Old code for the "bu set" command
	//Stored here, in case it's needed later
	//TODO: remove it sometimes
	private boolean cmdSet( CommandSender cs,Command cmd,String label,String[]args )
	{
		if( args[0].equalsIgnoreCase( "set" ) )
		{
			//bu set [group name] [x/~] [y/~] [z/~] [world/~]
			
			//Maybe add default name group for undefined cmd blocks
			
			String nameGroup = "#DEFAULT";
			
			if( args.length > 1 )
			{
				if( !args[1].equals( "~" ) )
				nameGroup = args[1];
				else
				{
					if( isPlayer( cs ) )
						nameGroup = castPlayer( cs ).getName();
					else
						nameGroup = "#CONSOLE";
				}
			}
			else
			{
				if( isPlayer( cs ) )
					nameGroup = castPlayer( cs ).getName();
				else
					nameGroup = "#CONSOLE";
			}
			
			int x=0,y=0,z=0;
			World w=null;
			
			if( isConsole( cs ) && args.length < 6 )
			{
				cs.sendMessage( ChatColor.RED + "If used via a console this command requires the exact location as argument!" );
				return false;
			}
			
			if( isConsole( cs ) && args.length >= 5 )
			{
				if( isInt( args[2] ) && isInt( args[3] ) && isInt( args[4] ) && (Bukkit.getWorld( args[5] ) != null) )
				{
					x = parseInt( args[2] );
					y = parseInt( args[3] );
					z = parseInt( args[4] );
					w = Bukkit.getWorld( args[5] );
				}
				else
				{
					cs.sendMessage( "Invalid location data!" );
					return false;
				}
			}
			
			if( isPlayer( cs ) || isBlock( cs ) )
			{
				Location loc = getCmdLocation( cs );
				x = loc.getBlockX();
				y = loc.getBlockY();
				z = loc.getBlockZ();
				w = loc.getWorld();
				
				if( args.length > 5 )
				{
					if( !args[5].equals( "~" ) )
						w = Bukkit.getWorld( args[5] );
					
					if( w == null )
					{
						cs.sendMessage( ChatColor.RED + "This world does not exist!" );
						return true;
					}
				}
				else if( args.length > 4 )
				{
					if( !args[4].equals( "~" ) )
					{
						z = parseInt( args[4] );
					
						if( !isInt( args[4]) )
						{
							cs.sendMessage( ChatColor.RED + "Invalid z coordinate!" );
							return true;
						}
					}
				}
				else if( args.length > 3 )
				{
					if( !args[3].equals( "~" ) )
					{
						y = parseInt( args[3] );
					
						if( !isInt( args[3]) )
						{
							cs.sendMessage( ChatColor.RED + "Invalid y coordinate!" );
							return true;
						}
					}
				}
				else if( args.length > 2 )
				{
					if( !args[2].equals( "~" ) )
					{
						x = parseInt( args[2] );
					
						if( !isInt( args[2]) )
						{
							cs.sendMessage( ChatColor.RED + "Invalid x coordinate!" );
							return true;
						}
					}
				}
			}
			//ENDIF
			
			if( !stacks.containsKey( nameGroup ) )
				stacks.put( nameGroup, new LocationStack() );
			
			stacks.get( nameGroup ).pushToStack( new Location( w,x,y,z ) );
			
			cs.sendMessage( ChatColor.GREEN + "Successfully pushed location to the stack '" + nameGroup + "'!" );
		}
		
		return true;
	}
}
