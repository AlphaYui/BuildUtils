package com.gmail.einsyui.buildutils.argumentreader;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;

public class CommandWithLateArgs {
	Command cmd;
	String args;
	Context context;
	CommandLibrary subcommandLibrary;
	
	public CommandWithLateArgs(Command cmd, String args, Context context,
			CommandLibrary subcommandLibrary){
		this.cmd=cmd; this.args=args; this.context=context;
		this.subcommandLibrary=subcommandLibrary;
	}
	public String execute() throws ArgumentException{
		return cmd.execute(new ArgumentReader(args, subcommandLibrary)
				.readArguments(cmd.args(), context), context);
	}
}
