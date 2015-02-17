package com.gmail.marzipankaiser.argumentreader;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.marzipankaiser.argumentreader.ArgumentType.TEnum;

public class BukkitArgumentType {

	
	////----------------------------------------------------------------------
	/// Player
	public static class TPlayer implements ArgumentType{
		static final TOr Name = 
				new TOr(IDENTIFIER, STRING, STRING_IN_ANGLE_BRACKETS);
		@Override
		public Player readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar, context);
			Player p = Bukkit.getPlayer(name);
			if(p==null){
				ar.syntaxError("No player with name "+name);
			}
			return p;
		}

		@Override
		public String name() {
			return "online player";
		}
	};
	public static final TPlayer PLAYER = new TPlayer();
	////----------------------------------------------------------------------
	/// Offline Player
	public static class TOfflinePlayer implements ArgumentType{
		static final TOr Name = 
				new TOr(IDENTIFIER, STRING, STRING_IN_ANGLE_BRACKETS);
		@Override
		public OfflinePlayer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar, context);
			OfflinePlayer p = Bukkit.getOfflinePlayer(name); 
			if(p==null){
				ar.syntaxError("No player with name "+name);
			}
			return p;
		}

		@Override
		public String name() {
			return "offine player";
		}
	};
	public static final TOfflinePlayer OFFLINE_PLAYER = new TOfflinePlayer();
	////----------------------------------------------------------------------
	/// World
	public static class TWorld implements ArgumentType{
		@Override
		public World readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			World res=null;
			if(ar.tryExpect('#')){ // by UUID
				UUID uuid = JAVA_UUID.readAndValidateFrom(ar, context);
				res = Bukkit.getServer().getWorld(uuid);
				if(res==null)
					ar.syntaxError("Couldn't find world with UUID "+uuid);
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar, context);
				res = Bukkit.getServer().getWorld(name);
				if(res==null)
					ar.syntaxError("Couldn't find world with name "+name);
			}
			
			return res;
		}
		@Override
		public String name() {
			return "world";
		}
	};
	public static final TWorld WORLD = new TWorld();
	////----------------------------------------------------------------------
	/// Location
	public static class TLocation implements ArgumentType{
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			if(ar.tryExpect("here")){
				if(context!=null){
					Object me = context.get("me");
					if(me instanceof Player){
						return ((Player) me).getLocation();
					}else if(me instanceof CommandBlock){
						return ((CommandBlock) me).getLocation();
					}
				}
				ar.syntaxError("'here' can only be used by Players or CommandBlocks.");
			}else if(ar.peekChar()=='('){
				// Syntax in () like command
				String args = STRING_PARENTHESIZED.readAndValidateFrom(ar, context);
				ArgumentReader subargs 
					= new ArgumentReader(args, ar.getSubcommandLibrary());
				Map<String, Object> parsed =subargs.readArguments(
						Arrays.asList(
								new Argument("world", WORLD, true),
								new Argument("x", FLOAT, true),
								new Argument("y", FLOAT, true),
								new Argument("z", FLOAT, true),
								new ArgumentWithDefault("yaw", FLOAT, 0.0f),
								new ArgumentWithDefault("pitch", FLOAT, 0.0f)
						), context);
				return new Location(
						(World) parsed.get("world"),
						(Double) parsed.get("x"), 
						(Double) parsed.get("y"),
						(Double) parsed.get("z"),
						(float) (double) (Double) parsed.get("yaw"),
						(float) (double) (Double) parsed.get("pitch"));
			}
			ar.syntaxError("Invalid syntax for Location"); 
			return null;
		}
		@Override
		public String name() {
			return "Location";
		}
	};
	public static final TLocation LOCATION = new TLocation();
	
	////----------------------------------------------------------------------
	////----------------------------------------------------------------------
	/// Enums
	public static final TEnum MATERIAL = new TEnum(Material.class);
	public static final TEnum ART = new TEnum(Art.class);
	public static final TEnum ENTITY_TYPE = new TEnum(EntityType.class);
	public static final TEnum TREE_SPECIES = new TEnum(TreeSpecies.class);
};