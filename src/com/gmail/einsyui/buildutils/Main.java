package com.gmail.einsyui.buildutils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.einsyui.buildutils.ObjectGen.TObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.CommandLibrary;
import com.gmail.einsyui.buildutils.commands.CuboidCommand;
import com.gmail.einsyui.buildutils.commands.DebugCommand;
import com.gmail.einsyui.buildutils.commands.DoCommand;
import com.gmail.einsyui.buildutils.commands.LineCommand;
import com.gmail.einsyui.buildutils.commands.ParallelepipedCommand;
import com.gmail.einsyui.buildutils.commands.ParallelogramCommand;
import com.gmail.einsyui.buildutils.commands.SetCommand;
import com.gmail.einsyui.buildutils.commands.SphereCommand;
import com.gmail.einsyui.buildutils.commands.SphereThroughCommand;
import com.gmail.einsyui.buildutils.commands.VarCommand;
import com.gmail.einsyui.buildutils.objectgens.EntitySpawnGen;
import com.gmail.einsyui.buildutils.objectgens.MaterialGen;
import com.gmail.einsyui.buildutils.objectgens.SignGen;

public class Main extends JavaPlugin{
	
	public Map<String,LocationStack> stacks;
	public CommandLibrary commands;
	public ScalingGenerationController generationController;
	
	@Override
	public void onEnable()
	{
		stacks = new HashMap<String,LocationStack>();
		
		getLogger().info( "Loading commands..." );
		commands = new CommandLibrary(this);
		commands.addCommand( new SetCommand( this ) );
		commands.addCommand( new LineCommand () );
		commands.addCommand( new ParallelogramCommand () );
		commands.addCommand( new ParallelepipedCommand () );
		commands.addCommand( new CuboidCommand () );
		commands.addCommand( new SphereCommand () );
		commands.addCommand( new SphereThroughCommand () );
		
		commands.addCommand(new DebugCommand());
		commands.addCommand(new DoCommand());
		commands.addCommand(new VarCommand());
		
		TObjectGen.register(MaterialGen.MATERIAL_GEN_AT);
		TObjectGen.register(EntitySpawnGen.ENTITY_SPAWN_GEN_AT);
		TObjectGen.register(SignGen.SIGN_GEN_AT);
		
		generationController = new ScalingGenerationController(10, this, 2);
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
