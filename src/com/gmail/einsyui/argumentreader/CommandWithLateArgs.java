package com.gmail.einsyui.argumentreader;

import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;

public class CommandWithLateArgs {
	Command cmd;
	ArgumentReader args;
	Context context;
	
	public CommandWithLateArgs(Command cmd, ArgumentReader args, Context context){
		this.cmd=cmd; this.args=args; this.context=context;
	}
	public String execute() throws ArgumentException{
		return cmd.execute(args.readArguments(cmd.args(), context), context);
	}
}
