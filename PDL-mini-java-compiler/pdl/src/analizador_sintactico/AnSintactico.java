package analizador_sintactico;

import java.util.Stack;


import java.util.LinkedList;
import TS.PILA_TABLAS;
import analizador_lexico.AnLexico;
import analizador_lexico.TokenTablas;
import analizador_lexico.Tokens;
import analizador_semantico.AnSemantico;
import analizador_semantico.Expresion;
import analizador_semantico.PilaAuxTS_tabla;
import gestor_errores.gestor_error;


public class AnSintactico {
	
	private String []NoTerminales = {"D","P","B", "E", "O", "R", "W", "U","Y", "V", "N",
									 "L", "Q", "X", "S", "M", "T", "H", "A", "K", "F", "C", "Z"};
	private tabla_sintactico tabla ;
	public  PILA_TABLAS ts;
	public AnLexico al ;
	private AnSemantico as;
	public gestor_error g ;
	public Stack<Expresion> aux;
	PILA_TABLAS pts;
	//vueltas
	LinkedList<Tokens> lista_tokens;
	//LISTA DE TABLAS PARA AÑADIR AL FICHERO LECTOR DE TABLAS
	PILA_TABLAS lista_tablas;
	public boolean error;
	
	public AnSintactico(PILA_TABLAS ts) {
		tabla = new tabla_sintactico(); 
		this.pts = new PILA_TABLAS();
		this.ts = ts;
		al = new AnLexico();
		as = new AnSemantico();
		g = new gestor_error();
		aux = new Stack<Expresion>();
		
		lista_tokens = new LinkedList<Tokens>();
		lista_tablas = new PILA_TABLAS();
		error = false;
	}
	//METODOS PARA EL LECTOR
	public LinkedList<Tokens> getListaTokens(){
		return lista_tokens;
	}
	
	public PILA_TABLAS getTablas() {
		return lista_tablas;
	}
	
	//DEVUELVE SI ES NO TERMINAL EL CARACTER
	boolean esNoTerminal(String c) {
		boolean x = false;
		for(int i = 0; i < NoTerminales.length; i++) {
			if(c.equals(NoTerminales[i])) {
				
				x = true;
				break;
			}
		}
		return x;
	}
	
	public String[] separar(String implicacion) {//separa el texto en un arraylist (usado para las implicaciones)
		String[]parts = implicacion.split(" ");
		return parts;
	}
	
	
	public void MeterEnPila(Stack<Expresion> pila, String[ ]separar) {//mete en la pila la implicacion dada la vuelta para coger primero el sig Nterminal
		int n = separar.length -1 ;
		for(int i= 0; i < separar.length ;i++) {
			pila.push(new Expresion(separar[n],null));
			n--;
		}
	}
	
	public boolean M(LinkedList<Integer> parser,Stack<Expresion> pila, Tokens token, String c) {//da y mete en la pila la siguiente regla a usar
		if(tabla.getEstado(token, c)==null) {	return true;	}
		
		else {
			
			Expresion t = pila.pop();

			aux.add(t);
			parser.add(tabla.getEstado(token, c).getRegla()+1);

			
			if(tabla.getEstado(token, c).getImplica().equals("lambda 3")) {
				pila.push(new Expresion("3", ""));
			}
			else if(tabla.getEstado(token, c).getImplica().equals("lambda 6")) {
				pila.push(new Expresion("6", ""));
			}
			else {
				MeterEnPila(pila,separar(tabla.getEstado(token, c).getImplica()));
			}
		
			
			return false;
		}
		
	}
	
	public void equip(Stack<Expresion> pila, Tokens token) {//equip para los terminales
		Expresion t =pila.pop();

		if(t.getLetra().equals("ID")) {	t.pos= token.getValue(); }
		 t.lexema=token.getLex();
		aux.add(t);

		
	}
	public Tokens cambio(Tokens token) {
		Tokens t = new Tokens();
		switch(token.getType()) {
		case "opMult": t.token("*=", ""); t.setLinea(token.linea); return t;	
		case "igual": t.token("=", ""); t.setLinea(token.linea); return t;		
		case "coma": t.token(",", "");  t.setLinea(token.linea); return t;		
		case "puntoycoma": t.token(";", ""); t.setLinea(token.linea); return t;	
		case "abrirParentesis": t.token("(", ""); t.setLinea(token.linea); return t;	
		case "cerrarParentesis": t.token(")", ""); t.setLinea(token.linea); return t;	
		case "abrirLlave": t.token("{", ""); t.setLinea(token.linea); return t;	
		case "cerrarLlave": t.token("}", "");  t.setLinea(token.linea); return t;	
		case "mas": t.token("+", "");  t.setLinea(token.linea); return t;		
		case "and": t.token("&&", ""); t.setLinea(token.linea); return t;	
		case "mayorQ": t.token(">", ""); t.setLinea(token.linea); return t;	
		}
		return token;
	}


	//ALGORITMO DESCENDENTE POR TABLAS
	public String algoritmo() {
		Stack<Expresion> pila = new Stack<Expresion>();
		LinkedList<Integer> parser = new LinkedList<Integer>();
		pila.push(new Expresion("$",null));
		pila.push(new Expresion("D",null));
		boolean end = false;

		PilaAuxTS_tabla vuelta;
		Tokens dolar = new Tokens();
		dolar.token("$", "");
		TokenTablas tokentabla = al.getToken(ts);
		this.ts = tokentabla.tabla;
		Tokens token = tokentabla.token;
		lista_tokens.add(token);
		token = cambio(token);
//INICIA EL ALGORITMO
		
		while(!end) {

//VEMOS PARA SEMANTICO
			if(as.esAccionSemantica(pila.peek().getLetra())) {	 

				vuelta = as.ejecutarAccion(pila.peek().getLetra(), this.aux, this.pts);
				//-1 INDICA QUE NO HAY ERROR
				if(as.getCodError()!=-1) {
					error=true;
					
					return g.error_semantico(as.getCodError(),token,as.getSitio());
				}
				if (vuelta.tabla!=null) {
					lista_tablas.addTS(vuelta.tabla);
				}
				
				
				
			
				this.aux = vuelta.aux;
			    this.pts = vuelta.ts;
				pila.pop();	
				
			}
			
				else {
			
			if((!esNoTerminal(pila.peek().getLetra())) && !(token.getType().equals(pila.peek().getLetra()))) {
				error=true;
				 return g.error_sintactico(pila.peek().getLetra(), token.getType(), token.getLinea(), 2 ) ;}//*METIDO EN GESTOR* ERROR CADENA ERRONEA, ESPERADO OTRO TOKEN(SIMBOLO TERMINAL)
			
			else if(pila.peek().getLetra().equals("$") && token.getType()!="$") {
				error=true;	

				return  g.error_sintactico( pila.peek().getLetra(), token.getType(), token.getLinea(), 3 ) ;} //ERROR CADENA ERRONEA, EL ANALIZADOR NO LLEGA A LA CONFIGURACION FINAL
			else {
				if(esNoTerminal(pila.peek().getLetra())) {
					error = M(parser,pila, token, pila.peek().getLetra());
					
					if(error) { return g.error_sintactico( pila.peek().getLetra(), token.getType(), token.getLinea(), 1  );} //ERROR CADENA ERRONEA, NO HAY REGLA PARA pila.peek() CON SIG_TOKEN listatokens.get(n) *EN GESTOR*
					
				}
			
				
				else if(token.getType().equals(pila.peek().getLetra())) {
					
					if(token.getType().equals("$")) {	
						pila.pop();
						end=true;	
					}
					else {
						
						equip(pila, token);
						
						if(as.esAccionSemantica(pila.peek().getLetra())) {	

							vuelta = as.ejecutarAccion(pila.peek().getLetra(), this.aux, this.pts);
							//-1 INDICA QUE NO HAY ERROR
							if(as.getCodError()!=-1) {
								error=true;
								return g.error_semantico(as.getCodError(),token,as.getSitio());
							}
							if (vuelta.tabla!=null) {
								lista_tablas.addTS(vuelta.tabla);
							}
							
							
							
							
							this.aux = vuelta.aux;
						    this.pts = vuelta.ts;
							pila.pop();	
						
						}
						

						tokentabla = al.getToken(pts);//llamamos al lexer para siguiente token
						if(tokentabla.token.getType().equals("ERROR")) {error = true; 	return  tokentabla.token.getLex();
						
							}
						
						token = tokentabla.token;
						
						lista_tokens.add(token);
						token = cambio(token);
						this.ts = tokentabla.tabla;
						
					
			
					}
					
				
				}
			}
			
		}

		}

	
		String pars="";
		for(int i=0;i<parser.size();i++) {
			pars= pars + parser.get(i) + " ";
		}
		return pars;	
		
		
				}
	

	
	
}		