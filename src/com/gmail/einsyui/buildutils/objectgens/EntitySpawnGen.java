package com.gmail.einsyui.buildutils.objectgens;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType.TConstructorArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class EntitySpawnGen implements ObjectGen {
	EntityType type;
	
	public EntitySpawnGen(EntityType type){ this.type=type; }
	
	@Override
	public void generateAt(Location l) {
		l.getWorld().spawnEntity(l, type);
	}
	
	public static final TMaterialGen ENTITY_SPAWN_GEN_AT = new TMaterialGen();
	public static class TMaterialGen extends TConstructorArgumentType{
		@Override
		public String name() {
			return "spawn";
		}
		@Override
		public List<Argument> args() {
			return Arrays.asList(new Argument("type", 
					BukkitArgumentType.ENTITY_TYPE));
		}
		@Override
		public Object construct(Map<String, Object> args, Context ctx) {
			return new EntitySpawnGen(
					(EntityType) Argument.getWithDefault(
							args, "type", EntityType.PIG));
		}
	};

}
