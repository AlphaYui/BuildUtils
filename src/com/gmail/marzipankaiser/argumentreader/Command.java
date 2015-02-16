package com.gmail.marzipankaiser.argumentreader;

import java.util.List;
import java.util.Map;

public interface Command extends Describable {
	public String execute(Map<String, Object> args, Context ctx);
	public List<Argument> args();
}
