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
import com.gmail.einsyui.buildutils.structs.Circle;

public class CircleCommand implements Command {

	@Override
	public String name() {
		return "circle";
	}

	@Override
	public String description() {
		return "A circle around M with radius r and normal through N";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgenF = (ObjectGen) args.get("withFill");
		ObjectGen objectgenL = (ObjectGen) args.get("withLine");
		List<Location> l = ls.getLast(2);
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"circle needs 2 points to be set via set");
			return "";
		}
		if(objectgenF==null) objectgenF = new MaterialGen(Material.AIR);
		if(objectgenL==null) objectgenL = objectgenF;
		Circle circle = new Circle(l.get(1), (Integer)args.get("r"),
				l.get(0).toVector().subtract(l.get(1).toVector()),
				objectgenF, objectgenL);
		ctx.getPlugin().generate(circle);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("r", ArgumentType.INTEGER, true),
				new Argument("withFill", ObjectGen.OBJECT_GEN),
				new Argument("withLine", ObjectGen.OBJECT_GEN),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK));
	}


}
