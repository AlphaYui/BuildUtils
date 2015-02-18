package com.gmail.einsyui;

import org.bukkit.Location;

import com.gmail.einsyui.argumentreader.ArgumentReader;
import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.argumentreader.ArgumentType;
import com.gmail.einsyui.argumentreader.Context;

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
		public ObjectGen readAndValidateFrom(ArgumentReader ar, Context ctx) 
				throws ArgumentException{
			//TODO: add "standard" syntax, i.e. CLAY or similar...
			Object res = super.readAndValidateFrom(ar, ctx);
			if(res==null || !(res instanceof ObjectGen))
				ar.syntaxError("Not a valid object generator"); //TODO: improve
			return (ObjectGen) res;
		}
		@Override
		public String name() {
			return "object generator";
		}
	};
	public static final TObjectGen OBJECT_GEN = new TObjectGen();
}
