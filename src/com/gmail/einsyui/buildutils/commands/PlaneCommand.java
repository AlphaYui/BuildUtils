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
import com.gmail.einsyui.buildutils.structs.Plane;

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
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"plane needs 3 points to be set via set");
			return "";
		}
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
