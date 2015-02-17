package com.gmail.einsyui.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.gmail.marzipankaiser.argumentreader.BukkitArgumentType;
import com.gmail.marzipankaiser.argumentreader.Command;
import com.gmail.marzipankaiser.argumentreader.Context;

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
		
		World w = (World) args.get( "world" );
		
		String groupName = (String) Argument.getWithDefault(args, "group", "#console" );
		int x = (int) args.get( "x" );
		int y = (int) args.get( "y" );
		int z = (int) args.get( "z" );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );
		cs.sendMessage( ChatColor.GREEN + "Successfully pushed position [" + w.getName() +"|"+x+"|"+y+"|"+z+ "] to stack '"+groupName+"'!" );		
		return "";
	}
	
	private String executeForPlayers( Map<String,Object>args, Context ctx )
	{
		Player p = (Player)ctx.get( "me" );
		World w = (World) Argument.getWithDefault( args, "world", p.getWorld() );
		
		String groupName = (String) Argument.getWithDefault( args, "group", p.getName() );
		int x = (int) Argument.getWithDefault( args, "x", p.getLocation().getBlockX() );
		int y = (int) Argument.getWithDefault( args, "y", p.getLocation().getBlockY() );
		int z = (int) Argument.getWithDefault( args, "z", p.getLocation().getBlockZ() );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );
		p.sendMessage( ChatColor.GREEN + "Successfully pushed position [" + w.getName() +"|"+x+"|"+y+"|"+z+ "] to stack '"+groupName+"'!" );	
		
		return "";
	}
	
	private String executeForBlocks( Map<String,Object>args, Context ctx )
	{
		BlockCommandSender bcs = (BlockCommandSender)ctx.get( "me" );
		Block b = bcs.getBlock();
		World w = (World) Argument.getWithDefault( args, "world", b.getWorld() );
		
		//All commandblocks in one world are also in one group
		String groupName = (String) Argument.getWithDefault( args, "group", "#blocks-" + w.getName() );
		int x = (int) Argument.getWithDefault( args, "x", b.getX() );
		int y = (int) Argument.getWithDefault( args, "y", b.getY() );
		int z = (int) Argument.getWithDefault( args, "z", b.getZ() );
		
		main.getStackFor( groupName ).pushToStack( new Location( w,x,y,z ) );

		bcs.sendMessage( ChatColor.GREEN + "Successfully pushed position [" + w.getName() +"|"+x+"|"+y+"|"+z+ "] to stack '"+groupName+"'!" );	
		return "";
	}

	@Override
	public List<Argument> args() {
		return (List<Argument>) Arrays.asList( 	
								new Argument( "group", ArgumentType.IDENTIFIER, "Name of the stack the location shall be pushed to" ),
								new Argument( "x", ArgumentType.INTEGER, "X-coordinate of the point" ),
								new Argument( "y", ArgumentType.INTEGER, "Y-coordinate of the point" ),
								new Argument( "z", ArgumentType.INTEGER, "Z-coordinate of the point" ),
								new Argument( "world", BukkitArgumentType.WORLD, "The world of the point" ));
	}
	
	
}
