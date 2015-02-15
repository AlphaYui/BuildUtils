package com.gmail.marzipankaiser.argumentreader;

import java.util.HashMap;

public class HashMapContext implements Context {

	public HashMap<String, Object> values;
	
	@Override
	public Object get(String name) {
		return values.get(name);
	}

	@Override
	public void set(String name, Object value) {
		values.put(name, value);
	}

}
