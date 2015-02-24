package com.gmail.einsyui.buildutils.arithmetic;

import java.util.Map;

public interface Expression {
	public Number evaluate(Map<String, Number> bindings);
}
