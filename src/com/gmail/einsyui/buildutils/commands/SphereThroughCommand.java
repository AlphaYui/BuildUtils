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
import com.gmail.einsyui.buildutils.structs.Sphere;

public class SphereThroughCommand implements Command {

	@Override
	public String name() {
		return "spherethrough";
	}

	@Override
	public String description() {
		return "A sphere through 4 points";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgen = (ObjectGen) args.get("with");
		List<Location> l = ls.getLast(4);
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"spherethrough needs 4 points to be set via set");
			return "";
		}
		Location center = l.get(0).clone().add(l.get(1)).add(l.get(2))
							.add(l.get(3)).multiply(0.25);
		int radius = (int) Math.ceil(center.distance(l.get(0)));
		if(objectgen==null) objectgen = new MaterialGen(Material.AIR);
		Sphere sphere = new Sphere(center, radius, objectgen);
		ctx.getPlugin().generate(sphere);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("with", ObjectGen.OBJECT_GEN),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK));
	}


}
