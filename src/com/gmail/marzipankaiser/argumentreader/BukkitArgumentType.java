package com.gmail.marzipankaiser.argumentreader;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;

public class BukkitArgumentType {

	
	////----------------------------------------------------------------------
	/// Material
	public static class TMaterial implements ArgumentType{
		@Override
		public Material readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			Material res;
			if(ar.tryExpect('#')){
				int id = INTEGER.readAndValidateFrom(ar);
				res = Material.getMaterial(id); // Deprected, but maybe ok?
												 // (no other way...)
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar);
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
	/// Player
	public static class TPlayer implements ArgumentType{
		static final TOr Name = 
				new TOr(IDENTIFIER, STRING, STRING_IN_ANGLE_BRACKETS);
		@Override
		public Player readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar);
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
		public OfflinePlayer readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			String name = (String) Name.readAndValidateFrom(ar);
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
		public World readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			World res=null;
			if(ar.tryExpect('#')){ // by UUID
				UUID uuid = JAVA_UUID.readAndValidateFrom(ar);
				res = Bukkit.getServer().getWorld(uuid);
				if(res==null)
					ar.syntaxError("Couldn't find world with UUID "+uuid);
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar);
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
}
