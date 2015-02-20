package com.gmail.einsyui.buildutils.argumentreader;

import java.util.Map;

public class CommandWithArgs {
	Command cmd;
	Map<String, Object> args;
	Context context;
	
	public CommandWithArgs(Command cmd, Map<String, Object> args, Context context){
		this.cmd=cmd; this.args=args; this.context=context;
	}
	public String execute(){
		return cmd.execute(args, context);
	}
}
