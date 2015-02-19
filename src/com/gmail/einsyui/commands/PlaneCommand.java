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
import com.gmail.einsyui.structs.Plane;

public class PlaneCommand implements Command {

	@Override
	public String name() {
		return "plane";
	}

	@Override
	public String description() {
		return "Generates a plane";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgen = (ObjectGen) args.get("with");
		List<Location> l = ls.getLast(3);
		if(objectgen==null) objectgen = new MaterialGen(Material.AIR);
		Plane plane = new Plane(l.get(2), l.get(1), l.get(0), objectgen);
		ctx.getPlugin().generate(plane);
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
