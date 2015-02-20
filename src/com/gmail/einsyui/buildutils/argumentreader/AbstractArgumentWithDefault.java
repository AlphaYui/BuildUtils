package com.gmail.einsyui.buildutils.argumentreader;

public abstract class AbstractArgumentWithDefault extends Argument {
	public AbstractArgumentWithDefault(String name, ArgumentType type) {
		super(name, type);
	}
	public AbstractArgumentWithDefault(String name, ArgumentType type,
			String description) {
		super(name, type, description);
	}

	public abstract Object defaultValue(Context ctx);
}
