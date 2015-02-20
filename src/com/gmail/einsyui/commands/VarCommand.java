package com.gmail.einsyui.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import com.gmail.einsyui.argumentreader.Argument;
import com.gmail.einsyui.argumentreader.ArgumentReader;
import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.argumentreader.ArgumentType;
import com.gmail.einsyui.argumentreader.Command;
import com.gmail.einsyui.argumentreader.Context;

public class VarCommand implements Command {
	@Override
	public String name() {
		return "var";
	}
	@Override
	public String description() {
		return "operations on variables";
	}
	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		String cmd = ((String) args.get("operation")).toLowerCase();
		String varname = (String) args.get("name");
		if(varname=="me"){
			ctx.printLn(ChatColor.RED+"me is not allowed as a varname");
			return "";
		}
		if(cmd.equals("read")){
			Object val_o = ctx.get(varname);
			if(val_o==null) val_o=0;
			String val=val_o.toString();
			ctx.printLn(varname+" = "+val);
			return val; //TODO: better solution...
		}else if(cmd.equals("set")){
			String val = (String) args.get("value");
			ctx.set(varname, val);
			ctx.printLn("Set "+varname+" to "+val);
		}else if(cmd.equals("incr")){
			String by_v = (String) Argument.getWithDefault(args, "value", "1");
			ArgumentReader ar = new ArgumentReader (by_v, null);
			ar.tryExpect("incr"); ar.skipWhitespace();
			int by=1;
			try {
				by =ArgumentType.INTEGER.readAndValidateFrom(ar, null);
			} catch (ArgumentException e) {
				by=1;
			}
			String oldval = (String) ctx.get(varname);
			int oldval_i;
			try {
				oldval_i = ArgumentType.INTEGER
						.readAndValidateFrom(new ArgumentReader(oldval, null), null);
			} catch (ArgumentException e) {
				oldval_i=0;
			}
			String newval = String.valueOf(oldval_i+by);
			ctx.set(varname, newval);
			ctx.printLn("Set "+varname+" to "+newval);
		}
		return "";
	}
	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("operation", ArgumentType.IDENTIFIER, true),
				new Argument("name", ArgumentType.IDENTIFIER, true),
				new Argument("value", ArgumentType.STRING_IN_ANGLE_BRACKETS)
				);
	}
}
