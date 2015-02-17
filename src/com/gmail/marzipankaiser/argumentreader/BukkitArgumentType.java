package com.gmail.marzipankaiser.argumentreader;

import java.util.UUID;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;

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
			//TODO: implement
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
	
	////----------------------------------------------------------------------
	/// Material
	public static class TMaterial implements ArgumentType{
		@Override
		public Material readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			Material res;
			if(ar.tryExpect('#')){
				int id = INTEGER.readAndValidateFrom(ar, context);
				res = Material.getMaterial(id); // Deprected, but maybe ok?
												 // (no other way...)
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar, context);
				res = Material.matchMaterial(name);
				if(res==null){ // Allow CamelCase
					StringBuilder ccname = new StringBuilder();
					for(char c:name.toCharArray()){
						if(Character.isUpperCase(c))
							ccname.append('_');
						ccname.append(Character.toUpperCase(c));
					}
					res = Material.getMaterial(ccname.toString());
				}
				if(res==null){ // Allow removing _ and ignore case
					for(Material m:Material.values()){
						if(m.name().replace("_", "").equalsIgnoreCase(name)){
							res=m;
						}
					}
				}
			}	
			return res;
		}
		@Override
		public String name() {
			return "Material";
		}
	};
	public static final TMaterial MATERIAL = new TMaterial();
	////----------------------------------------------------------------------
	/// Art
	public static class TArt implements ArgumentType{
		@Override
		public Art readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			Art res=null;
			String name = IDENTIFIER.readAndValidateFrom(ar, context);
			res=Art.getByName(name);
			if(res==null)
				ar.syntaxError("Art "+name+" not found");
			return res;
		}

		@Override
		public String name() {
			return "Art";
		}
	};
	public static TArt ART = new TArt();
	////----------------------------------------------------------------------
	/// Tree species
	public static class TTreeSpecies implements ArgumentType{
		@Override
		public TreeSpecies readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			TreeSpecies res=null;
			String name = IDENTIFIER.readAndValidateFrom(ar, context);
			res=TreeSpecies.valueOf(name.toUpperCase());
			if(res==null)
				ar.syntaxError("Tree species "+name+" not found");
			return res;
		}

		@Override
		public String name() {
			return "Tree species";
		}
	};
	public static TTreeSpecies TREE_SPECIES = new TTreeSpecies();

};