package com.gmail.einsyui.buildutils;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.objectgens.MaterialGen;

public interface ObjectGen {
	public void generateAt(Location l);
	
	
	////------------------------------------------------------------------------
	public class TObjectGen extends ArgumentType.TDispatchArgumentType{
		public static TObjectGen SINGLETON = new TObjectGen();
		public static void register(ArgumentType objectGenArgumentType){
			SINGLETON.subTypes.put(objectGenArgumentType.name(), 
									objectGenArgumentType);
		}
		public static void register(String name, ArgumentType objectGenArgumentType){
			SINGLETON.subTypes.put(name, objectGenArgumentType);
		}
		@Override
		public ObjectGen readDefault(ArgumentReader ar, Context context) 
				throws ArgumentException{
			Material m = (Material) BukkitArgumentType.MATERIAL.readAndValidateFrom(ar, context);
			return new MaterialGen(m);
		}
		@Override
		public String name() {
			return "object generator";
		}
	};
	public static final TObjectGen OBJECT_GEN = TObjectGen.SINGLETON;
}
