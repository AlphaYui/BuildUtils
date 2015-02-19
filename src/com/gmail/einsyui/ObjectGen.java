package com.gmail.einsyui;

import org.bukkit.Location;

import com.gmail.einsyui.argumentreader.ArgumentReader;
import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.argumentreader.ArgumentType;
import com.gmail.einsyui.argumentreader.Context;
import com.gmail.einsyui.objectgens.MaterialGen;

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
			return (MaterialGen) MaterialGen.MATERIAL_GEN_AT
					.readAndValidateFrom(ar, context);
		}
		@Override
		public String name() {
			return "object generator";
		}
	};
	public static final TObjectGen OBJECT_GEN = TObjectGen.SINGLETON;
}
