package com.gmail.einsyui.buildutils.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentWithDefault;
import com.gmail.einsyui.buildutils.argumentreader.Command;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class ParamCommand implements Command {

	@Override
	public String name() {
		return "param";
	}

	@Override
	public String description() {
		return "sets the default arguments for following commands";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		String name = (String) args.get("name");
		if((boolean) args.get("delete")){
			ctx.getDefaultParameters().remove(name);
			return "";
		}
		if(args.containsKey("stringValue")){
			ctx.getDefaultParameters().put(name, 
					(String) args.get("stringValue"));
			return "";
		}else{
			String v = ctx.getDefaultParameters().get(name);
			ctx.printLn(name + ":= "+v);
			return v;
		}
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("name", ArgumentType.IDENTIFIER, true),
				new Argument("stringValue", ArgumentType.STRING_IN_ANGLE_BRACKETS),
				new ArgumentWithDefault("delete", ArgumentType.BOOLEAN, false)
				);
	}

}
