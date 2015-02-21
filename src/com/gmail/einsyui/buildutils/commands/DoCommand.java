package com.gmail.einsyui.buildutils.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentWithDefault;
import com.gmail.einsyui.buildutils.argumentreader.Command;
import com.gmail.einsyui.buildutils.argumentreader.CommandWithLateArgs;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class DoCommand implements Command {

	@Override
	public String name() {
		return "do";
	}

	@Override
	public String description() {
		return "execute cmds in order, n times";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		if(!(args.get("cmds") instanceof List<?>)) return "";
		List<CommandWithLateArgs> cmds = (List<CommandWithLateArgs>) args.get("cmds");
		if(cmds==null) return "";
		int n = (Integer) args.get("n");
		for(int i=0;i<n;i++){
			for(CommandWithLateArgs cmd:cmds)
				try {
					if(cmd!=null)
						cmd.execute();
					else ctx.printLn("in do: unknown command");
				} catch (ArgumentException e) {
					ctx.printLn("error in do: "+e.getMessage());
				}
		}
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("cmds", new ArgumentType.TDelimitedList(
									'{', ' ', '}', 
									ArgumentType.LATE_COMMAND)),
				new ArgumentWithDefault("n", ArgumentType.INTEGER, 1)
				);
	}

}
