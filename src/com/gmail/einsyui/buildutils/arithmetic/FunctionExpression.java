package com.gmail.einsyui.buildutils.arithmetic;

public class FunctionExpression implements Expression {
	Function fn;
	Expression[] args;
	
	public FunctionExpression(Function fn, Expression[] args){
		this.fn=fn; this.args=args;
	}

	@Override
	public Number evaluate() {
		Number[] evaluatedArgs = new Number[args.length];
		for(int i=0;i<args.length;i++)
			evaluatedArgs[i]=args[i].evaluate();
		return fn.evaluate(evaluatedArgs);
	}

}
