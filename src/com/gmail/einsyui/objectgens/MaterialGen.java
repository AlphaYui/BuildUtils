package com.gmail.einsyui.objectgens;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.einsyui.ObjectGen;
import com.gmail.einsyui.argumentreader.Argument;
import com.gmail.einsyui.argumentreader.ArgumentType.TConstructorArgumentType;
import com.gmail.einsyui.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.argumentreader.Context;

public class MaterialGen implements ObjectGen {
	Material material;
	
	public MaterialGen(Material material){
		this.material = material;
	}
	@Override
	public void generateAt(Location l) {
		l.getBlock().setType(material);
	}
	
	public static class TMaterialGen extends TConstructorArgumentType{
		static{
			TObjectGen.register("m", new TMaterialGen());
		}
		@Override
		public String name() {
			return "material generator";
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
	};

}
