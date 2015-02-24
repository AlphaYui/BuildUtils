package com.gmail.einsyui.buildutils.arithmetic;

import java.util.Map;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.argumentreader.Context;

public class NumberExpression implements Expression {
	Number num;
	public NumberExpression(Number num){this.num=num;}
	@Override
	public Number evaluate(Map<String, Number> bindings) {
		return num;
	}
	
	public static class TNumberExpression implements ArgumentType<NumberExpression>{
		@Override
		public String name() {
			return "number";
		}
		@Override
		public String description() {
			return "a double number";
		}

		@Override
		public NumberExpression readAndValidateFrom(ArgumentReader ar,
				Context context) throws ArgumentException {
			return new NumberExpression(FLOAT.readAndValidateFrom(ar, context));
		}
		
	};

}
