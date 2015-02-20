package com.gmail.einsyui.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.gmail.einsyui.Main;
import com.gmail.einsyui.argumentreader.Argument;
import com.gmail.einsyui.argumentreader.ArgumentReader;
import com.gmail.einsyui.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.argumentreader.ArgumentType;
import com.gmail.einsyui.argumentreader.Command;
import com.gmail.einsyui.argumentreader.Context;

public class DebugCommand implements Command {

	@Override
	public String name() {
		return "debug";
	}

	@Override
	public String description() {
		return "various debugging information";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		String key = (String) args.get("key");
		boolean set = args.containsKey("setTo");
		ArgumentReader valuereader=null;
		Main plugin = ctx.getPlugin();
		if(set)
			valuereader = new ArgumentReader((String) args.get("setTo"), null);
		if(key==null){
			
		}else if(key.equalsIgnoreCase("blocksperperiod")){
			if(set){
				try {
					int v=ArgumentType.INTEGER.readAndValidateFrom(valuereader, ctx);
					plugin.generationController.setBlocksPerPreiod(v);
					ctx.printLn("Set");
				} catch (ArgumentException e) {
					ctx.printLn("wrong syntax for value");
				}
			}else ctx.printLn("Blocks per period: "
					+plugin.generationController.blocksPerPeriod());
			return String.valueOf(plugin.generationController.blocksPerPeriod());
		}else if(key.equalsIgnoreCase("period")){
			if(set){
				try {
					int v=ArgumentType.INTEGER.readAndValidateFrom(valuereader, ctx);
					plugin.generationController.setPeriod(v);
					ctx.printLn("Set");
				} catch (ArgumentException e) {
					ctx.printLn("wrong syntax for value");
				}
			}else ctx.printLn("period: "
					+plugin.generationController.period());
			return String.valueOf(plugin.generationController.period());
		}else if(key.equalsIgnoreCase("remainingstructs")){
			if(set) ctx.printLn("can't set");
			ctx.printLn("Remaining structs to generate: "
					+plugin.generationController.remainingStructs());
			return String.valueOf(plugin.generationController.remainingStructs());
		}
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("key", ArgumentType.IDENTIFIER),
				new Argument("setTo", ArgumentType.STRING_IN_ANGLE_BRACKETS)
				);
	}

}
