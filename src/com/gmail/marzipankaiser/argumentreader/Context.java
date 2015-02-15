package com.gmail.marzipankaiser.argumentreader;

public interface Context {
	public Object get(String name);
	public void set(String name, Object value);
}
