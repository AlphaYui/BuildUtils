package com.gmail.einsyui.buildutils.argumentreader;

public class ArgumentWithDefault extends AbstractArgumentWithDefault {
	Object defaultValue;
	
	public ArgumentWithDefault(String name, ArgumentType<?> type,
			Object defaultValue){
		super(name,type);
		this.defaultValue=defaultValue;
	}
	public ArgumentWithDefault(String name, ArgumentType<?> type,
			Object defaultValue,
			String description){
		super(name,type,description);
		this.defaultValue=defaultValue;
	}
	@Override
	public Object defaultValue(Context ctx){ 
		return defaultValue; 
	}
}
