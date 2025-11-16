package analizador_semantico;

public class Expresion {
	String letra;
	public String lexema;
	String expresion;
	public String tipo;
	String tipoParam;
	String tipoRet;
	int nParam;
	public int pos;
	int tamaño;
	public Expresion(String l, String e) {
		this.letra = l;
		this.expresion= e;
		this.pos=0;
		this.tipo="no_ok";
		nParam = 0;
		lexema = "";
	}
	
	public String getLetra()	{	return letra;	}
	public String getExpresion()	{	return expresion;	}
	public void setTipo(String t) {	tipo = t;	}
	
}
