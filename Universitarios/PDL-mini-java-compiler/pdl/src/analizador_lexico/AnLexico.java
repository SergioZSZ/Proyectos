package analizador_lexico;

import java.util.ArrayList;
import java.util.LinkedList;
import TS.PILA_TABLAS;
import TS.TS;

import gestor_errores.gestor_error;

public class AnLexico {
	
	private String[] palabrasReservadas = new String[] {"boolean", "for", "function", "get", "if", "int", "let", 
			"put", "return", "string", "void"};
	private  String comilla="'";
	private  Automata AFD;
	gestor_error gest_error;	
	public LinkedList<LinkedList<Character>> texto;
	public LinkedList<Character> linea;
	public int nlinea;
	private int pos;
	
	 public AnLexico () {
		 AFD = new Automata();
		 gest_error = new gestor_error();
		 nlinea = 0;
		 texto = new LinkedList<LinkedList<Character>>();
		 linea = new LinkedList<Character>();
		 pos = 0;
	 }
	 //METE POR LINEAS EL TEXTO EN A PROCESAR EN LA LISTA TEXTO
	 public void setTextoLinea(String text) {
		 LinkedList<Character> linea = new LinkedList<Character>();
		 char[] t = text.toCharArray();
		 for (int i = 0; i < t.length; i++) {
			linea.addLast(t[i]);
		}
		 
		 texto.addLast(linea);
		 this.linea = texto.getFirst();
		 
	 }
	 //SI LA PALABRA ES MAYOR A 64 NO SE PERMITE EN EL PROGRAMA
	 public boolean permitePalabra(String lexema) {
		char[] pal = lexema.toCharArray();
		if(pal.length>64) {
			return false;
		}
		else {
			return true;
		}
	 }
	
	 //DEVUELVE EL CARACTER EN LA POSICION DEL TEXTO INDICADA
 	  public  char getNextChar(LinkedList<Character> cadena, int pos) { //para obtener el siguiente caracter del archivo		//a b c d
 		  if(pos < cadena.size()) {
 			
 			 return cadena.get(pos);
		  }
 		  else {
 			  int i = nlinea+1;
 			  while(i<texto.size()) {
 				  if(!texto.get(i).isEmpty()) {
 					  this.pos = 0;
 					  nlinea = i;
 					  linea = texto.get(i);
 					  linea.addFirst(' ');
 			
 					  return linea.getFirst();
 				  }
 				  i++;
 			  }	
 			  return '$';
 		  }
 		  
	  }
 	  
 	  private  boolean esLetra(char l) {
 		  int r = l;
 		  return(r>=65 && r<=90  || r>=97 && r<=122);
 	  }
 	  
 	  private boolean esDigito(char dig) {
 		 int n = dig;
 		  return(n>=48 && n<=57);
  	  }
		
	public  TokenTablas getToken( PILA_TABLAS tabla) {
		
		char letra = 0;
		estado estadoActual = Automata.getInicial();
        Tokens token = new Tokens();
        boolean esFinal= false;
        boolean error = false;
        boolean coment = false;
        String lexema = "";
        int num = 0;
        while(!esFinal && !error ){//se sigue el bucle mientras no sea un estado final o no haya errores
        	if(estadoActual.getCodEstado() >= 50  ) {
        		error = true;
        		token = gest_error.error_lexico(nlinea+1, estadoActual, lexema);
        		
        	}
        	 
        	else { 	
        		
	switch (estadoActual.getCodEstado()) {
	
    case 0: 
    	
    	letra = getNextChar(linea, pos);

    	if (letra=='$' ) { esFinal = true; token.type="$"; break;}	
    	else if (letra==' ') { ++pos; break;} //protocolo espacios al principio del texto
    	else if (letra=='	') {++pos;break;} //protocolo tabuladores
    	else if(letra == '/' && coment) {     //protocolo de acabar comentario
    		coment = false;
    		letra = getNextChar(linea, ++pos); 	
    		if (letra==' ') { ++pos; break;} //protocolo espacios al principio del texto
    		if (letra=='	') {++pos;break;}
    		if (letra=='$' ) { esFinal = true; token.token("$", 0); token.setLinea(nlinea); break;}
    	}
    		if(esDigito(letra)) {estadoActual = AFD.getNextEstado(0,letra);num = (int)letra-48;}
    		
    		else {
    		if(letra == '_'){ estadoActual = AFD.getNextEstado(0,'a');	}
    		else {	estadoActual = AFD.getNextEstado(0,letra); }//se cambia el estado actual por el siguiente estado
    		
    		lexema = String.valueOf(letra);} //se a�ade al lexema el simbolo
    	
    		break;
    	 
	case 1:
		letra = getNextChar(linea,++pos);
		if(letra!=comilla.charAt(0)) {
			estadoActual = AFD.getNextEstado(1, 'a');
			lexema = lexema + String.valueOf(letra);
		}
		else {
		estadoActual = AFD.getNextEstado(1,letra ); //se cambia el estado actual por el siguiente estado
     	lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
		}
     	break;
	
	case 2: 
		letra = getNextChar(linea,++pos);
			estadoActual = AFD.getNextEstado(2,letra ); //se cambia el estado actual por el siguiente estado
	     	lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
	     	
		
		break;
		
	case 3: 
		letra = getNextChar(linea,++pos);
		coment=true;
			estadoActual = AFD.getNextEstado(3,letra ); //se cambia el estado actual por el siguiente estado
		    lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
		

		break;
	
	case 4:
		letra = getNextChar(linea,++pos);
		if(letra!='*') {letra =' ';	}
		estadoActual = AFD.getNextEstado(4,letra ); //se cambia el estado actual por el siguiente estado
		lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
		 break;
		 
	case 5:
		letra = getNextChar(linea,++pos);
		if (letra == '/') {
			estadoActual = new estado(0,' ');
		
			lexema ="";
			
		}
		else {
		estadoActual = AFD.getNextEstado(5,letra ); //se cambia el estado actual por el siguiente estado
		lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
	
		}
		break;
		
	case 6: 
		letra = getNextChar(linea,++pos);
			
			estadoActual = AFD.getNextEstado(6,letra ); //se cambia el estado actual por el siguiente estado
			lexema = lexema + String.valueOf(letra); //se a�ade al lexema el simbolo
		
		
		break;
		 	
	case 7: 
		letra = getNextChar(linea,++pos);
		
		if(letra=='$' || letra ==' ' || letra =='	') {estadoActual = AFD.getNextEstado(7, '&'); }   
		else if(esLetra(letra)) {
			estadoActual = AFD.getNextEstado(7,letra ); //se cambia el estado actual por el siguiente estado
			lexema = lexema + letra; //se a�ade al lexema el simbolo
		}
		else if(letra=='_') {
			estadoActual = AFD.getNextEstado(7,letra );
			lexema = lexema+letra;
		}
		else if(esDigito(letra)) {
			num = (int)letra-48;
			estadoActual = AFD.getNextEstado(7,letra );
			lexema = lexema + String.valueOf(num);
		
		}
		
		else { estadoActual = AFD.getNextEstado(7,letra );
		}

		break;
		
	case 8:
		letra = getNextChar(linea,++pos);
		if(esDigito(letra)) {
			estadoActual = AFD.getNextEstado(8,letra ); //se cambia el estado actual por el siguiente estado
			num = num*10 + ((int)letra-48);
			
		}
		else if(letra=='$') {estadoActual = AFD.getNextEstado(8, '&'); }
		else {estadoActual = AFD.getNextEstado(8,letra );  }
		
		break;
		
	case 11: 
		token.token("mas", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
				
		
		 
		break;
		
	case 12: 
		token.token("puntoycoma", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		break;
		
	case 13: 
		token.token("coma", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;

		break;
		
	case 14:
		token.token("abrirParentesis", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;

		break;
		
	case 15: 
		token.token("cerrarParentesis", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		
		break;
		
	case 16: 
		token.token("abrirLlave", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;

		break;
		
	case 17: 
		token.token("cerrarLlave", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		
		break;
		
	case 18:
		token.token("mayorQ", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		
		break;
		
	case 19:
		if(!permitePalabra(lexema)) {
		token = gest_error.error_lexico(nlinea+1, estadoActual, lexema);
		}
		else {
		token.token("cadena", lexema);
		token.setLinea(nlinea);
		}
		esFinal=true;
		pos++;
		
		break;
		
	case 20:
		token.token("opMult", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		
		break;
		
	case 21: 
		token.token("and", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		
		break;
		
	case 22: 
		boolean esReservada=false;
		for(int i=0;i < palabrasReservadas.length;i++) {
			if(lexema.equals(palabrasReservadas[i])) {	token.token(lexema,""); token.setLinea(nlinea); esReservada=true;  }
			}
		 if(!esReservada) {
			 
		 if(tabla.getActual().esZonaDec()) {
			 
			 if(!tabla.getActual().estaEnTS(lexema)) {
				 tabla.getActual().añadir(lexema); 
				 int px = tabla.getActual().getPos(lexema);
				 token.token("ID",px,lexema);
				 token.setLinea(nlinea);
			 }
			 else {  
					 token = gest_error.error_lexico(nlinea+1, estadoActual, lexema, tabla.getActual().esZonaDec()); /*ERROR, YA DECLARADO ESTE LEXEMA*/ 	 
				 }
			 	}
		 	
		 else {//DECLARACION IMPLICITA DE VARIABLES, SE AÑADE VARIABLE COMO ENTERO
			 if(!tabla.getActual().estaEnTS(lexema)/* && tabla.getActual().esGlobal*/) { 
			
				 if(tabla.getActual().esGlobal || !tabla.buscarSiDeclarado(lexema)){
				 tabla.getGlobal().añadir(lexema);
				 int px = tabla.getGlobal().getPos(lexema);
				 tabla.getGlobal().añadirTipo(px, "entero");
				 tabla.getGlobal().añadirDesplazamiento(px, tabla.getGlobal().desplazamiento);
				 tabla.getGlobal().setDespl(1);	//DESPLAZAMIENTO DE ENTEROS = 1+
				 token.token("ID", px,lexema);
				 token.setLinea(nlinea);
				 }
				 
				 else {//DECLARADA EN LA GLOBAL O LA LOCAL
					 
					  	token.token("ID", tabla.getPos(lexema),lexema);//declarado como su tipo
					  	token.setLinea(nlinea);
				 }
			 }
			 else {
				 token.token("ID", tabla.getPos(lexema),lexema);//declarado como su tipo
				  	token.setLinea(nlinea);
			 }
		
			 
		 	}
		 }
		 esFinal=true;
			break;
				
	case 23:
		if(num<=32767) {
		token.token("constEnt",num);
		token.setLinea(nlinea);
		}
		else {
			 token = gest_error.error_lexico(nlinea+1, estadoActual, num);
		}
		esFinal=true;
		
		break;
		
	case 24: 
		token.token("igual", "");
		token.setLinea(nlinea);
		esFinal=true;
		pos++;
		 
		break;
		
	}
	
        }
        	}


        LinkedList<Character> lineaNueva = new LinkedList<Character>();
        while(pos<linea.size()) {
        	lineaNueva.addLast(linea.get(pos));
        	pos++;
        }

        linea = lineaNueva;
        pos = 0;
        TokenTablas res = new TokenTablas(token,tabla);

        return res;
	}

	
		

		
	}






