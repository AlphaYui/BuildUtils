package com.gmail.marzipankaiser.argumentreader;

import java.util.List;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;


public class Argument implements Describable{
	String name, description;
	ArgumentType type;
	
	public Argument(String name, ArgumentType type){
		this.name=name; this.type=type; this.description="";
	}
	public Argument(String name, ArgumentType type, String description){
		this.name=name; this.type=type; this.description=description;
	}
	
	@Override
	public String name(){ return name; }
	public boolean hasName(String name){ // case insensitive
		return this.name.toLowerCase().equals(name.toLowerCase());
	}
	@Override
	public String description(){ return description; }
	public ArgumentType type(){ return type; }
	
	public Object readAndValidateValueFrom(ArgumentReader ar) 
			throws ArgumentException{
		ar.beginArgument(this);
		Object res = type.readAndValidateFrom(ar);
		ar.endArgument();
		return res;
	}
	
	
	public static Argument findByName(String name, List<Argument> args){
		for(Argument arg:args){
			if(arg.name().toLowerCase()==name.toLowerCase())
				return arg;
		}
		return null;
	}
}
