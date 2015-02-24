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
import com.gmail.einsyui.buildutils.structs.Circle;

public class CircleThroughCommand implements Command {

	@Override
	public String name() {
		return "circlethrough";
	}

	@Override
	public String description() {
		return "generates a circle through 3 points";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		LocationStack ls = (LocationStack) Argument.getWithDefault(args, "stack", 
				ctx.getPlugin().getStackFor(ctx.getSender()));
		ObjectGen objectgenF = (ObjectGen) args.get("withFill");
		ObjectGen objectgenL = (ObjectGen) args.get("withLine");
		List<Location> l = ls.getLast(3);
		if(l.contains(null)){
			ctx.printLn(ChatColor.RED+"[E] "+ChatColor.YELLOW
					+"circlethrough needs 3 points to be set via set");
			return "";
		}
		if(objectgenF==null) objectgenF = new MaterialGen(Material.AIR);
		if(objectgenL==null) objectgenL = objectgenF;
		Circle circle = new Circle(l.get(0), l.get(1), l.get(2), objectgenF, objectgenL);
		ctx.getPlugin().generate(circle);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(
				new Argument("withFill", ObjectGen.OBJECT_GEN, "The material to use"),
				new Argument("withLine", ObjectGen.OBJECT_GEN, "The material to use"),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK,
						"the location stack to use")
				);
	}

}
