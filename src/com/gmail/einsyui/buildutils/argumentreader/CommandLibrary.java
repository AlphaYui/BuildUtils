package com.gmail.einsyui.buildutils.argumentreader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.einsyui.buildutils.Main;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.UnknownArgumentException;

public class CommandLibrary {
	public HashMap<String, Command> commandTable;
	HashMap<String, ArgumentType<?>> usedArgumentTypes;
	public Context context;
	
	public CommandLibrary(Main plugin){
		commandTable = new HashMap<String, Command>();
		usedArgumentTypes = new HashMap<String, ArgumentType<?>>();
		context = new HashMapContext(plugin);
		addCommand(new HelpCommand()); // always help
	}
	
	//// Error handling
	public void printErrorLn(String msg){
		context.printLn(ChatColor.RED+"[E] "+msg);
	}
	public void unknownCommand(String name){
		printErrorLn(ChatColor.YELLOW+"Unknown command "+name+"!");
	}
	public void handleArgumentException(ArgumentException e, Command cmd){
		//e.printStackTrace();
		if(e instanceof ArgumentReader.ArgumentSyntaxException){
			Argument arg = ((ArgumentReader.ArgumentSyntaxException)e).inArgument();
			if(arg==null){ // in between arguments
				printErrorLn(ChatColor.YELLOW+"Syntax error: "+e.getMessage());
			}else{
				printErrorLn(ChatColor.YELLOW+"Syntax error in argument "+arg.name()
						+": "+e.getMessage());
			}
		}else if(e instanceof UnknownArgumentException){
			printErrorLn(ChatColor.YELLOW+"Unknown argument "
				+((UnknownArgumentException)e).getArgumentName()
				+" for command "+cmd.name()+"!");
		}else{
			printErrorLn(ChatColor.RED+"ERROR: "+e.getMessage());
		}
	}
	
	public void setCommandSender(CommandSender sender){
		context.setSender(sender);
	}
	public String execute(String name, String arguments){
		if(name=="") name="help"; // default: help
		
		// lookup command name
		if(!commandTable.containsKey(name.toLowerCase())){
			unknownCommand(name); return null;
		}
		Command cmd = commandTable.get(name);
		
		// parse arguments
		ArgumentReader ar = new ArgumentReader(arguments,this);
		Map<String, Object> args;
		try {
			args = ar.readArguments(cmd.args(), context);
		} catch (ArgumentException e) {
			handleArgumentException(e, cmd); return null;
		}
		
		// execute command
		String res = cmd.execute(args, context);
		context.set("#"+context.getSender().getName()+"-r", res);
		return res;
	}
	public CommandWithArgs getCommandWithArgs(String name, String arguments){
		if(name=="") name="help"; // default: help
		
		// lookup command name
		if(!commandTable.containsKey(name.toLowerCase())){
			unknownCommand(name); return null;
		}
		Command cmd = commandTable.get(name);
		
		// parse arguments
		ArgumentReader ar = new ArgumentReader(arguments,this);
		Map<String, Object> args;
		try {
			args = ar.readArguments(cmd.args(), context);
		} catch (ArgumentException e) {
			handleArgumentException(e, cmd); return null;
		}
		
		// execute command
		return new CommandWithArgs(cmd, args, context);
	}
	public CommandWithArgs getCommandWithArgs(String command){
		if(command.replace(" ", "")=="") 
			return getCommandWithArgs("help",""); // default: help
		int i = command.indexOf(' ');
		if(i==-1)
			return getCommandWithArgs(command, "");
		else
			return getCommandWithArgs(command.substring(0, i), command.substring(i+1));
	}
	public CommandWithLateArgs getCommandWithLateArgs(String name, String arguments){
		if(name=="") name="help"; // default: help
		
		// lookup command name
		if(!commandTable.containsKey(name.toLowerCase())){
			unknownCommand(name); return null;
		}
		Command cmd = commandTable.get(name);
		
		// execute command
		return new CommandWithLateArgs(cmd, arguments, context, this);
	}
	public CommandWithLateArgs getCommandWithLateArgs(String command){
		if(command.replace(" ", "")=="") 
			return getCommandWithLateArgs("help",""); // default: help
		int i = command.indexOf(' ');
		if(i==-1)
			return getCommandWithLateArgs(command, "");
		else
			return getCommandWithLateArgs(command.substring(0, i), command.substring(i+1));
	}
	public String execute(String command){
		if(command.replace(" ", "")=="") return execute("help",""); // default: help
		int i = command.indexOf(' ');
		if(i==-1)
			return execute(command, "");
		else
			return execute(command.substring(0, i), command.substring(i+1));
	}
	public String handleCommand(CommandSender cs, org.bukkit.command.Command cmd,
			String label, String[] args){
		setCommandSender(cs);
		
		//TODO: Implement the join function
		//String cmdstr = String.join(" ", args);
		String cmdstr = joinStr(" ", args );
		
		return execute(cmdstr);
	}
	
	//TODO: Needs to be written and moved somewhere else
	//If it shall not just write the string of the array in one string, separated by baseStr, it has to be rewritten
	private String joinStr( String baseStr, String[]args )
	{
		if(args.length==0) return "";
		StringBuilder result = new StringBuilder();
		result.append(args[0]);
		for(int i=1;i<args.length;i++){
			result.append(baseStr);
			result.append(args[i]);
		}
		return result.toString();
	}
	
	//// Adding commands
	public void addCommand(Command cmd){
		commandTable.put(cmd.name().toLowerCase(), cmd);
		for(Argument arg:cmd.args()){
			ArgumentType<?> at = arg.type();
			usedArgumentTypes.put(at.name().toLowerCase(), at);
		}
	}

	public void addCommands(Command...commands){
		for(Command cmd:commands)
			addCommand(cmd);
	}
	
	public class HelpCommand implements Command{
		@Override
		public String name() {
			return "help";
		}
		@Override
		public String description() {
			return "shows this help";
		}
		
		@Override
		public String execute(Map<String, Object> args, Context ctx) {
			if(args.containsKey("about")){
				String about =((String)args.get("about")).toLowerCase();
				if(commandTable.containsKey(about)){
					Command cmd = commandTable.get(about);
					printLongDescription(cmd, ctx);
				}else if(usedArgumentTypes.containsKey(about)){
					ArgumentType<?> at = usedArgumentTypes.get(about);
					ctx.printLn("Argument Type "+about+":");
					ctx.printLn(at.description());
				}else{
					ctx.printLn("help topic not found");
				}
			}else{
				for(Command cmd : commandTable.values()){
					printShortDescription(cmd, ctx);
				}
			}
			return null;
		}
		private void printLongDescription(Command cmd, Context ctx) {
			List<Argument> cargs = cmd.args();
			
			// Usage
			ctx.print(ChatColor.DARK_BLUE+cmd.name()+ChatColor.GRAY+" ");
			for(Argument arg:cargs){
				ctx.print(arg.name()+" ");
			}
			ctx.printLn("");
			
			// Description
			ctx.printLn(" "+ChatColor.RESET+cmd.description());
			
			// Arguments
			for(Argument arg:cargs){
				ctx.printLn(
						ChatColor.BLUE+arg.name()
						+ChatColor.GRAY+" ("+arg.type().name()+"): "
						+ChatColor.RESET+arg.description());
			}
		}
		private void printShortDescription(Command cmd, Context ctx) {
			List<Argument> cargs = cmd.args();
			
			// Usage
			ctx.print(ChatColor.DARK_BLUE+cmd.name()+ChatColor.GRAY+" ");
			for(Argument arg:cargs){
				ctx.print(arg.name()+" ");
			}
			ctx.printLn(": "+ChatColor.RESET+cmd.description());
		}

		@Override
		public List<Argument> args() {
			return Arrays.asList(
					new Argument("about", 
							new ArgumentType.TOr<String>(ArgumentType.IDENTIFIER,
									ArgumentType.STRING))
					);
		}
		
	}

	public Context getContext() {
		return context;
	};
}
