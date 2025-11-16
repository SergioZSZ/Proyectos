package TS;

import java.util.LinkedList;

public class PILA_TABLAS {// IDEA PARA LAS DIFERENTES TABLAS , COMO MAXIMO VA A HABER 2 ACTIVAS A LA VEZ,
							// LA GLOBAL Y UNA LOCAL

	public LinkedList<TS> pila = new LinkedList<TS>();

	public PILA_TABLAS() {

	}
	public TS buscarTabla(String lexema) {
		for (int i = 0; i < pila.size(); i++) {
			if (pila.get(i).estaEnTS(lexema)) {
				return pila.get(i);
			}
		}
		return null;
	}

	public boolean esVacia() {
		return pila.isEmpty();
	}

	public void addTS(TS tabla) {
		pila.add(tabla);
	}

	public void addTSGlobal(String lexema) {
		pila.getFirst().añadir(lexema);
	}

	public TS popTS() {
		TS t = pila.getLast();
		pila.removeLast();
		return t;
	}

	public void destruirTS(TS tabla) {
		pila.remove(tabla);
	}

	public int size() {
		return pila.size();
	}

	public boolean esGlobal(TS t) {
		return t == pila.getFirst();
	}
	
	public void setActual(TS ts) {
		for (int i = 0; i < pila.size(); i++) {
			if (pila.get(i).equals(ts)) {
				pila.get(i).actual = true;
			} else {
				pila.get(i).actual = false;
			}
		}
	}

	public TS getActual() {
		TS ts = null;
		for (int i = 0; i < pila.size(); i++) {
			if (pila.get(i).actual == true) {
				ts = pila.get(i);
				break;
			}
		}
		return ts;
	}

	public TS getGlobal() {
		return pila.getFirst();
	}

	public boolean buscarSiDeclarado(String lexema) {
		for (int i = 0; i < pila.size(); i++) {
			if (pila.get(i).estaEnTS(lexema)) {
				return true;
			}
		}
		return false;
	}

	public int getPos(String lexema) {
		int x = 0;
		for (int i = 0; i < pila.size(); i++) {
			if (pila.get(i).estaEnTS(lexema)) {
				x = pila.get(i).getPos(lexema);
			}
		}
		return x;
	}
}
