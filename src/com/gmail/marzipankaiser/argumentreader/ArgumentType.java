package com.gmail.marzipankaiser.argumentreader;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import com.gmail.marzipankaiser.argumentreader.ArgumentReader.ArgumentException;

public interface ArgumentType {
	public Object readAndValidateFrom(ArgumentReader ar) throws ArgumentException;
	
	
	////-----------------------------------------------------------------
	/// Numbers (in LOCALE-SPECIFIC Format)
	public static class TLocaleSpecificNumber implements ArgumentType{
		boolean integer;
		public TLocaleSpecificNumber(boolean integer){ 
			this.integer=integer; 
		}
		@Override
		public Number readAndValidateFrom(ArgumentReader ar) 
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
			return n;
		}
	};
	public static final TLocaleSpecificNumber LS_NUMBER 
		= new TLocaleSpecificNumber(false);
	public static final TLocaleSpecificNumber LS_INTEGER_NUMBER 
		= new TLocaleSpecificNumber(true);
	
	////-----------------------------------------------------------------
	/// Identifiers (i.e.: valid Java identifiers as Strings)
	public static class TIdentifier implements ArgumentType{
		@Override
		public String readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			StringBuilder res = new StringBuilder(); // to hold result
			char c = ar.readChar();
			if(!Character.isJavaIdentifierStart(c))
				ar.syntaxError("Identifier can't start with '"+c+"'.");
			do{
				res.append(c);
				c = ar.tryReadChar();
			}while(Character.isJavaIdentifierPart(c) && c!='\0');
			ar.back();
			return res.toString();
		}
	};
	public final static TIdentifier IDENTIFIER = new TIdentifier();
	
	////-----------------------------------------------------------------
	/// Booleans
	public static class TBoolean implements ArgumentType{
		@Override
		public Boolean readAndValidateFrom(ArgumentReader ar)
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
				name = IDENTIFIER.readAndValidateFrom(ar).toLowerCase();
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
	};
	public final static TBoolean BOOLEAN = new TBoolean();
	
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
		public List<Object> readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			if(skipWhitespace) ar.skipWhitespace();
			ar.expect(start, "at beginning of delimited list");
			List<Object> res = new ArrayList<Object>();
			do{
				if(skipWhitespace) ar.skipWhitespace();
				res.add(elementType.readAndValidateFrom(ar));
				if(skipWhitespace) ar.skipWhitespace();
			}while(ar.tryExpect(delimiter));
			ar.expect(end, "at end of delimited list");
			return res;
		}
	};
	
	////-----------------------------------------------------------------
	/// Digits
	public static class TDigit implements ArgumentType{
		int radix;
		public TDigit(int radix){
			assert radix<=Character.MAX_RADIX && radix>=Character.MIN_RADIX;
			this.radix=radix;
		}
		@Override
		public Integer readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			char d = ar.readChar();
			int r = Character.digit(d, radix);
			if(r==-1) 
				ar.syntaxError("Expected base "+radix+" digit, got '"+d+"'.");
			return r;
		}
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
		public Integer readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			int res=0;
			char c=ar.readChar();
			int d = Character.digit(c, radix);
			do{
				res*=radix; res+=d;
				c=ar.tryReadChar(); d=Character.digit(c, radix);
			}while(c!='\0' && d!=-1);
			return res;
		}
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
		public Integer readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			int radix=10;
			if(ar.peekChar()=='0'){
				ar.readChar();
				char f=ar.readChar();
				switch(f){
				case 'b': radix=2;
				case 'o': radix=8;
				case 'x': radix=16;
				default: radix=8; ar.back(); // TODO: Java-like 010 == 8 ?
				}
			}
			if(ar.tryExpect('\0')){ //TODO: choose character to give radix
				radix = DECIMAL_INTEGER.readAndValidateFrom(ar);
				if(radix>Character.MAX_RADIX || radix<Character.MIN_RADIX)
					ar.syntaxError("Invalid radix "+radix+" in radix specification.");
				ar.expect('\0', "after radix specification"); //TODO: choose char
			}
			return (new TFixedRadixInteger(radix)).readAndValidateFrom(ar);
		}
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
		public Integer readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			int res = super.readAndValidateFrom(ar);
			if(res>max)
				ar.syntaxError("Expected integer <="+max+", got "+res+".");
			if(res<min)
				ar.syntaxError("Expected integer >="+min+", got "+res+".");
			return res;
		}
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
		public Double readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
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
			}
			
			return res;
		}
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
		public Double readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			int radix=10;
			if(ar.peekChar()=='0'){
				ar.readChar();
				char f=ar.readChar();
				switch(f){
				case 'b': radix=2;
				case 'o': radix=8;
				case 'x': radix=16;
				default: radix=8; ar.back(); // TODO: Java-like 010 == 8 ?
				}
			}
			if(ar.tryExpect('\0')){ //TODO: choose character to give radix
				radix = DECIMAL_INTEGER.readAndValidateFrom(ar);
				if(radix>Character.MAX_RADIX || radix<Character.MIN_RADIX)
					ar.syntaxError("Invalid radix "+radix+" in radix specification.");
				ar.expect('\0', "after radix specification"); //TODO: choose char
			}
			return (new TFixedRadixFloat(radix)).readAndValidateFrom(ar);
		}
		
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
		public Double readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			double res = super.readAndValidateFrom(ar);
			if(res>max)
				ar.syntaxError("Expected float <="+max+", got "+res+".");
			if(res<min)
				ar.syntaxError("Expected float >="+min+", got "+res+".");
			return res;
		}
	};	
	
	////-----------------------------------------------------------------
	/// Enums
	public static class TEnum implements ArgumentType{
		Class<? extends Enum<?>> enumType;
		public <T extends Enum<T>> TEnum(Class<T> enumClass){
			this.enumType=enumClass;
		}
		
		@Override
		public Enum<?> readAndValidateFrom(ArgumentReader ar)
				throws ArgumentException {
			String name = IDENTIFIER.readAndValidateFrom(ar);
			try { // TODO: DIRTY HACK!!
				return (Enum<?>) enumType
									.getMethod("valueOf", String.class)
									.invoke(null, name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	};
	
	////-----------------------------------------------------------------
	/// Bytes
	public static class TByte implements ArgumentType{
		static final TIntegerInRange sub = new TIntegerInRange(0,255); 
		@Override
		public Byte readAndValidateFrom(ArgumentReader ar) throws ArgumentException{
			return sub.readAndValidateFrom(ar).byteValue();
		}
	};
	public static final TByte BYTE = new TByte();
}
