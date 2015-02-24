package com.gmail.einsyui.buildutils.arithmetic;

public class BasicFunctions {
	public static class Plus implements Function{
		@Override
		public String name() {
			return "+";
		}
		@Override
		public Number evaluate(Number[] args) {
			double res=0;
			for(Number arg:args) res+=arg.doubleValue();
			return res;
		}
	};
	public static class Multiply implements Function{
		@Override
		public String name() {
			return "*";
		}
		@Override
		public Number evaluate(Number[] args) {
			double res=1;
			for(Number arg:args) res*=arg.doubleValue();
			return res;
		}
	};
	public static class Minus implements Function{
		@Override
		public String name() {
			return "-";
		}
		@Override
		public Number evaluate(Number[] args) {
			double res=args[0].doubleValue();
			for(int i=1;i<args.length;i++) res-=args[i].doubleValue();
			return res;
		}
	};
	public static class Divide implements Function{
		@Override
		public String name() {
			return "/";
		}
		@Override
		public Number evaluate(Number[] args) {
			double res=args[0].doubleValue();
			for(int i=1;i<args.length;i++) res/=args[i].doubleValue();
			return res;
		}
	};
	public static class Power implements Function{
		@Override
		public String name() {
			return "^";
		}
		@Override
		public Number evaluate(Number[] args) {
			double res=args[args.length-1].doubleValue();
			for(int i=args.length-2;i>=0;i--)
				res = Math.pow(args[i].doubleValue(), res);
			return res;
		}
	};
	public static class Sinus implements Function{
		@Override
		public String name() {
			return "sin";
		}
		@Override
		public Number evaluate(Number[] args) {
			return Math.sin(args[0].doubleValue());
		}
	};
	public static class Cosinus implements Function{
		@Override
		public String name() {
			return "cos";
		}
		@Override
		public Number evaluate(Number[] args) {
			return Math.cos(args[0].doubleValue());
		}
	};
	public static class Tangens implements Function{
		@Override
		public String name() {
			return "tan";
		}
		@Override
		public Number evaluate(Number[] args) {
			return Math.tan(args[0].doubleValue());
		}
	};
	public static class Logarithm implements Function{
		@Override
		public String name() {
			return "ln";
		}
		@Override
		public Number evaluate(Number[] args) {
			return Math.log(args[0].doubleValue());
		}
	};
}
