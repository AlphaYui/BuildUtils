package com.gmail.marzipankaiser.argumentreader;

import java.util.HashMap;
import java.util.Map;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.marzipankaiser.argumentreader.ArgumentReader.UnknownArgumentException;

public class CommandLibrary {
	public HashMap<String, Command> commandTable;
	public Context context;
	
	public CommandLibrary(){
		commandTable = new HashMap<String, Command>();
		context = new HashMapContext();
	}
	
	//// Error handling
	public void printErrorLn(String msg){
		//TODO
	}
	public void unknownCommand(String name){
		printErrorLn("Unknown command "+name+"!");
	}
	public void handleArgumentException(ArgumentException e, Command cmd){
		if(e instanceof ArgumentReader.ArgumentSyntaxException){
			Argument arg = ((ArgumentReader.ArgumentSyntaxException)e).inArgument();
			if(arg==null){ // in between arguments
				printErrorLn("Syntax error: "+e.getMessage());
			}else{
				printErrorLn("Syntax error in argument "+arg.name()
						+": "+e.getMessage());
			}
		}else if(e instanceof UnknownArgumentException){
			printErrorLn("Unknown argument "
				+((UnknownArgumentException)e).getArgumentName()
				+" for command "+cmd.name()+"!");
		}else{
			printErrorLn("ERROR: "+e.getMessage());
		}
	}
	
	
	public Object execute(String name, String arguments){
		
		// lookup command name
		if(!commandTable.containsKey(name.toLowerCase())){
			unknownCommand(name); return null;
		}
		Command cmd = commandTable.get(name);
		
		// parse arguments
		ArgumentReader ar = new ArgumentReader(arguments);
		Map<String, Object> args;
		try {
			args = ar.readArguments(cmd.args());
		} catch (ArgumentException e) {
			handleArgumentException(e, cmd); return null;
		}
		
		// execute command
		return cmd.execute(args, context);
	}
	
	
	public void addCommand(Command cmd){
		commandTable.put(cmd.name().toLowerCase(), cmd);
	}
}
