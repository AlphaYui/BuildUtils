package com.gmail.einsyui.buildutils.arithmetic;

import java.util.Map;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class VariableExpression implements Expression {
	String name;
	public VariableExpression(String name){
		this.name=name;
	}
	@Override
	public Number evaluate(Map<String, Number> bindings) {
		if(bindings.containsKey(name))
			return bindings.get(name);
		else return Double.NaN;
	}
	
	public static class TVariableExpression implements ArgumentType<VariableExpression>{
		@Override
		public String name() {
			return "variable";
		}
		@Override
		public String description() {
			return "a variable name";
		}
		@Override
		public VariableExpression readAndValidateFrom(ArgumentReader ar,
				Context context) throws ArgumentException {
			return new VariableExpression(IDENTIFIER.readAndValidateFrom(ar, context));
		}
	};

}
