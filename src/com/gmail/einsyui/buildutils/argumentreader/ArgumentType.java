package com.gmail.einsyui.buildutils.argumentreader;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gmail.einsyui.buildutils.argumentreader.ArgumentReader.ArgumentException;

public interface ArgumentType {
	public Object readAndValidateFrom(ArgumentReader ar, Context context) 
			throws ArgumentException;
	public String name();
	
	////-----------------------------------------------------------------
	/// Numbers (in LOCALE-SPECIFIC Format)
	public static class TLocaleSpecificNumber implements ArgumentType{
		boolean integer;
		public TLocaleSpecificNumber(boolean integer){ 
			this.integer=integer; 
		}
		@Override
		public Number readAndValidateFrom(ArgumentReader ar, Context context) 
				throws ArgumentException {
			// use Java's NumberFormat to read Numbers in Locale-specific Format
			ParsePosition pp = new ParsePosition(ar.position());
			NumberFormat nf;
			if(integer)
				nf = NumberFormat.getIntegerInstance();
			else
				nf = NumberFormat.getInstance();
			Number n = nf.parse(ar.getWholeArguments(), pp);
			if(pp.getIndex()==ar.position()) // nothing read => no number
				ar.syntaxError("Expected number");
			ar.setPosition(pp.getIndex());
			return n;
		}
		public String name(){return "Locale specific number";}
	};
	public static final TLocaleSpecificNumber LS_NUMBER 
		= new TLocaleSpecificNumber(false);
	public static final TLocaleSpecificNumber LS_INTEGER_NUMBER 
		= new TLocaleSpecificNumber(true);
	
	////-----------------------------------------------------------------
	/// Identifiers (i.e.: valid Java identifiers as Strings)
	public static class TIdentifier implements ArgumentType{
		@Override
		public String readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			StringBuilder res = new StringBuilder(); // to hold result
			char c = ar.readChar();
			if(!Character.isJavaIdentifierStart(c))
				ar.syntaxError("Identifier can't start with '"+c+"'.");
			do{
				res.append(c);
				c = ar.tryReadChar();
			}while(Character.isJavaIdentifierPart(c) && c!='\0');
			if(c!='\0') ar.back();
			return res.toString();
		}
		public String name(){return "Identifier";}
	};
	public final static TIdentifier IDENTIFIER = new TIdentifier();
	
	////-----------------------------------------------------------------
	/// Flags
	// to be inherited by Boolean-like, i.e. Flag (+/-) arg
	public static interface TFlag extends ArgumentType{}; 
	
	////-----------------------------------------------------------------
	/// Booleans
	public static class TBoolean implements ArgumentType, TFlag{
		@Override
		public Boolean readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			switch(ar.readChar()){
			case '+':
			case '1':
				return true;
			case '-':
			case '0':
				return false;
			}
			ar.back();
			
			String name; int pos = ar.position();
			try{
				name = IDENTIFIER.readAndValidateFrom(ar, context).toLowerCase();
				if("true".startsWith(name) || "yes".startsWith(name))
					return true;
				if("false".startsWith(name) || "no".startsWith(name))
					return false;
			}catch(ArgumentException e){
				ar.setPosition(pos); // jump back before identifier
			}
			ar.syntaxError("Expected boolean, got '"+ar.peekChar()+"'.");
			return null;
		}
		public String name(){return "Boolean";}
	};
	public final static TBoolean BOOLEAN = new TBoolean();
	

	////-----------------------------------------------------------------
	/// Digits
	public static class TDigit implements ArgumentType{
		int radix;
		public TDigit(int radix){
			assert radix<=Character.MAX_RADIX && radix>=Character.MIN_RADIX;
			this.radix=radix;
		}
		@Override
		public Integer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			char d = ar.readChar();
			int r = Character.digit(d, radix);
			if(r==-1) 
				ar.syntaxError("Expected base "+radix+" digit, got '"+d+"'.");
			return r;
		}
		public String name(){return "Base "+radix+" Digit";}
	};
	public final static TDigit OCTAL_DIGIT = new TDigit(8);
	public final static TDigit DECIMAL_DIGIT = new TDigit(10);
	public final static TDigit HEXADECIMAL_DIGIT = new TDigit(16);
	
	////-----------------------------------------------------------------
	/// Fixed-radix Integers
	public static class TFixedRadixInteger implements ArgumentType{
		int radix;
		public TFixedRadixInteger(int radix){
			assert radix>=Character.MIN_RADIX && radix<=Character.MAX_RADIX;
			this.radix=radix;
		}
		@Override
		public Integer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int sign=1;
			if(ar.peekChar()=='+' || ar.peekChar()=='-'){
				char s=ar.readChar();
				if(s=='-') sign=-1;
			}
			
			int res=0;
			char c=ar.readChar();
			int d = Character.digit(c, radix);
			do{
				res*=radix; res+=d;
				c=ar.tryReadChar(); d=Character.digit(c, radix);
			}while(c!='\0' && d!=-1);
			if(c!='\0') ar.back();
			return res*sign;
		}
		public String name(){return "Base "+radix+" integer";}
	};
	public final static TFixedRadixInteger BINARY_INTEGER
		= new TFixedRadixInteger(2);
	public final static TFixedRadixInteger OCTAL_INTEGER
		= new TFixedRadixInteger(8);
	public final static TFixedRadixInteger DECIMAL_INTEGER
		= new TFixedRadixInteger(10);
	public final static TFixedRadixInteger HEXADECIMAL_INTEGER
		= new TFixedRadixInteger(16);
	
	////-----------------------------------------------------------------
	/// Integers
	public static class TInteger implements ArgumentType{
		@Override
		public Integer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int sign=1;
			if(ar.peekChar()=='+' || ar.peekChar()=='-'){
				char s=ar.readChar();
				if(s=='-') sign=-1;
			}
			
			int radix=10;
			if(ar.peekChar()=='0'){
				ar.readChar();
				char f=ar.tryReadChar();
				switch(f){
				case 'b': radix=2; break;
				case 'o': radix=8; break;
				case 'd': radix=10; break;
				case '\0': radix=10; ar.back(); break;
				case 'x': radix=16; break;
				default: radix=8; ar.back(); ar.back(); // TODO: Java-like 010 == 8 ?
				}
			}
			if(ar.tryExpect('r')){ //TODO: choose character to give radix
				radix = DECIMAL_INTEGER.readAndValidateFrom(ar, context);
				if(radix>Character.MAX_RADIX || radix<Character.MIN_RADIX)
					ar.syntaxError("Invalid radix "+radix+" in radix specification.");
				ar.expect('r', "after radix specification"); //TODO: choose char
			}
			return sign*(new TFixedRadixInteger(radix)).readAndValidateFrom(ar, context);
		}
		public String name(){return "integer";}
	};
	public final static TInteger INTEGER = new TInteger();
	
	////-----------------------------------------------------------------
	/// Integers in range
	public static class TIntegerInRange extends TInteger{
		int min, max;
		public TIntegerInRange(int min, int max){
			this.min = min; this.max = max;
		}
		@Override
		public Integer readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int res = super.readAndValidateFrom(ar, context);
			if(res>max)
				ar.syntaxError("Expected integer <="+max+", got "+res+".");
			if(res<min)
				ar.syntaxError("Expected integer >="+min+", got "+res+".");
			return res;
		}
		public String name(){return "integer ["+min+";"+max+"]";}
	};
	
	////-----------------------------------------------------------------
	/// Fixed-radix Floating point numbers
	public static class TFixedRadixFloat implements ArgumentType{
		int radix; char dot;
		public TFixedRadixFloat(int radix, char dot){
			this.radix=radix; this.dot=dot;
		}
		public TFixedRadixFloat(int radix){
			this(radix, '.');
		}
		@Override
		public Double readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			double sign=1;
			if(ar.peekChar()=='+' || ar.peekChar()=='-'){
				char s=ar.readChar();
				if(s=='-') sign=-1;
			}
			
			// digits before dot
			double res=0;
			char c=ar.readChar();
			int d = Character.digit(c, radix);
			do{
				res*=radix; res+=d;
				c=ar.tryReadChar(); d=Character.digit(c, radix);
			}while(c!='\0' && d!=-1 && c!=dot);
			
			if(c==dot){ // digits after dot
				double currentRadixExp=1.0d/radix;
				c=ar.readChar(); // reuse variables from above
				d = Character.digit(c, radix);
				do{
					res+=d*currentRadixExp;
					currentRadixExp/=radix;
					c=ar.tryReadChar(); d=Character.digit(c, radix);
				}while(c!='\0' && d!=-1);
				
				if(c=='e'  // exponent
					|| (c=='*' && radix>=15)
					|| (c=='x' && radix>=15)){
					int exp = INTEGER.readAndValidateFrom(ar, context);
					res*=Math.pow(radix, exp);
				}
			}
			
			return sign*res;
		}
		public String name(){return "Base "+radix+" float";}
	};
	public static final TFixedRadixFloat BINARY_FLOAT
		= new TFixedRadixFloat(2);
	public static final TFixedRadixFloat OCTAL_FLOAT
		= new TFixedRadixFloat(8);
	public static final TFixedRadixFloat DECIMAL_FLOAT
		= new TFixedRadixFloat(10);
	public static final TFixedRadixFloat HEXADECIMAL_FLOAT
		= new TFixedRadixFloat(16);

	////-----------------------------------------------------------------
	/// Floating point numbers
	public static class TFloat implements ArgumentType{
		char dot;
		public TFloat(char dot){
			this.dot = dot;
		}
		@Override
		public Double readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int sign=1;
			if(ar.peekChar()=='+' || ar.peekChar()=='-'){
				char s=ar.readChar();
				if(s=='-') sign=-1;
			}
			
			int radix=10;
			if(ar.peekChar()=='0'){
				ar.readChar();
				char f=ar.tryReadChar();
				switch(f){
				case 'b': radix=2; break;
				case 'o': radix=8; break;
				case 'd': radix=10; break;
				case 'x': radix=16; break;
				case '\0': radix=10; ar.back(); break;
				default: radix=8; ar.back(); ar.back(); // TODO: Java-like 010 == 8 ?
				}
			}
			if(ar.tryExpect('r')){ //TODO: choose character to give radix
				radix = DECIMAL_INTEGER.readAndValidateFrom(ar, context);
				if(radix>Character.MAX_RADIX || radix<Character.MIN_RADIX)
					ar.syntaxError("Invalid radix "+radix+" in radix specification.");
				ar.expect('r', "after radix specification"); //TODO: choose char
			}
			return sign*(new TFixedRadixFloat(radix)).readAndValidateFrom(ar, context);
		}
		public String name(){return "float";}
	};
	public static final TFloat FLOAT = new TFloat('.');
	
	////-----------------------------------------------------------------
	/// Floats in range
	public static class TFloatInRange extends TFloat{
		double min, max;
		public TFloatInRange(double min, double max, char dot){
			super(dot);
			this.min = min; this.max = max;
		}
		public TFloatInRange(double min, double max){
			this(min, max, '.');
		}
		@Override
		public Double readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			double res = super.readAndValidateFrom(ar, context);
			if(res>max)
				ar.syntaxError("Expected float <="+max+", got "+res+".");
			if(res<min)
				ar.syntaxError("Expected float >="+min+", got "+res+".");
			return res;
		}
		public String name(){return "float ["+min+";"+max+"]";}
	};	
	
	////-----------------------------------------------------------------
	/// Enums
	public static class TEnum implements ArgumentType{
		@SuppressWarnings("rawtypes")
		Class<? extends Enum> enumType;
		public <T extends Enum<T>> TEnum(Class<T> enumClass){
			this.enumType=enumClass;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected static Enum<?> valueOf(Class<? extends Enum> enumType,
										String name){
			try{
				return Enum.valueOf(enumType, name);
			}catch(IllegalArgumentException e){
				return null;
			}
		}
		@SuppressWarnings("rawtypes")
		protected Enum<?>[] values(Class<? extends Enum> enumType){
			return enumType.getEnumConstants();
		}
		protected String onlyAlphanumeric(String str){
			StringBuilder sb = new StringBuilder();
			for(char c:str.toCharArray()){
				if(Character.isLetterOrDigit(c))
					sb.append(c);
			}
			return sb.toString();
			//return str.replaceAll("[^a-zA-Z0-9]", "");
		}
		@Override
		public Enum<?> readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name = IDENTIFIER.readAndValidateFrom(ar, context);
			Enum<?> res = valueOf(enumType, name);
			if(res==null)
				res = valueOf(enumType, name.toUpperCase());
			if(res==null){
				Enum<?> values[] = values(enumType);
				name = onlyAlphanumeric(name);
				for(Enum<?> value:values){
					if(onlyAlphanumeric(value.name())
							.equalsIgnoreCase(name)){
						res = value; break;
					}
				}
			}
			return res;
		}
		public String name(){return "Enum "+enumType.getName();}
	};
	
	////-----------------------------------------------------------------
	/// Bytes
	public static class TByte implements ArgumentType{
		static final TIntegerInRange sub = new TIntegerInRange(0,255); 
		@Override
		public Byte readAndValidateFrom(ArgumentReader ar, Context context) throws ArgumentException{
			return sub.readAndValidateFrom(ar, context).byteValue();
		}
		public String name(){return "byte";}
	};
	public static final TByte BYTE = new TByte();
	
	////-----------------------------------------------------------------
	/// Custom Enum (HashMap based)
	public static class TCustomEnum implements ArgumentType{
		HashMap<String, Object> map; boolean caseSensitive;
		public TCustomEnum(boolean caseSensitive){
			map = new HashMap<String, Object>();
			this.caseSensitive=caseSensitive;
		}
		public void put(String name, Object value){
			if(caseSensitive)
				map.put(name, value);
			else map.put(name.toLowerCase(), value);
		}
		public void putEnumConstants(Class<? extends Enum<?>> e){
			Enum<?>[] consts = e.getEnumConstants();
			for(Enum<?> enumConst : consts){
				put(enumConst.name(),enumConst);
			}
		}
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String name;
			if(caseSensitive)
				name = IDENTIFIER.readAndValidateFrom(ar, context);
			else
				name = IDENTIFIER.readAndValidateFrom(ar, context).toLowerCase();
			if(!map.containsKey(name))
				ar.syntaxError("\""+name+"\" is not a valid value");
			return map.get(name);
		}
		public String name(){return "custom enum";} //TODO: improve
	};
	
	////-----------------------------------------------------------------
	/// Classes
	public static class TClass implements ArgumentType{
		@Override
		public Class<?> readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			StringBuilder name = new StringBuilder();
			name.append(IDENTIFIER.readAndValidateFrom(ar, context));
			while(ar.tryExpect('.')){
				name.append('.');
				name.append(IDENTIFIER.readAndValidateFrom(ar, context));
			}
			try {
				return Class.forName(name.toString());
			} catch (ClassNotFoundException e) {
				ar.syntaxError("Invalid class name "+name);
				return null;
			}
		}
		public String name(){return "Class";}
	};
	public static final TClass CLASS = new TClass();
	
	////-----------------------------------------------------------------
	/// Strings (with "" - see also: TIdentifier)
	public static class TString implements ArgumentType{
		@Override
		public String readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			ar.expect('"', "before String");
			StringBuilder res = new StringBuilder();
			while(true){
				char c = ar.readChar();
				if(c=='"') return res.toString();
				if(c=='\\'){ // escape character
					c = ar.readChar();
					//TODO: add handling digits and specials etc (\0, \n, ...)
				}
				res.append(c);
			}
		}
		public String name(){return "string";}
	};
	public static final TString STRING = new TString();

	////-----------------------------------------------------------------
	/// in Balanced Brackets
	public static class TStringInBalancedBrackets implements ArgumentType{
		char open, close; boolean skipWhitespace;
		public TStringInBalancedBrackets(char openBracket, char closeBracket,
				boolean skipWhitespace){
			open=openBracket; close=closeBracket;
			this.skipWhitespace=skipWhitespace;
		}
		public TStringInBalancedBrackets(char openBracket, char closeBracket){
			this(openBracket, closeBracket, false);
		}
		@Override
		public String readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			if(skipWhitespace) ar.skipWhitespace();
			ar.expect(open, "before bracketed expression");
			StringBuilder res = new StringBuilder();
			int depth=0;
			while(true){
				char c = ar.readChar();
				if(skipWhitespace && c==' ') // multiple spaces => one space
					ar.skipWhitespace();
				if(c==close && depth==0) return res.toString();
				if(c==close){ 
					depth--;
					if(depth<0) ar.syntaxError("Unmatched close "+close);
				}
				if(c==open) depth++;
				res.append(c);
			}
		}
		@Override
		public String name() {
			return "string in "+open+close;
		}
	};
	public static final TStringInBalancedBrackets STRING_PARENTHESIZED 
		= new TStringInBalancedBrackets('(',')');
	public static final TStringInBalancedBrackets STRING_IN_SQUARE_BRACKETS 
		= new TStringInBalancedBrackets('[',']');
	public static final TStringInBalancedBrackets STRING_IN_ANGLE_BRACKETS 
		= new TStringInBalancedBrackets('<','>');
	public static final TStringInBalancedBrackets STRING_IN_CURLY_BRACES 
		= new TStringInBalancedBrackets('{','}');
	
	////-----------------------------------------------------------------
	// Rest as String
	public static class TRestAsString implements ArgumentType{
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String res=ar.getWholeArguments().substring(ar.position());
			ar.setPosition(ar.getWholeArguments().length());
			return res;
		}
		@Override
		public String name() {
			return "raw";
		}
	};
	public static final TRestAsString REST_AS_RAW_STRING
		= new TRestAsString();
	
	////-----------------------------------------------------------------
	/// UUIDs
	public static class TUUID implements ArgumentType{
		public static String readHexDigits(ArgumentReader ar, int n,
				Context context) 
				throws ArgumentException{
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<n;i++)
				sb.append(Integer.toString(
						HEXADECIMAL_DIGIT.readAndValidateFrom(ar, context),
						16));
			return sb.toString();
		}
		@Override
		public UUID readAndValidateFrom(ArgumentReader ar, Context ctx)
				throws ArgumentException {
			StringBuilder sb = new StringBuilder();
			sb.append(readHexDigits(ar,8, ctx));
			ar.expect('-',"in UUID"); sb.append('-');
			sb.append(readHexDigits(ar,4, ctx));
			ar.expect('-',"in UUID"); sb.append('-');
			sb.append(readHexDigits(ar,4, ctx));
			ar.expect('-',"in UUID"); sb.append('-');
			sb.append(readHexDigits(ar,4, ctx));
			ar.expect('-',"in UUID"); sb.append('-');
			sb.append(readHexDigits(ar,12, ctx));
			return UUID.fromString(sb.toString());
		}
		@Override
		public String name() {
			return "UUID";
		}
	};
	public static final TUUID JAVA_UUID = new TUUID();
	
	////-----------------------------------------------------------------
	/// Commands
	public static final class TCommand implements ArgumentType{
		@Override
		public CommandWithArgs readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			ar.deactivateSubcommands();
			String cmdstr=STRING_IN_SQUARE_BRACKETS.readAndValidateFrom(ar, context);
			ar.activateSubcommands();
			return ar.getSubcommandLibrary().getCommandWithArgs(cmdstr);
		}
		@Override
		public String name() {
			return "command in []";
		}
	};
	public static final TCommand COMMAND = new TCommand();
	
	////-----------------------------------------------------------------
	/// Commands with late-binding args
	public static final class TLateCommand implements ArgumentType{
		@Override
		public CommandWithLateArgs readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			ar.deactivateSubcommands();
			String cmdstr=STRING_IN_SQUARE_BRACKETS.readAndValidateFrom(ar, context);
			ar.activateSubcommands();
			return ar.getSubcommandLibrary().getCommandWithLateArgs(cmdstr);
		}
		@Override
		public String name() {
			return "command in []";
		}
	};
	public static final TLateCommand LATE_COMMAND = new TLateCommand();
	
	
	////-----------------------------------------------------------------
	////-----------------------------------------------------------------
	/// Type Combinators
	
	////-----------------------------------------------------------------
	/// Or
	public static class TOr implements ArgumentType{
		ArgumentType[] types;
		public TOr(ArgumentType...argumentTypes){
			types=argumentTypes;
		}
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int position = ar.position();
			for(ArgumentType type:types){
				try{
					Object res = type.readAndValidateFrom(ar, context);
					return res;
				}catch(ArgumentException e){
					ar.setPosition(position);
				}
			}
			ar.syntaxError("Invalid argument type"); //TODO: improve message
			return null;
		}
		public String name(){
			StringBuilder name = new StringBuilder();
			boolean firstTime=true;
			for(ArgumentType type:types){
				if(firstTime){ firstTime=false; }
				else name.append(" | ");
				name.append(type.name());
			}
			return name.toString();
		}
	};
	
	////-----------------------------------------------------------------
	/// Delimited Lists
	public static class TDelimitedList implements ArgumentType{
		char start, delimiter, end;
		boolean skipWhitespace;
		ArgumentType elementType;
		public TDelimitedList(char start, char delimiter, char end,
				ArgumentType elementType,
				boolean skipWhitespace){
			this.start=start; this.delimiter=delimiter; this.end=end;
			this.skipWhitespace=skipWhitespace;
			this.elementType = elementType;
		}
		public TDelimitedList(char start, char delimiter, char end,
				ArgumentType elementType){
			this(start, delimiter, end, elementType, true);
		}
		public TDelimitedList(char start, char end, ArgumentType elementType){
			this(start, ',', end, elementType, true);
		}
		public TDelimitedList(ArgumentType elementType){
			this('(', ',', ')', elementType, true);
		}
		@Override
		public List<Object> readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			if(skipWhitespace) ar.skipWhitespace();
			ar.expect(start, "at beginning of delimited list");
			List<Object> res = new ArrayList<Object>();
			do{
				if(skipWhitespace) ar.skipWhitespace();
				res.add(elementType.readAndValidateFrom(ar, context));
				if(skipWhitespace && delimiter!=' ') ar.skipWhitespace();
			}while(ar.tryExpect(delimiter));
			ar.expect(end, "at end of delimited list");
			return res;
		}
		public String name(){return "list of ("+elementType.name()+") in "
				+start+"A"+delimiter+"..."+end;}
	};
	
	////-----------------------------------------------------------------
	/// Command arguments in brackets
	public static class TCommandLikeArgumentsInBrackets implements ArgumentType{
		List<Argument> args;
		ArgumentType stringReader;
		public TCommandLikeArgumentsInBrackets(char open, char close, 
				List<Argument> args){
			stringReader = new TStringInBalancedBrackets(open, close);
			this.args=args;
		}
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			String argstr = (String) stringReader.readAndValidateFrom(ar, context);
			ArgumentReader subar = new ArgumentReader(argstr, 
										ar.getSubcommandLibrary());
			return subar.readArguments(args, context);
		}
		@Override
		public String name() {
			return "";
		}
	};
	////-----------------------------------------------------------------
	/// Constructor argument
	public static abstract class TConstructorArgumentType implements ArgumentType{
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			if(ar.peekChar()=='<'){
				String substr = STRING_IN_ANGLE_BRACKETS
									.readAndValidateFrom(ar, context);
				ArgumentReader subar = new ArgumentReader(substr, 
										ar.getSubcommandLibrary());
				return construct(subar.readArguments(args(), context), context);
			}else if(allowNothing()){
				return construct(null, context);
			}else{
				ar.syntaxError("Expected '<', got "+ar.peekChar());
				return null;
			}
		}
		public boolean allowNothing(){ return false; }
		public abstract List<Argument> args();
		public abstract Object construct(Map<String, Object> args, Context ctx);
	};
	
	////-----------------------------------------------------------------
	/// Dispatch argument type	
	public static abstract class TDispatchArgumentType implements ArgumentType {
		public Map<String, ArgumentType> subTypes;
		public TDispatchArgumentType(){
			subTypes = new HashMap<String, ArgumentType>();
		}
		public Object readDefault(ArgumentReader ar, Context context) 
				throws ArgumentException{
			ar.syntaxError("Unknown syntax");
			return null;
		}
		@Override
		public Object readAndValidateFrom(ArgumentReader ar, Context context)
				throws ArgumentException {
			int pos = ar.position();
			char c = ar.peekChar();
			if(subTypes.containsKey(String.valueOf(c))){
				ar.readChar();
				return subTypes.get(String.valueOf(c))
						.readAndValidateFrom(ar, context);
			}else{
				String name = IDENTIFIER.readAndValidateFrom(ar, context).toLowerCase();
				ArgumentType t = subTypes.get(name);
				if(t!=null){
					return t.readAndValidateFrom(ar, context);
				}else{
					ar.setPosition(pos);
					return readDefault(ar, context);
				}
			}
		}
	}
}
