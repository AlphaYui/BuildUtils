package com.gmail.einsyui.buildutils.argumentreader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.bukkit.command.CommandSender;

import com.gmail.einsyui.buildutils.Main;

public class HashMapContext implements Context {
	// Default implementation for Context.

	HashMap<String, Stack<Object>> values;
	HashMap<CommandSender, HashMap<String, Object>> params;
	Main plugin; CommandLibrary cl;
	CommandSender cs;
	
	public HashMapContext(Main plugin){
		values = new HashMap<String, Stack<Object>>();
		params = new HashMap<CommandSender, HashMap<String, Object>>();
		printCache = new StringBuilder();
		this.plugin = plugin;
	}
	
	@Override
	public Object get(String name) {
		if(name=="me") return getSender();
		if(!values.containsKey(name)) return null;
		return values.get(name).peek();
	}

	@Override
	public void set(String name, Object value) {
		values.put(name, new Stack<Object>());
		push(name,value);
	}

	@Override
	public void push(String name, Object value) {
		if(!values.containsKey(name))
			values.put(name, new Stack<Object>());
		values.get(name).push(value);
	}

	@Override
	public Object pop(String name) {
		return values.get(name).pop();
	}

	@Override
	public Collection<Object> getAll(String name) {
		return values.get(name);
	}

	protected StringBuilder printCache;
	@Override
	public void print(String msg){
		printCache.append(msg);
	}

	@Override
	public void printLn(String msg) {
		cs.sendMessage(printCache.append(msg).toString());
		printCache = new StringBuilder();
	}

	@Override
	public boolean amI(Class<?> type) {
		return type.isInstance(cs);
	}

	@Override
	public CommandSender getSender() {
		return cs;
	}
	
	@Override
	public void setSender(CommandSender sender){
		cs=sender;
	}

	@Override
	public Main getPlugin() {
		return plugin;
	}

	@Override
	public Map<String, Object> getDefaultParameters() {
		if(!params.containsKey(cs))
			params.put(cs, new HashMap<String, Object>());
		return params.get(cs);
	}
}
