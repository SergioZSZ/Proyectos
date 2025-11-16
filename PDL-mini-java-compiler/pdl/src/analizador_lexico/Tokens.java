package analizador_lexico;

public class Tokens{

	public String type;
	public int value; //aka attribute
	public String lexema;
	public int linea;
	
	public Tokens() {
		
	}
	
	///////////////////////////////////////////////TOKEN DECLARATION/////////////////////////////////////
	//this is for when we want to make a lex a token
	
	public void token(String name, String lex) { 

		this.lexema = lex;
		type = name;	
	}

	
	//this is for tokens with fixed value
	public void token(String name, int value){

		type = name;
		this.value = value;	
	}
	
	public void token(String name, int value,String lex){

		type = name;
		this.value = value;	
		this.lexema = lex;
	}


	////////////////////////////////////////////GET FUNCTIONS//////////////////////////////////////
	public String getType() {

		return type; //is this right?
	}


	public String getLex() { 

		return this.lexema;
	}


	public int getValue() {
 
		return this.value;
	}
	
	public int getLinea() {
		return this.linea;
	}

	/////////////////////////////////////////SET FUNCTIONS//////////////////////////////////

	public void changeType(String name){

		type = name;
	}

	public void setValue(int value){

		this.value = value;
	}
	
	public void setLinea(int v) {
		this.linea=v;
	}

	///////////////////////////////////////TO-STRING FUNCTION//////////////////////////////
	@Override
	public String toString() {
	
	String type = this.type;

	if (this.lexema !=null){
	
		return "<" + type + ", " + this.lexema + ">";	
	
	}
	
	switch(type){
		
		case "constEnt" :
			return "<" + type + ", " + this.value + ">";

		default : 
			return "<" + type + ",  " + ">";

		}	
	}
	

}








