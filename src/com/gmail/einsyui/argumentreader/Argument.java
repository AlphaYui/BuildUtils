package com.gmail.einsyui.argumentreader;

import java.util.List;
import java.util.Map;

import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;


public class Argument implements Describable{
	String name, description;
	ArgumentType type;
	boolean required;
	
	public Argument(String name, ArgumentType type){
		this.name=name; this.type=type; this.description="";
		this.required=false;
	}
	public Argument(String name, ArgumentType type, String description){
		this.name=name; this.type=type; this.description=description;
		this.required=false;
	}
	public Argument(String name, ArgumentType type, boolean required){
		this.name=name; this.type=type; this.description="";
		this.required=required;
	}
	public Argument(String name, ArgumentType type, boolean required, 
			String description){
		this.name=name; this.type=type; this.description=description;
		this.required=required;
	}
	
	@Override
	public String name(){ return name; }
	public boolean hasName(String name){ // case insensitive
		return this.name.toLowerCase().equals(name.toLowerCase());
	}
	@Override
	public String description(){ return description; }
	public ArgumentType type(){ return type; }
	public boolean required(){ return required; }
	
	public Object readAndValidateValueFrom(ArgumentReader ar, Context context) 
			throws ArgumentException{
		ar.beginArgument(this);
		Object res = type.readAndValidateFrom(ar, context);
		ar.endArgument();
		return res;
	}
	
	
	public static Object getWithDefault( Map<String,Object>args, String name, Object def )
	{
		Object o = args.get( name );
		if( o == null )
			return def;
		else
			return o;
	}
	public static Argument findByName(String name, List<Argument> args){
		for(Argument arg:args){
			if(arg.name().equalsIgnoreCase(name))
				return arg;
		}
		return null;
	}
}
