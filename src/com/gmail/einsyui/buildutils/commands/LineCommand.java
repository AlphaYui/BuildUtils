package com.gmail.einsyui.buildutils.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.einsyui.buildutils.LocationStack;
import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Command;
import com.gmail.einsyui.buildutils.argumentreader.Context;
import com.gmail.einsyui.buildutils.objectgens.MaterialGen;
import com.gmail.einsyui.buildutils.structs.Line;

public class LineCommand implements Command {

	@Override
	public String name() {
		return "line";
	}

	@Override
	public String description() {
		return "Generates a line";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgen = (ObjectGen) args.get("with");
		List<Location> l = ls.getLast(2);
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"line needs 2 points set via set");
			return "";
		}
		if(objectgen==null) objectgen = new MaterialGen(Material.AIR);
		Line line = new Line(l.get(0), l.get(1), objectgen);
		ctx.getPlugin().generate(line);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("with", ObjectGen.OBJECT_GEN, "The material to use"),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK,
						"the location stack to use")
				);
	}

}
