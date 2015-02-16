package com.gmail.marzipankaiser.argumentreader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

import org.bukkit.command.CommandSender;

public class HashMapContext implements Context {
	// Default implementation for Context.

	public HashMap<String, Stack<Object>> values;
	
	public HashMapContext(){
		values = new HashMap<String, Stack<Object>>();
		printCache = new StringBuilder();
	}
	
	@Override
	public Object get(String name) {
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
}
