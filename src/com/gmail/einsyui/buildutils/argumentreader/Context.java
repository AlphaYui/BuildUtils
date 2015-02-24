package com.gmail.einsyui.buildutils.argumentreader;

import java.util.Collection;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.gmail.einsyui.buildutils.Main;

public interface Context {
	public Object get(String name);
	public void set(String name, Object value);
	
	public void push(String name, Object value);
	public Object pop(String name);
	public Collection<Object> getAll(String name);
	
	public void print(String msg);
	public void printLn(String msg);
	
	public boolean amI(Class<?> type);
	public CommandSender getSender();
	public void setSender(CommandSender sender);
	
	public Main getPlugin();
	
	public Map<String, String> getDefaultParameters();
}
