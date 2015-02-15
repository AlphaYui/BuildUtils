package com.gmail.marzipankaiser.argumentreader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		public String getArgumentName(){return name;}
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
	public boolean atEnd(){
		return position>=arguments.length();
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
	
	//// Argument syntax
	public Object readArgument(Argument arg) throws ArgumentException{
		return arg.readAndValidateValueFrom(this);
	}
	public Map<String, Object> readArguments(List<Argument> args) throws ArgumentException{
		HashMap<String, Object> res = new HashMap<String, Object>();
		int argumentPosition=0; // next positional argument
		
		while(!atEnd()){
			skipWhitespace();
			int pos = position; 
			
			// Check if it is a named argument (see below)
			boolean named = true; String name="";
			try{ // Kind of not-to-cool, deciding on error...
				name = ArgumentType.IDENTIFIER.readAndValidateFrom(this);
				skipWhitespace();
				expect('=',"");
			}catch(ArgumentException e){
				named=false; position=pos;
			}
			
			if(named){ /// Named arguments. Syntax NAME = VALUE
				Argument arg = Argument.findByName(name, args);
				res.put(arg.name(), arg.readAndValidateValueFrom(this));
			}
			
			else if(peekChar()=='+' || peekChar()=='-'){ // Special flags. Syntax: +FLAG / -FLAG
				char c = readChar();
				String flagName = ArgumentType.IDENTIFIER.readAndValidateFrom(this);
				Argument arg = Argument.findByName(flagName, args);
				if(!(arg.type() instanceof ArgumentType.TFlag)) // not a flag
					syntaxError("Argument "+arg.name()+" is not a valid flag");
				if(c=='+') res.put(arg.name(), true);
				if(c=='-') res.put(arg.name(), false);
			}
			
			else{ // Positional argument
				// skip already specified arguments
				while(res.containsKey(args.get(argumentPosition).name())){
					argumentPosition++;
					if(argumentPosition>=args.size()) break; // prevent IndexOutOfBounds.
				}
				if(argumentPosition>=args.size())
					syntaxError("Found trailing garbage (Found positional argument after all arguments were set)");
				Argument arg = args.get(argumentPosition);
				arg.readAndValidateValueFrom(this);
			}
		}
		
		return res;
	}
}
