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
			set=false; key="";
		}
		if(key.equalsIgnoreCase("blocksperperiod") || key==""){
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
			if(key!="")
				return String.valueOf(plugin.generationController.blocksPerPeriod());
		}
		if(key.equalsIgnoreCase("period") || key==""){
			if(set){
				try {
					int v=ArgumentType.INTEGER.readAndValidateFrom(valuereader, ctx);
					plugin.generationController.setPeriod(v);
					ctx.printLn("Set");
				} catch (ArgumentException e) {
					ctx.print("wrong syntax for value :");
					ctx.printLn(e.getMessage());
					return "";
				}
			}else ctx.printLn("period: "
					+plugin.generationController.period());
			if(key!="")
				return String.valueOf(plugin.generationController.period());
		}
		if(key.equalsIgnoreCase("remainingstructs") || key==""){
			if(set) ctx.printLn("can't set");
			ctx.printLn("Remaining structs to generate: "
					+plugin.generationController.remainingStructs());
			if(key!="")
				return String.valueOf(plugin.generationController.remainingStructs());
		}
		if(key.equalsIgnoreCase("generating") || key==""){
			if(set){
				try {
					boolean b=ArgumentType.BOOLEAN.readAndValidateFrom(valuereader, ctx);
					if(b)
						plugin.generationController.startGenerating();
					else
						plugin.generationController.stopGenerating();
					ctx.printLn("Set");
				} catch (ArgumentException e) {
					ctx.print("wrong syntax for value :");
					ctx.printLn(e.getMessage());
					return "";
				}
			}else {
				if(plugin.generationController.isGenerating())
					ctx.printLn("is generating");
				else
					ctx.printLn("isn't generating");
			}
			if(key!="")
				return String.valueOf(plugin.generationController.isGenerating());
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