package analizador_lexico;

import TS.PILA_TABLAS;
import TS.TS;

public class TokenTablas {
	 public Tokens token;
	 public PILA_TABLAS tabla;
	public TokenTablas(Tokens t, PILA_TABLAS ts) {
		token = t;
		this.tabla = ts;
	}
	
}
