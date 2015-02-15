package com.gmail.marzipankaiser.argumentreader;

import java.util.Stack;

public class ArgumentReader {
	String arguments;
	int position;
	Stack<Argument> currentArgumentStack;
	
	public ArgumentReader(String arguments){
		this.arguments=arguments; position=0;
		currentArgumentStack = new Stack<Argument>();
	}
	
	//// Exception classes
	public class ArgumentException extends Exception{
		private static final long serialVersionUID = -5322508080772813318L;
		public ArgumentException(String msg){
			super(msg);
		}
	}; 
	public class ArgumentSyntaxException extends ArgumentException{
		private static final long serialVersionUID = -9038854115751111790L;
		protected Argument inArgument;
		public ArgumentSyntaxException(String msg, Argument arg){
			super(msg); inArgument=arg;
		}
		public Argument inArgument(){ return inArgument; }
	};
	public class UnknownArgumentException extends ArgumentException{
		private static final long serialVersionUID = 2261534049445636858L;
		protected String name;
		public UnknownArgumentException(String msg, String name){
			super(msg); this.name=name;
		}
	};
	
	//// Position metadata
	public void beginArgument(Argument arg){
		currentArgumentStack.push(arg);
	}
	public void endArgument(){
		currentArgumentStack.pop();
	}
	
	//// Exception methods
	public void syntaxError(String msg) throws ArgumentSyntaxException{
		if(currentArgumentStack.empty())
			throw new ArgumentSyntaxException(msg, null);
		else
			throw new ArgumentSyntaxException(msg, 
				currentArgumentStack.peek());
	}
	public void unknownArgument(String name) throws UnknownArgumentException{
		throw new UnknownArgumentException("Uknown argument name.", name);
	}
	
	//// Basic reading
	public char readChar() throws ArgumentSyntaxException{
		// Error if no character available (i.e. at end)
		if(position+1>=arguments.length())
			syntaxError("Premature end of command.");
		return arguments.charAt(position++);
	}
	public char tryReadChar(){
		// return \0 character if no character available (i.e. at end)
		if(position+1>=arguments.length())
			return '\0'; 
		return arguments.charAt(position++);
	}
	public String readString(int length) throws ArgumentSyntaxException{ 
		// read String of specific length
		// Error if not enough characters are available
		if(position+length>=arguments.length())
			syntaxError("Premature end of command.");
		String res = arguments.substring(position, position+length);
		position+=length;
		return res;
	}
	public char peekChar(){
		return arguments.charAt(position);
	}
	public void back(){ // jump back one character (= unread)
		position--;
	}
	
	/// Internal
	public void setPosition(int p){ position=p; }
	public int position(){ return position; }
	public String getWholeArguments(){ return arguments; }
	
	//// Expect
	public void expect(char c, String positionDescription) 
			throws ArgumentSyntaxException{
		char got = readChar();
		if(got!=c)
			syntaxError("Expected '"+c+"' "+positionDescription+", got '"+got+"'.");
	}
	public void expect(String str, String positionDescription, boolean ignoreCase) 
			throws ArgumentSyntaxException{
		String got = readString(str.length());
		if(str!=got)
			syntaxError("Expected \""+str+"\" "+position+", got \""+got+"\".");
	}
	public void expect(String str, String positionDescription) 
			throws ArgumentSyntaxException{
		expect(str, positionDescription, true);
	}
	
	//// if next thing is ..., read and return true. Else, return false
	public boolean tryExpect(char c){
		return c==tryReadChar();
	}
	public boolean tryExpect(String str){
		if(arguments.regionMatches(position, str, 0, str.length())){
			position+=str.length();
			return true;
		}else return false;
	}
	
	//// skip whitespace (spaces & tabs)
	public void skipWhitespace(){
		while(arguments.charAt(position)==' ' 
			|| arguments.charAt(position)=='\t'){
			position++;
		}
	}
}
