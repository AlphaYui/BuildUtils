package com.gmail.einsyui.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import com.gmail.einsyui.Main;
import com.gmail.marzipankaiser.argumentreader.Argument;
import com.gmail.marzipankaiser.argumentreader.ArgumentType;
import com.gmail.marzipankaiser.argumentreader.Command;
import com.gmail.marzipankaiser.argumentreader.Context;
import com.gmail.marzipankaiser.buildutils.geometry.Utils;

public class SetCommand implements Command{

	Main main;
	
	public SetCommand( Main m )
	{
		main = m;
	}

	@Override
	public String name() {
		return "set";
	}

	@Override
	public String description() {
		return "Sets a location to be used for generating structures";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		
		CommandSender cs = (CommandSender) ctx.get( "me" );
		
		if( cs instanceof ConsoleCommandSender || cs instanceof RemoteConsoleCommandSender )
			return executeForConsoles( args, ctx );
		else if( cs instanceof Player )
			return executeForPlayers( args,ctx );
		else if( cs instanceof BlockCommandSender )
			return executeForBlocks( args,ctx );
		else
		{
			//Unknown command sender type
			cs.sendMessage( ChatColor.RED + "Sorry, but this type of commandsender isn't implemented yet!" );
			cs.sendMessage( ChatColor.RED + "Maybe you're using an outdated version of this plugin." );
			return "";
		}
	}
	
	private String executeForConsoles( Map<String,Object> args, Context ctx )
	{
		CommandSender cs = (CommandSender) ctx.get( "me" );
		if( !args.containsKey( "x" ) || !args.containsKey( "y" ) || !args.containsKey( "z" ) || !args.containsKey( "world" ) )
		{
			cs.sendMessage( ChatColor.RED + "Illegal arguments! Using a console you need to enter all coordinates." );
			cs.sendMessage( ChatColor.RED + "Use /bu help set for further information!");
			return "";
		}
		
		World w = Bukkit.getWorld( (String)args.get( "world" ) );
		
		if( w == null )
		{
			cs.sendMessage( ChatColor.RED + "Couldn't find the world '" + (String)args.get( "world" ) + "'");
			return "";
		}
		
		String groupName = (String) Utils.getWithDefault(args, "group", "#console" );
		int x = (int) args.get( "x" );
		int y = (int) args.get( "y" );
		int z = (int) args.get( "z" );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );
		cs.sendMessage( ChatColor.GREEN + "Successfully pushed position to stack!" );		
		return "";
	}
	
	private String executeForPlayers( Map<String,Object>args, Context ctx )
	{
		Player p = (Player)ctx.get( "me" );
		World w = Bukkit.getWorld( (String) Utils.getWithDefault( args, "world", p.getWorld().getName() ) );

		if( w == null )
		{
			p.sendMessage( ChatColor.RED + "Couldn't find the world '" + (String)args.get( "world" ) + "'");
			return "";
		}
		
		String groupName = (String) Utils.getWithDefault( args, "group", p.getName() );
		int x = (int) Utils.getWithDefault( args, "x", p.getLocation().getBlockX() );
		int y = (int) Utils.getWithDefault( args, "y", p.getLocation().getBlockY() );
		int z = (int) Utils.getWithDefault( args, "z", p.getLocation().getBlockZ() );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );
		p.sendMessage( ChatColor.GREEN + "Successfully pushed position to stack!" );	
		
		return "";
	}
	
	private String executeForBlocks( Map<String,Object>args, Context ctx )
	{
		BlockCommandSender bcs = (BlockCommandSender)ctx.get( "me" );
		Block b = bcs.getBlock();
		World w = Bukkit.getWorld( (String) Utils.getWithDefault( args, "world", b.getWorld().getName() ) );

		if( w == null )
		{
			bcs.sendMessage( ChatColor.RED + "Couldn't find the world '" + (String)args.get( "world" ) + "'");
			return "";
		}
		
		//All commandblocks in one world are also in one group
		String groupName = (String) Utils.getWithDefault( args, "group", "#blocks-" + w.getName() );
		int x = (int) Utils.getWithDefault( args, "x", b.getX() );
		int y = (int) Utils.getWithDefault( args, "y", b.getY() );
		int z = (int) Utils.getWithDefault( args, "z", b.getZ() );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );

		bcs.sendMessage( ChatColor.GREEN + "Successfully pushed position to stack!" );	
		return "";
	}

	@Override
	public List<Argument> args() {
		return (List<Argument>) Arrays.asList( 	
								new Argument( "group", ArgumentType.STRING ),
								new Argument( "x", ArgumentType.INTEGER ),
								new Argument( "y", ArgumentType.INTEGER ),
								new Argument( "z", ArgumentType.INTEGER ),
								new Argument( "world", ArgumentType.STRING_IN_ANGLE_BRACKETS ));
	}
	
	
}
