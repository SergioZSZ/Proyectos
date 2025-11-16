package TS;

import java.util.LinkedList;

public class TS {
	
	public LinkedList<ID> tabla;
	public String nombre;
	public int desplazamiento;
	private boolean zonaDec = false;
	public boolean esGlobal = false;
	public boolean actual = false;
	public TS(String nombre) {
		this.nombre=nombre;
		desplazamiento = 0;
		tabla  = new LinkedList<ID>();
	}
	//zona declarativa set y get
	public void setZonaDec(boolean zd) {	this.zonaDec=zd;			}
 	public boolean esZonaDec()		   {	return zonaDec; 	}
 	public int tamaño() {
 		return tabla.size();
 	}

 	public void printTS() {
 		System.out.println("TS: 	LEX	 TIPO	DESPL	NPARAM	TIPOPARAM   RETORN	PARAM	ETIQ	MODOP");
 		for (int i = 0; i < tabla.size(); i++) {
 			System.out.println(i + " 	" + tabla.get(i).getLexema() + "	" + tabla.get(i).getTipo()+"	"+tabla.get(i).getDespl()+
			"	"+tabla.get(i).getNumParam()+"	"+tabla.get(i).getTipoParam()+"	   "+ tabla.get(i).getTipoRet()+"	"+
 					tabla.get(i).getParam()+"	"+tabla.get(i).getEtiq()+"	"+tabla.get(i).getMParam());
		}
 	}
 	
 	public String volcarTS() {
 		String t = "";
 		for (int i = 0; i < tabla.size(); i++) {
 			t= t+ "* LEXEMA: '" + tabla.get(i).getLexema() + "' \n"
 				+	"Atributos: \n";
 			
 			if(tabla.get(i).getTipo()!="funct") {
 				t=t+ "+ Tipo: '" + tabla.get(i).getTipo() + "' \n"
 				+	 "+ Despl: " + tabla.get(i).getDespl()+" \n";
 			}
 			else {
 				t=t+ "+ Tipo: '" + tabla.get(i).getTipo() + "' \n"
 				+	 "+ NumParam: " + tabla.get(i).getNumParam()+" \n";
 				if(tabla.get(i).tipoParam!=null && tabla.get(i).tipoParam!="vacio") {
 					
 				
 				String[] tipoParams = tabla.get(i).getTipoParam().split(",");
 				for (int j = 0; j < tipoParams.length; j++) {
 					if(j<10) {
 						t=t+"+ TipoParam0"+ (j+1) + ": '" + tipoParams[j]+"' \n" ;
 					}
 					else {
 						t=t+"+ TipoParam"+ (j+1) + ": '" + tipoParams[j]+"' \n" ;
 					}
				}
 				}
 				t=t+ "+ TipoRetorno: '"+ tabla.get(i).getTipoRet()+"' \n"
 				+	 "+ EtiqFuncion: '"+ tabla.get(i).getEtiq()+"' \n" ;
 				
 			}
 			
 			if(i+1 < tabla.size()){
 				
			t=t+"--------- ---------- \n";
 			}

		}
 		return t;
 	}
	public boolean esVacia() {
		if (tabla.isEmpty()) { return true;}
		else 				 { return false;}
	}
	
	public String getTipoRet(int pos) {
		return tabla.get(pos).getTipoRet();
	}
	
	
	public boolean estaEnTS(String lexema) {
		boolean esta = false;
	
		if(esVacia()) {return esta	;}
		else {
		for(int i = 0; i < tabla.size(); i++) {
			if (tabla.get(i).getLexema().equals(lexema)){
				esta = true;
			}
		}
	}
		return esta;
	}
	
	public int getPos(String lexema) {
		int pos = 0;
		for (int i = 0; i < tabla.size(); i++) {
			if(tabla.get(i).getLexema().equals(lexema)) {
				return i;
			}
		}
		//System.out.println("POSICION " + lexema + ": " + pos);
		return pos;
	}
	public void añadir(String lexema) {
		tabla.add(new ID(lexema));
	}
	
	public int size() {
		return tabla.size();
	}
	
	public String getLexIndex(int i) {
		return tabla.get(i).getLexema();
	}
	
	public String buscarTipo(int pos) {
		return tabla.get(pos).getTipo();
	}
	public String buscarTparam(int pos) {
		return tabla.get(pos).getTipoParam();
	}
	
	public void añadirTipo(int pos, String tipo) {
		tabla.get(pos).tipo(tipo);;
			}
	public void añadirParam(int pos, String param) {
		tabla.get(pos).param(param);
			}
	public void añadirDesplazamiento(int pos, int desplazamiento) {
		tabla.get(pos).despl(desplazamiento);
	}
	public void añadirNumParam(int pos, int num) {
		tabla.get(pos).numParam(num);
	}
	public void añadirTipoParam(int pos, String tipo) {
		tabla.get(pos).tipoParam(tipo);
	}
	public void añadirModoPararm(int pos, String modo) {
		tabla.get(pos).mparam(modo);
	}
	public void añadirTipoRet(int pos, String tipo) {
		tabla.get(pos).tipoRet(tipo);
	}
	public void añadirEtiq(int pos, String etiq) {
		tabla.get(pos).etiq(etiq);
	}
	public void setDespl(int x) {
		desplazamiento = desplazamiento + x;
	}
}
