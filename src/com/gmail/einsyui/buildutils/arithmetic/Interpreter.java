package com.gmail.einsyui.buildutils.arithmetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;
import com.gmail.einsyui.buildutils.argumentreader.ArgumentType;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Cosinus;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Divide;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Logarithm;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Minus;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Multiply;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Plus;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Power;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Sinus;
import com.gmail.einsyui.buildutils.arithmetic.BasicFunctions.Tangens;

public class Interpreter{
	public List<Function> ops = new ArrayList<Function>();
	public Map<String, Function> fns = new HashMap<String, Function>();
	public char open, close;
	public ArgumentType<? extends Expression> atomType;
	
	private Expression readOpExpression(ArgumentReader ar, int op_level) 
			throws ArgumentException{
		if(op_level>=ops.size()){
			return readFnExpression(ar);
		}else{
			ArrayList<Expression> args = new ArrayList<Expression>();
			Function op = ops.get(op_level);
			do{
				args.add(readOpExpression(ar, op_level+1));
			}while(ar.tryExpect(op.name()));
			if(args.size()==1) return args.get(0);
			Expression[] e=new Expression[args.size()];
			return new FunctionExpression(op, args.toArray(e));
		}
	}

	private Expression readFnExpression(ArgumentReader ar) 
			throws ArgumentException {
		int pos = ar.position();
		if(ar.tryExpect(open)){
			Expression res = readOpExpression(ar, 0);
			ar.expect(close, "to match "+open);
			return res;
		}
		String fnName="";
		try {
			fnName = ArgumentType.IDENTIFIER.readAndValidateFrom(ar, null);
			ar.skipWhitespace();
			ar.expect(open,"");
		} catch (ArgumentException e) {
			ar.setPosition(pos);
			return atomType.readAndValidateFrom(ar, null);
		}
		ArrayList<Expression> args = new ArrayList<Expression>();
		do{
			ar.skipWhitespace();
			args.add(readOpExpression(ar, 0));
			ar.skipWhitespace();
		}while(ar.tryExpect(','));
		ar.expect(close, "after function arguments");
		Expression[] e = new Expression[args.size()];
		return new FunctionExpression(fns.get(fnName), args.toArray(e));
	}
	
	public Expression readExpression(ArgumentReader ar) throws ArgumentException{
		return readOpExpression(ar, 0);
	}
	
	public static Interpreter makeInterpreter(ArgumentType<? extends Expression> atom){
		Interpreter res = new Interpreter();
		res.atomType=atom;
		res.open='('; res.close=')';
		res.ops.add(new Plus());
		res.ops.add(new Minus());
		res.ops.add(new Multiply());
		res.ops.add(new Divide());
		res.ops.add(new Power());
		
		res.addFn(new Sinus());
		res.addFn(new Cosinus());
		res.addFn(new Tangens());
		res.addFn(new Logarithm());
		
		res.atomType = new ArgumentType.TOr<Expression>(
				new NumberExpression.TNumberExpression(),
				new VariableExpression.TVariableExpression());
		return res;
	}

	private void addFn(Function fn) {
		fns.put(fn.name(), fn);
	}
}
