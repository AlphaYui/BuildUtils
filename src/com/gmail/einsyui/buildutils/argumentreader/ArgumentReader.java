package com.gmail.einsyui.buildutils.argumentreader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ArgumentReader {
	String arguments;
	int position;
	Stack<Argument> currentArgumentStack;
	CommandLibrary subcommandLibrary;
	
	public ArgumentReader(String arguments, CommandLibrary subcommandLibrary){
		this.arguments=arguments; position=0;
		currentArgumentStack = new Stack<Argument>();
		this.subcommandLibrary=subcommandLibrary;
		if(subcommandLibrary==null)
			replaceSubcommands=false;
		else replaceSubcommands=true;
	}
	public ArgumentReader(String arguments){
		this(arguments, null);
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
		throw new UnknownArgumentException("Unknown argument name.", name);
	}
	public void unspecifiedRequiredArgument(String name) throws ArgumentException{
		throw new ArgumentException("Required argument "+name+"left unspecified");
	}
	
	//// Basic reading
	public char readChar() throws ArgumentException{
		// Error if no character available (i.e. at end)
		if(position+1>arguments.length())
			syntaxError("Premature end of command.");
		if(arguments.charAt(position)=='[' || arguments.charAt(position)=='$') 
			replaceSubcommandHere();
		return arguments.charAt(position++);
	}
	public char tryReadChar() throws ArgumentException{
		// return \0 character if no character available (i.e. at end)
		if(position+1>arguments.length())
			return '\0'; 
		if(arguments.charAt(position)=='[' || arguments.charAt(position)=='$') 
			replaceSubcommandHere();
		return arguments.charAt(position++);
	}
	public String readString(int length) throws ArgumentException{ 
		// read String of specific length
		// Error if not enough characters are available
		if(position+length>=arguments.length()
				&& (arguments.indexOf('[', position)==-1 
					|| !replaceSubcommands))
			syntaxError("Premature end of command.");
		String res = arguments.substring(position, 
						Math.min(position+length,arguments.length()));
		int i=res.indexOf('[');
		int j=res.indexOf('$');
		if(replaceSubcommands && (i!=-1 || j!=-1)){
			if(i==-1) i=j; else if(j!=-1) i=Math.min(i, j);
			position+=i;
			replaceSubcommandHere();
			return res.substring(0, i) + readString(length-i);
		}
		position+=length;
		return res;
	}
	public char peekChar() throws ArgumentException{
		int pos=position;
		char c= tryReadChar();
		position=pos;
		return c;
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
			throws ArgumentException{
		char got = readChar();
		if(got!=c)
			syntaxError("Expected '"+c+"' "+positionDescription+", got '"+got+"'.");
	}
	public void expect(String str, String positionDescription, boolean ignoreCase) 
			throws ArgumentException{
		String got = readString(str.length());
		if(str!=got)
			syntaxError("Expected \""+str+"\" "+position+", got \""+got+"\".");
	}
	public void expect(String str, String positionDescription) 
			throws ArgumentException{
		expect(str, positionDescription, true);
	}
	
	//// if next thing is ..., read and return true. Else, return false
	public boolean tryExpect(char c) throws ArgumentException{
		int pos=position;
		if(c==tryReadChar()) return true;
		else{
			position=pos;
			return false;
		}
	}
	public boolean tryExpect(String str){
		if(arguments.length()<=position+str.length()) return false;
		if(arguments.regionMatches(position, str, 0, str.length())){
			position+=str.length();
			return true;
		}else return false;
	}
	
	//// skip whitespace (spaces & tabs)
	public void skipWhitespace(){
		if(!atEnd())
			while(arguments.charAt(position)==' ' 
				|| arguments.charAt(position)=='\t'){
				position++;
			}
	}
	
	//// Argument syntax
	public Object readArgument(Argument arg, Context context) throws ArgumentException{
		return arg.readAndValidateValueFrom(this, context);
	}
	public Map<String, Object> readArguments(List<Argument> args,
			Context ctx) 
			throws ArgumentException{
		HashMap<String, Object> res = new HashMap<String, Object>();
		int argumentPosition=0; // next positional argument
		
		while(!atEnd()){
			skipWhitespace();
			int pos = position; 
			
			// Check if it is a named argument (see below)
			boolean named = true; String name="";
			boolean positional=false;
			try{ // Kind of not-to-cool, deciding on error...
				name = ArgumentType.IDENTIFIER.readAndValidateFrom(this, ctx);
				skipWhitespace();
				expect('=',"");
			}catch(ArgumentException e){
				named=false; position=pos;
			}
			
			if(named){ /// Named arguments. Syntax NAME = VALUE
				Argument arg = Argument.findByName(name, args);
				if(arg==null) unknownArgument(name);
				res.put(arg.name(), arg.readAndValidateValueFrom(this, ctx));
			}
			
			else if(peekChar()=='+' || peekChar()=='-'){ // Special flags. Syntax: +FLAG / -FLAG
				int oldpos=position;
				char c = readChar();
				String flagName=null;
				try{
					flagName = ArgumentType.IDENTIFIER.readAndValidateFrom(this, ctx);
				}catch(ArgumentException e){
					position=oldpos;
					positional=true;
				}
				if(!positional){
					Argument arg = Argument.findByName(flagName, args);
					if(!(arg.type() instanceof ArgumentType.TFlag)) // not a flag
						syntaxError("Argument "+arg.name()+" is not a valid flag");
					if(c=='+') res.put(arg.name(), true);
					if(c=='-') res.put(arg.name(), false);
				}
			}
			else positional=true;
			
			if(positional==true){ // Positional argument
				// skip already specified arguments
				while(res.containsKey(args.get(argumentPosition).name())){
					argumentPosition++;
					if(argumentPosition>=args.size()) break; // prevent IndexOutOfBounds.
				}
				if(argumentPosition>=args.size())
					syntaxError("Found trailing garbage (Found positional argument after all arguments were set): "
							+ arguments.substring(position));
				Argument arg = args.get(argumentPosition);
				res.put(arg.name(), arg.readAndValidateValueFrom(this, ctx));
			}
		}
		
		// Handle default && required values
		while(argumentPosition<args.size()){
			if(!res.containsKey(args.get(argumentPosition).name())){
				Argument arg = args.get(argumentPosition);
				if(arg instanceof AbstractArgumentWithDefault){
					res.put(arg.name(), 
							((ArgumentWithDefault) arg)
							.defaultValue(ctx));
				}else if(arg.required()){
					unspecifiedRequiredArgument(arg.name());
				}
			}
			argumentPosition++;
		}
		
		return res;
	}
	
	//// Sub-commands
	boolean replaceSubcommands=true;
	public void deactivateSubcommands(){ replaceSubcommands = false; }
	public void activateSubcommands(){
		if(subcommandLibrary!=null)
			replaceSubcommands = true; 
	}
	public boolean subcommandsActivated(){ return replaceSubcommands; }
	public void setSubcommandLibrary(CommandLibrary cl){
		subcommandLibrary = cl;
	}
	public CommandLibrary getSubcommandLibrary(){ return subcommandLibrary; }
	protected void replaceSubcommandHere() throws ArgumentException{
		if(!replaceSubcommands) return;
		int pos=position; // save current position for later use
		
		String subcmd;
		replaceSubcommands=false;
		if(tryExpect('$')){
			String varname = ArgumentType.IDENTIFIER.readAndValidateFrom(this, null);
			subcmd="var read "+varname;
		}else{
			// read command and execute it
			subcmd = ArgumentType.STRING_IN_SQUARE_BRACKETS
						.readAndValidateFrom(this, null);
		}
		replaceSubcommands=true;
		String value = subcommandLibrary.execute(subcmd);
		
		// replace in String (StringBuffer needed for replace by Index)
		//  (sadly, String copied, so O(n)? )
		StringBuffer sb = new StringBuffer(arguments);
		sb.replace(pos, position, value);
		
		// set arguments & position
		arguments = sb.toString();
		position=pos;
	}
}
