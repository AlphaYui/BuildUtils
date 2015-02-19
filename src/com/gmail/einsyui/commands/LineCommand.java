package com.gmail.einsyui.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.einsyui.LocationStack;
import com.gmail.einsyui.ObjectGen;
import com.gmail.einsyui.argumentreader.Argument;
import com.gmail.einsyui.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.argumentreader.Command;
import com.gmail.einsyui.argumentreader.Context;
import com.gmail.einsyui.objectgens.MaterialGen;
import com.gmail.einsyui.structs.Line;

public class LineCommand implements Command {

	@Override
	public String name() {
		return "line";
	}

	@Override
	public String description() {
		return "A line";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgen = (ObjectGen) args.get("with");
		List<Location> l = ls.getLast(2);
		if(objectgen==null) objectgen = new MaterialGen(Material.AIR);
		Line line = new Line(l.get(0), l.get(1), objectgen);
		ctx.getPlugin().generate(line);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("with", ObjectGen.OBJECT_GEN),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK)
				);
	}

}
