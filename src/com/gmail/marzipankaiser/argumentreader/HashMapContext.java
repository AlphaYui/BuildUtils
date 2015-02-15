package com.gmail.marzipankaiser.argumentreader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;

public class HashMapContext implements Context {
	// Default implementation for Context.

	public HashMap<String, Stack<Object>> values;
	
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

	@Override
	public void print(String msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void printLn(String msg) {
		// TODO Auto-generated method stub
	}

}
