package com.gmail.einsyui.buildutils.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.gmail.einsyui.buildutils.LocationStack;
import com.gmail.einsyui.buildutils.ObjectGen;
import com.gmail.einsyui.buildutils.argumentreader.Argument;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentWithDefault;
import com.gmail.einsyui.buildutils.argumentreader.BukkitArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Command;
import com.gmail.einsyui.buildutils.argumentreader.Context;
import com.gmail.einsyui.buildutils.structs.PointsStruct;

public class PointsCommand implements Command {

	@Override
	public String name() {
		return "points";
	}

	@Override
	public String description() {
		return "Sets n points";
	}

	@Override
	public String execute(Map<String, Object> args, Context ctx) {
		List<Location> p = ((LocationStack) args.get("stack"))
				.getLast((Integer)args.get("n"));
		PointsStruct points = new PointsStruct(p, (ObjectGen) args.get("with"));
		ctx.getPlugin().generate(points);
		return "";
	}

	@Override
	public List<Argument> args() {
		return Arrays.asList(new Argument("with", ObjectGen.OBJECT_GEN, true, 
				"generator to use"),
				new ArgumentWithDefault("n", ArgumentType.INTEGER, 1,
						"number of locations to set"),
				new Argument("stack", BukkitArgumentType.LOCATION_STACK));
	}

}
