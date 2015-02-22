package com.gmail.einsyui.buildutils.objectgens;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType.TConstructorArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class MaterialGen implements ObjectGen {
	Material material;
	
	public MaterialGen(Material material){
		this.material = material;
	}
	@Override
	public void generateAt(Location l) {
		l.getBlock().setType(material);
	}
	public static final TMaterialGen MATERIAL_GEN_AT = new TMaterialGen();
	public static class TMaterialGen extends TConstructorArgumentType{
		@Override
		public String name() {
			return "material";
		}
		@Override
		public List<Argument> args() {
			return Arrays.asList(new Argument("material", 
					BukkitArgumentType.MATERIAL));
		}
		@Override
		public Object construct(Map<String, Object> args, Context ctx) {
			return new MaterialGen(
					(Material) Argument.getWithDefault(
							args, "material", Material.AIR));
		}
		@Override
		public String description() {
			return "Material. Syntax: material<MATERIAL>";
		}
	};

}
