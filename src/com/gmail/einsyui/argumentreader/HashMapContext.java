package com.gmail.einsyui.argumentreader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import org.bukkit.command.CommandSender;

import com.gmail.einsyui.Main;

public class HashMapContext implements Context {
	// Default implementation for Context.

	public HashMap<String, Stack<Object>> values;
	Main plugin; CommandLibrary cl;
	
	public HashMapContext(Main plugin){
		values = new HashMap<String, Stack<Object>>();
		printCache = new StringBuilder();
		this.plugin = plugin;
	}
	
	@Override
	public Object get(String name) {
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
		if(!(get("me") instanceof CommandSender))
			return; //TODO: handle
		((CommandSender) get("me")).sendMessage(printCache.append(msg).toString());
		printCache = new StringBuilder();
	}

	@Override
	public boolean amI(Class<?> type) {
		return type.isInstance(get("me"));
	}

	@Override
	public CommandSender getSender() {
		return (CommandSender) get("me");
	}

	@Override
	public Main getPlugin() {
		return plugin;
	}
}
