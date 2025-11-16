package gestor_errores;

import analizador_lexico.Tokens;
import analizador_lexico.estado;

public class gestor_error {

	public gestor_error() {

		
	}

	//GESTOR DE ERRORES ANALIZADOR LEXICO
	
	public Tokens error_lexico (int num_linea, estado estado, String lexema) {
		Tokens token = new Tokens();
			// HACER ERROR DE SI UNA LETRA NO LA ENTIENDE EL LENGUAJE?
			// HACER ERROR DE SI UNA VARIABLE NO PUEDE LLEVAR PRIMERO UN NUMERO(SI PRIMER CARACTER = DIGITO NO PUEDE HABER DESPUES LETRA
			
			if (64 >= estado.getCodEstado() && estado.getCodEstado()>=50) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
							+ "DEL LEXEMA  " + lexema + "  EN ESTE LENGUAJE, ESPERADO COMO LEXEMA * | *=");
				
				
				return token;  
				}
			else if(estado.getCodEstado()==19) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
						+ "DEL LEXEMA  " + lexema + "  EN ESTE LENGUAJE, DEBE TENER COMO MAXIMO 64 CARACTERES");
			}
			
			else if (79 >= estado.getCodEstado() && estado.getCodEstado()>=65) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
						+ "DEL LEXEMA  " + lexema + "  EN ESTE LENGUAJE, ESPERADO COMO LEXEMA /*" );
				
			}
			
			else if (94 >= estado.getCodEstado() && estado.getCodEstado()>=80) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
						+ "DEL LEXEMA  " + lexema + "  EN ESTE LENGUAJE, ESPERADO COMO LEXEMA &&"); ; 
			}
			
			else if (95 == estado.getCodEstado()) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
						+ "DEL LEXEMA EN ESTE LENGUAJE, LAS VARIABLES NO SE PUEDEN DECLARAR CON UN NUMERO AL PRINCIPO ") ;
				
				} 
			else if (100 == estado.getCodEstado()) {
				token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS  "
						+ "DEL LEXEMA  " + lexema + "  EN ESTE LENGUAJE, CARACTER NO RECONOCIDO POR EL LENGUAJE ") ;
				
					
				} 
			
			else {	token.token("ERROR", "ERROR LINEA " + num_linea + ", NO ESTA PERMITIDA LA SINTAXIS DEL LEXEMA" +  lexema + "EN ESTE LENGUAJE");
			
				
			
			}

		return token;
	}
	public Tokens error_lexico(int num_linea, estado estado, int num) {
		Tokens token = new Tokens();
		token.token("ERROR", " ERROR LINEA " + num_linea + ", "
					+ "EL NUMERO  " + num + "  ES MAYOR A 32767, NO PERMITIDO");
		return token;
			
		}
	
	public Tokens error_lexico(int num_linea, estado estado, String lexema, boolean zonaDec) {
		Tokens token = new Tokens();
		
		if (estado.getCodEstado()==22 && zonaDec) { 
			token.token("ERROR", " (ZONADEC=TRUE) ERROR LINEA " + num_linea + ", "
					+ "LA VARIABLE  " + lexema + "  YA HA SIDO DECLADA ANTERIORMENTE");
			
		}
		//ESTE ERROR DEJA DE TENER SENTIDO DEBIDO A LA DECLARACION IMPLICITA DE VARIABLES DEL LENGUAJE
	/*	else if(estado.getCodEstado()==22 && !zonaDec) {
			token.token("ERROR", " (ZONADEC=FALSE) ERROR LINEA " + num_linea + ", "
					+ "LA VARIABLE  " + lexema + "  NO HA SIDO DECLADA ANTERIORMENTE");
			
		}	*/
		return token;
	}
	
	//FIN GESTOR DE ERRORES ANALIZADOR LEXICO
	public String error_sintactico(String lexema, String lexema2, int num_linea, int tipo) {
		num_linea++;
		if(tipo==1) {
			
			return "ERROR LINEA " + num_linea + ", ERROR SINTACTICO,"
					+ " SINTAXIS ERRONEA PARA LLEGAR AL LEXEMA '" +  lexema2 + "'";
		}
		else if(tipo==2) {
		
			
			return "ERROR LINEA " + num_linea + ", ERROR SINTACTICO ,"
					+ " ESPERADO EL TERMINAL '" + lexema + "' EN VEZ DEL ENCONTRADO '"+ lexema2 + "'";
		}
		else if(tipo==3) {
				return "ERROR LINEA " + num_linea + ", ERROR SINTACTICO,"
					+ "EL ANALIZADOR NO LLEGA A LA CONFIGURACION FINAL '$', BUSCA " + lexema2;
		}
		else 
			return "ERROR INCALIFICADO";
		
	}
	
	
	public String error_semantico(int tipo, Tokens token,String sitio) {
		String error="";
		int linea = token.getLinea()+1;
		if(tipo==1) {
			error = "ERROR SEMANTICO LINEA " + linea + ", EL OPERADOR NO ES VALIDO PARA ESTA EXPRESION";
		}
		else if(tipo==2) {
			error = "ERROR SEMANTICO LINEA " + linea + ", LOS OPERANDOS DEBEN SER TIPO LOGICOS";
		}
		else if(tipo==3) {
			error = "ERROR SEMANTICO LINEA " + linea + ", LOS OPERANDOS DEBEN SER ENTEROS";
		}
		else if(tipo==4) {
			error = "ERROR SEMANTICO LINEA " + linea + ", " + sitio + " NO DECLARADO";
			}
		else if(tipo==5) {
			error = "ERROR SEMANTICO LINEA " + linea + ", NO ENCONTRADO TIPO DE " + sitio;
		}
		else if(tipo==6) {

			error = "ERROR SEMANTICO LINEA " + linea + ", NO COINCIDE EL TIPO INTENTADO ASIGNAR"
					+ " A " + sitio + " CON SU TIPO" ;
		}
		else if(tipo==7) {
			error = "ERROR SEMANTICO LINEA " + linea + ", LA CONDICION DEL IF TIENE QUE SER DE TIPO LOGICO" ;
		}
		else if(tipo==8) {
			error = "ERROR SEMANTICO LINEA " + linea + ", LAS SENTENCIAS DE UN BUCLE 'FOR' DEBEN SER DE TIPO ENTERO"
					+ " Y LA EXPRESION TIPO LOGICO";
		}
		else if(tipo==9) {
			error = "ERROR SEMANTICO LINEA " + linea + ", DECLARADO TIPO RETORNO DE LA FUNCION DISTINTO AL ESPERADO,"
					+ " ESPERADO TIPO RETORNO " + sitio;
		}
		else if(tipo==10) {
			error = "ERROR SEMANTICO LINEA " + linea + ",LOS PARAMETROS DE LA FUNCION "+ sitio +" NO COINCIDEN CON LOS DECLARADOS";
		}
		else if(tipo==11) {
			error = "ERROR SEMANTICO LINEA " + linea + ",LOS PARAMETROS DE LA FUNCION "+ sitio +" NO COINCIDEN CON LOS DECLARADOS";
		}
		
		
		
		return error;
	}
	
	}

