package analizador_semantico;

import java.util.Stack;

import TS.PILA_TABLAS;
import TS.TS;
//ESTRUCTURA PAR AQUE EL SINTACTICO Y EL SEMANTICO SE PASEN LA PILA, LA PILA AUX, LA PILA DE TS Y LA TABLA
//QUE VAYA A ELIMINAR PARA AÑADIRLA AL LECTOR TABLA DE SIMBOLOS
public class PilaAuxTS_tabla{
	public Stack<Expresion> aux;
	public PILA_TABLAS ts;
	public TS tabla;
	public boolean error_semantico;
	public PilaAuxTS_tabla(Stack<Expresion> aux,PILA_TABLAS ts, TS tabla) {
		
		this.aux=aux;
		this.ts=ts;
		this.tabla = tabla;
		error_semantico = false;
	}
}