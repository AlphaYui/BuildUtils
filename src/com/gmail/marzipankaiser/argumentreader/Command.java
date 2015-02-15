package com.gmail.marzipankaiser.argumentreader;

import java.util.List;
import java.util.Map;

public interface Command {
	public void execute(Map<String, Object> args);
	public List<Argument> args();
}
