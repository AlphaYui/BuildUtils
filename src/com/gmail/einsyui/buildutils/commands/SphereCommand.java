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
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Command;
import com.gmail.einsyui.buildutils.argumentreader.Context;
import com.gmail.einsyui.buildutils.objectgens.MaterialGen;
import com.gmail.einsyui.buildutils.structs.Sphere;

public class SphereCommand implements Command {

	@Override
	public String name() {
		return "sphere";
	}

	@Override
	public String description() {
		return "A sphere around M with radius r";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgen = (ObjectGen) args.get("with");
		List<Location> l = ls.getLast(1);
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"sphere needs 1 points to be set via set");
			return "";
		}
		if(objectgen==null) objectgen = new MaterialGen(Material.AIR);
		Sphere sphere = new Sphere(l.get(0), (Integer)args.get("r"), objectgen);
		ctx.getPlugin().generate(sphere);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("r", ArgumentType.INTEGER, true),
				new Argument("with", ObjectGen.OBJECT_GEN),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK));
	}

}
