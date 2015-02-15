package com.gmail.marzipankaiser.argumentreader;

import java.util.HashMap;
import java.util.Map;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;

public class CommandLibrary {
	public HashMap<String, Command> commandTable;
	public Context context;
	
	public CommandLibrary(){
		commandTable = new HashMap<String, Command>();
		context = new HashMapContext();
	}
	
	public void unknownCommand(String name){
		//TODO
	}
	public void handleArgumentException(ArgumentException e){
		//TODO
	}
	public Object execute(String name, String arguments){
		
		// lookup command name
		if(!commandTable.containsKey(name)){
			unknownCommand(name); return null;
		}
		Command cmd = commandTable.get(name);
		
		// parse arguments
		ArgumentReader ar = new ArgumentReader(arguments);
		Map<String, Object> args;
		try {
			args = ar.readArguments(cmd.args());
		} catch (ArgumentException e) {
			handleArgumentException(e); return null;
		}
		
		// execute command
		return cmd.execute(args, context);
	}
}
