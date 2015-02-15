package com.gmail.marzipankaiser.argumentreader;

import java.util.Collection;

public interface Context {
	public Object get(String name);
	public void set(String name, Object value);
	
	public void push(String name, Object value);
	public Object pop(String name);
	public Collection<Object> getAll(String name);
}
