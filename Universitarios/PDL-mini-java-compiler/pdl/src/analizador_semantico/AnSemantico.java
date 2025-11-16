package analizador_semantico;

import java.util.LinkedList;
import java.util.Stack;

import TS.PILA_TABLAS;
import TS.TS;

public class AnSemantico {
	private String[] acciones = { "0.1", "0.2", "1","2", "3","4","5", "6","7","8","10","11", "12","13","14","16","17", "18", 
			"19","20", "21", "22", "23", "24","25","26","28", "30","31","32.1", "32.2","33","34","35","36","37","38.1","38.2","38.3",
			"38.4","39","40","41","42.1","42.2","43","44.1","44.2","46","47"};
	String sitioError;
	private int codError;
	private LinkedList<String> etiquetas_usadas;
	private TS tabla;
	private int params;
	private String tipoParam;
	private LinkedList<Integer> variables;
	private LinkedList<Integer> desplazamientos;
	public AnSemantico() {
		this.params=0;
		tipoParam="";
		variables = new LinkedList<Integer>();
		desplazamientos = new LinkedList<Integer>();
		etiquetas_usadas =new LinkedList<String>();
		codError=-1;
		sitioError="";
	}
	public int getCodError() {
		return codError;
	}
	public String getSitio() {
		return sitioError;
	}
	//GENERAR ETIQUETA QUE NO SE REPITA PARA LAS FUNCIONES
	public String genEtiq() {
		int etiq =(int)Math.floor(Math.random()*10) ;
		while(etiquetas_usadas.contains(etiq)) {
			etiq =(int)Math.floor(Math.random()*10) ;
		}
		etiquetas_usadas.add("Etiqueta_"+String.valueOf(etiq));
		return "Etiqueta_"+String.valueOf(etiq);
		
	}
//DEVUELVE SI LO QUE HAY EN LA PILA ES UNA ACCION SEMANTICA
	public boolean esAccionSemantica(String c) {
		for (int i = 0; i < acciones.length; i++) {
			if (acciones[i].equals(c)) {
				return true;
			}
		}
		return false;
	}
	//EJECUCION DE LAS ACCIONES SEMANTICAS
	public PilaAuxTS_tabla ejecutarAccion(String accion,Stack<Expresion> aux, PILA_TABLAS pts) {
		String etiq;
		TS tabla_a_destruir = null;
		
		switch (accion) {
		case "0.1": // { TSGlobal = Crear_TS(); DespG = 0; }
			 tabla = new TS("GLOBAL");
			tabla.actual=true;
			pts.addTS(tabla);
			
			break;

		case "0.2": // { destruirTS(TSGlobal); }
			
			tabla_a_destruir = pts.getGlobal();
			pts.destruirTS(pts.getGlobal());
			aux.pop();
			break;
		
		case "1":   
			aux.pop();
			aux.pop();
			break;
		
		case "2":   
			aux.pop();
			aux.pop();
			break;
			
		case "3":	//CASO TODOS LOS LAMBDAS pop 3,15,29,45
			aux.pop();
			break;
			 	
			
		case "4":
			
			
			if(aux.get(aux.size()-1).tipo!="no_ok") {
				aux.get(aux.size()-3).tipo = aux.get(aux.size()-2).tipo;
			}
			else {
				aux.get(aux.size()-3).tipo = "logico";
			}
			 if(aux.get(aux.size()-1).tipo=="no_ok" && aux.get(aux.size()-2).tipo!="entero" ) {
				
					//ERROR EL OPERADOR NO ES VALIDO
				codError=1;
				break;
			}
			 if(aux.get(aux.size()-2).tipo.equals("funct")) {
				 aux.get(aux.size()-3).tipoParam=aux.get(aux.size()-2).tipoParam;
					aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-2).tipoRet;
			 }
			
			aux.pop();
			aux.pop();
			
			break;
			
		case"5":

			if(aux.get(aux.size()-1).tipo!="no_ok") {
				aux.get(aux.size()-4).tipo = aux.get(aux.size()-2).tipo;
			}
		
			else if(aux.get(aux.size()-2).tipo!="logico"  || aux.get(aux.size()-1).tipo!="logico" ) {
				codError=2;
				//ERROR NECESARIOS OPERANDOS LOGICOS
				break;
			}

			aux.pop();
			aux.pop();
			aux.pop();
			
			break;
			
			
		case "6": //TODOS LOS CASOS LAMBDA vacio 6,9,12,27,47,43
			
			aux.get(aux.size()-1).tipo = "vacio";
	
			break;
			
		case "7":	
		
			
			if(aux.get(aux.size()-1).tipo=="logico" && aux.get(aux.size()-2).tipo=="entero") {
				aux.get(aux.size()-3).tipo=aux.get(aux.size()-1).tipo;
			}
			else if(aux.get(aux.size()-1).tipo!="no_ok") {
				aux.get(aux.size()-3).tipo=aux.get(aux.size()-2).tipo;
			}
			else if(aux.get(aux.size()-1).tipo!="entero" || aux.get(aux.size()-2).tipo!="entero") {
				codError=3; //ERRROR ERROR LOS TIPOS DEBEN SER ENTEROS
				break;
			}
			if(aux.get(aux.size()-2).tipo.equals("funct")) {
				aux.get(aux.size()-3).tipoParam=aux.get(aux.size()-2).tipoParam;
				aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-2).tipoRet;
			}

			aux.pop();
			aux.pop();
			break;
			
		case "8":// VIENDO
			
			if(aux.get(aux.size()-2).tipo =="entero" && aux.get(aux.size()-1).tipo !="no_ok") {
				aux.get(aux.size()-4).tipo = "logico";
			}
			else {	codError=3; break;}		//ERROR TIPOS ENTEROS

			aux.pop();
			aux.pop();
			aux.pop();
			break;
		

		case "10":

			if(aux.get(aux.size()-1).tipo!="no_ok") {
				aux.get(aux.size()-3).tipo=aux.get(aux.size()-2).tipo;
				if(aux.get(aux.size()-2).tipo.equals("funct")) {
					aux.get(aux.size()-3).tipoParam=aux.get(aux.size()-2).tipoParam;
					aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-2).tipoRet;
				}
			}
			else if(aux.get(aux.size()-1).tipo!="entero" || aux.get(aux.size()-2).tipo!="entero") {
				codError=3; //ERRORERROR, DEBEN SER ENTEROS
				break;
			}

			aux.pop();
			aux.pop();
			
			break;
			
		case "11":
			

			if(aux.get(aux.size()-1).tipo!="entero" && aux.get(aux.size()-2).tipo!="entero") {
				codError=3;; //ERRORERROR, DEBEN SER ENTEROS
				
				break;
			}
			else {
				aux.get(aux.size()-4).tipo=aux.get(aux.size()-2).tipo;
			}

			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
		case "12": //{	Y.tipo = vacio;}	
			
			aux.get(aux.size()-1).tipo = "vacio";

			aux.pop();
			break;
			
		case "13": //	{	V.tipo = buscaTipoTS(id.pos); } 

		
			if(!pts.getActual().estaEnTS(aux.get(aux.size()-2).lexema)) {
			
			TS var = pts.buscarTabla(aux.get(aux.size()-2).lexema);
			if(var.buscarTipo(aux.get(aux.size()-2).pos).equals("funct")) {
				aux.get(aux.size()-3).tipo = var.getTipoRet(aux.get(aux.size()-2).pos);
			}
			else {
			aux.get(aux.size()-3).tipo = var.buscarTipo(aux.get(aux.size()-2).pos);
			}

			}
			
			else {
				aux.get(aux.size()-3).tipo = pts.getActual().buscarTipo(aux.get(aux.size()-2).pos);
			
			if(pts.getActual().buscarTipo(aux.get(aux.size()-2).pos).equals("funct")){
				
				aux.get(aux.size()-2).tipoParam=pts.getActual().buscarTparam(aux.get(aux.size()-2).pos);
				aux.get(aux.size()-2).tipoRet = pts.getActual().getTipoRet(aux.get(aux.size()-2).pos);
				
				
				if(aux.get(aux.size()-2).tipoParam==null && pts.getActual().buscarTparam(aux.get(aux.size()-2).pos)==null) {
					aux.get(aux.size()-3).tipoRet = pts.getActual().getTipoRet(aux.get(aux.size()-2).pos);
					
				}
				else if(aux.get(aux.size()-2).tipoParam.equals(aux.get(aux.size()-1).tipo)) {
				aux.get(aux.size()-3).tipoParam=pts.getActual().buscarTparam(aux.get(aux.size()-2).pos);
				aux.get(aux.size()-3).tipoRet = pts.getActual().getTipoRet(aux.get(aux.size()-2).pos);
			}
				else {
					codError=10;
					sitioError = aux.get(aux.size()-2).lexema;
					break;
				}
			}
			}
		
			aux.pop();
			aux.pop();
			
		
		break;
		
		case "14"://{	N.tipo = L.tipo;	}
			
			aux.get(aux.size()-4).tipo=aux.get(aux.size()-2).tipo;
			
	
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
			
		case "16"://{	V.tipo = E.tipo;	}
			
			aux.get(aux.size()-4).tipo=aux.get(aux.size()-2).tipo;

			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
		case "17": //{	V.tipo := entero;	T.tamaño := 1;	}
			aux.get(aux.size()-2).tipo = "entero";
			aux.get(aux.size()-2).tamaño=1;
			aux.pop();
		break;
		
		case "18": //{	V.tipo := cadena;	T.tamaño := 64;	}
			aux.get(aux.size()-2).tipo = "cadena";
			aux.get(aux.size()-2).tamaño=64;
			aux.pop();
		break;
		
		case "19":
			
			
	
			
			if(!pts.getActual().estaEnTS(aux.get(aux.size()-3).lexema)) {
				TS t = pts.buscarTabla(aux.get(aux.size()-3).lexema);
				
				if(t==null) {codError=4; break;//NO DECLARADO 
			}
				else {
					int p = t.getPos(aux.get(aux.size()-3).lexema);
					if(t.buscarTipo(aux.get(aux.size()-3).pos).equals("funct")) {
						aux.get(aux.size()-4).tipo =t.getTipoRet(aux.get(aux.size()-3).pos );
					}
					else {
						aux.get(aux.size()-4).tipo =t.buscarTipo(aux.get(aux.size()-3).pos );
					}
				}
			}
			else {
				
				TS t = pts.buscarTabla(aux.get(aux.size()-3).lexema);
				if(pts.getActual().buscarTipo(aux.get(aux.size()-3).pos).equals("funct")) {


				if(t.buscarTparam(aux.get(aux.size()-3).pos)==null && aux.get(aux.size()-2).tipoParam.equals("vacio")
						|| t.buscarTparam(aux.get(aux.size()-3).pos).equals(aux.get(aux.size()-2).tipoParam) ) {
					
					aux.get(aux.size()-4).tipo= t.getTipoRet(aux.get(aux.size()-3).pos);
				}
				else {
					codError=10; //no coinciden tipoparam
					sitioError=aux.get(aux.size()-3).lexema;
					break;
				}
				}
			
			aux.get(aux.size()-3).tipo=pts.getActual().buscarTipo(aux.get(aux.size()-3).pos);
			if(pts.getActual().buscarTipo(aux.get(aux.size()-3).pos).equals("funct")) {
				aux.get(aux.size()-3).tipoParam= aux.get(aux.size()-2).tipoParam;
				pts.getGlobal().añadirTipoParam(aux.get(aux.size()-3).pos, aux.get(aux.size()-3).tipoParam);
			}
			else if(aux.get(aux.size()-2).tipo.equals("funct")) {
				  if(aux.get(aux.size()-2).tipoRet==pts.getActual().buscarTipo(aux.get(aux.size()-3).pos)){
					  aux.get(aux.size()-3).tipo=aux.get(aux.size()-2).tipoRet;


				  }
				  
			}
			else if(aux.get(aux.size()-3).tipo==null || aux.get(aux.size()-3).tipo=="-") {
				codError=5; //ERRO\"ERROR NO ENCONTRADO TIPO
				sitioError =aux.get(aux.size()-3).lexema;
				break;
			}
			else if(aux.get(aux.size()-3).tipo == aux.get(aux.size()-2).tipo){
				aux.get(aux.size()-4).tipo =  aux.get(aux.size()-3).tipo;
			}
			else {
				codError=6; //ERRORERROR, NO COINCIDEN TIPOS
				sitioError =aux.get(aux.size()-3).lexema;
	
				break;
			}
			}
			
			aux.pop();
			aux.pop();
			aux.pop();
			
			break;
		
		case "20": //{	M.tipo = E.tipo;	}
			aux.get(aux.size()-3).tipo = aux.get(aux.size()-1).tipo;
			aux.get(aux.size()-3).tipoParam = aux.get(aux.size()-1).tipoParam;
			aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-1).tipoRet;
		
				
			
			aux.pop();
			aux.pop();
		break;
		
		case "21": //{	M.tipo = E.tipo;	}
			
			aux.get(aux.size()-3).tipo = aux.get(aux.size()-1).tipo;
			
			aux.pop();
			aux.pop();
		break;
		
		case "22": //{M.tipo = vacio;  M.tipoParam = L.tipoParam;	}
			
			aux.get(aux.size()-4).tipo = "vacio";
			aux.get(aux.size()-4).tipoParam = aux.get(aux.size()-2).tipo;
			
			aux.pop();
			aux.pop();
			aux.pop();
			
		break;
		
		case "23": //{	S.tipo = E.tipo;	}
			
			aux.get(aux.size()-4).tipo = aux.get(aux.size()-2).tipo;
			
			aux.pop();
			aux.pop();
			aux.pop();
		break;
		
		
		case "24":
			
			if(!pts.getActual().estaEnTS(aux.get(aux.size()-2).lexema)) {
				  TS t = pts.buscarTabla(aux.get(aux.size()-2).lexema);
				  aux.get(aux.size()-2).tipo=t.buscarTipo(aux.get(aux.size()-2).pos);
				}
			else {
			aux.get(aux.size()-2).tipo=pts.getActual().buscarTipo(aux.get(aux.size()-2).pos);
			}
			if(aux.get(aux.size()-2).tipo==null) {
				
				codError=5;; //ERROERROR NO ENCONTRADO TIPO
				sitioError=aux.get(aux.size()-2).lexema;
				break;
			}
			else {
				aux.get(aux.size()-4).tipo=aux.get(aux.size()-2).tipo;
			}
			
			aux.pop();
			aux.pop();
			aux.pop();
			break;
		
		case "25":	//S.tipoRet = X.tipo;	}
			
			aux.get(aux.size()-4).tipo = aux.get(aux.size()-2).tipo;
			aux.get(aux.size()-4).tipoRet = aux.get(aux.size()-2).tipo;
	
	
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
			
		case "26":///////////////////////////
			
			if(aux.get(aux.size()-2).tipo.equals("vacio")) {
				aux.get(aux.size()-3).tipo= aux.get(aux.size()-1).tipo;
			}
			else if(aux.get(aux.size()-1).tipo.equals("vacio")) {
				aux.get(aux.size()-3).tipo= aux.get(aux.size()-2).tipo;
			}
			else {
			aux.get(aux.size()-3).tipo= aux.get(aux.size()-2).tipo +","+aux.get(aux.size()-1).tipo; 
			}
			
			aux.pop();
			aux.pop();
			break;
			
		
		
		case "28":///////////////////////////
			
			if(aux.get(aux.size()-1).tipo!="vacio") {
			aux.get(aux.size()-4).tipo= aux.get(aux.size()-2).tipo +","+aux.get(aux.size()-1).tipo;  
			}
			else if (aux.get(aux.size()-1).tipo.equals("vacio")){
				aux.get(aux.size()-4).tipo= aux.get(aux.size()-2).tipo;
			}
			
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
		
		case "30": //{	X.tipo = E.tipo;}
			
			aux.get(aux.size()-2).tipo = aux.get(aux.size()-1).tipo;
			
			aux.pop();
			break;
			
		case "31": //if
			
			if(aux.get(aux.size()-3).tipo=="logico") {
				aux.get(aux.size()-6).tipo = aux.get(aux.size()-1).tipo;
			}
			else {
				
				codError=7; //ERROERROR EXPRESION NO LOGICA NECESARIO
				
				break;
			}
			
			
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
		case "32.1": //  {zonaDec=true;	}
			pts.getActual().setZonaDec(true);
			break;
			
		case "32.2": // {	insertTipoTS(id.pos, T.tipo);	insertDespTS(id.pos, desp);	desp = desp + T.tamaño);	zonaDec = false;	}
			
			pts.getActual().añadirTipo(aux.get(aux.size()-3).pos, aux.get(aux.size()-2).tipo);
			pts.getActual().añadirDesplazamiento(aux.get(aux.size()-3).pos, pts.getActual().desplazamiento);
			if(aux.get(aux.size()-5).letra.equals("B")) {
				aux.get(aux.size()-5).tipo= aux.get(aux.size()-2).tipo;
			}
			pts.getActual().desplazamiento = pts.getActual().desplazamiento+aux.get(aux.size()-2).tamaño;
			pts.getActual().setZonaDec(false);
	
			

			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();

			break;
		
		case "33": //{	B.tipo = S.tipo;	}

			if(aux.get(aux.size()-1).tipoRet!=null) {
				aux.get(aux.size()-2).tipoRet = aux.get(aux.size()-1).tipoRet;
				
			}
			aux.get(aux.size()-2).tipo = aux.get(aux.size()-1).tipo;


			aux.pop();
			break;
			
			
			
		case "34":

			if(pts.getActual().buscarTipo(aux.get(aux.size()-9).pos)=="entero" && aux.get(aux.size()-6).tipo=="logico" 
					&& pts.getActual().buscarTipo(aux.get(aux.size()-9).pos)=="entero"
				&& aux.get(aux.size()-3).tipo=="entero" && aux.get(aux.size()-8).tipo=="entero") {
				aux.get(aux.size()-12).tipo = aux.get(aux.size()-1).tipo;
			}
			else {
				codError=8;//ERROR ERROR LAS SENTENCIA TIENEN QUE SER  ENTERO Y LA EXPRESION BOOLEANA
				break;
			}
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
			
			
		case "35": //{	T.tipo = entero;	T.tamaño = 1;	}
			aux.get(aux.size()-2).tipo = "entero";
			aux.get(aux.size()-2).tamaño=1;
			aux.pop();
		break;
		
		case "36": //{	T.tipo = logico;  T.tamaño = 1;	}
			aux.get(aux.size()-2).tipo = "logico";
			aux.get(aux.size()-2).tamaño=1;
			aux.pop();
		break;
		
		case "37": //{	T.tipo = cadena;  T.tamaño = 64;	}
			aux.get(aux.size()-2).tipo = "cadena";
			aux.get(aux.size()-2).tamaño=64;
			aux.pop();
		break;
		
		case "38.1"://////////////////////////
			pts.getActual().setZonaDec(true);
			break;
			
		case "38.2"://////////////////////////

			etiq = genEtiq();
			pts.getActual().añadirEtiq(aux.get(aux.size()-1).pos, etiq);
			pts.getActual().añadirTipo(aux.get(aux.size()-1).pos, "funct");
			pts.getActual().setZonaDec(false);
			tabla = new TS(pts.getActual().getLexIndex(aux.get(aux.size()-1).pos));
			pts.addTS(tabla);
			pts.setActual(tabla);
			pts.getActual().setZonaDec(true);
			
			break;
		case "38.3"://////////////////////////
			pts.getActual().setZonaDec(false);
			pts.getGlobal().añadirNumParam(aux.get(aux.size()-5).pos, aux.get(aux.size()-2).nParam);
			pts.getGlobal().añadirTipoParam(aux.get(aux.size()-5).pos,aux.get(aux.size()-2).tipoParam);
			pts.getGlobal().añadirTipoRet(aux.get(aux.size()-5).pos, aux.get(aux.size()-4).tipo);
			params = 0;
			tipoParam="";

			break;
			
			
		case "38.4"://////////////////////////

			if(aux.get(aux.size()-1).tipoRet != aux.get(aux.size()-5).tipo && aux.get(aux.size()-1).tipo!=aux.get(aux.size()-5).tipo) {
				codError=9;//ERROR INCORRECTO RETORNO
				sitioError= aux.get(aux.size()-5).tipo;
				break;
			}
			
			String t="---";

			if(aux.get(aux.size()-1).tipoRet == aux.get(aux.size()-5).tipo) {
				t = aux.get(aux.size()-1).tipoRet;
			}
			else if(aux.get(aux.size()-1).tipo==aux.get(aux.size()-5).tipo) {
				t = aux.get(aux.size()-1).tipo;
			}
		
			
			tabla_a_destruir = pts.getActual();
			pts.destruirTS(pts.getActual());
			pts.setActual(pts.getGlobal());
			pts.getActual().añadirTipoRet(aux.get(aux.size()-6).pos, t);
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			
			break;
			
		
		case "39":	//{	Z.tipo = C.tipo; }

			if(aux.get(aux.size()-2).tipoRet != null) {
				aux.get(aux.size()-4).tipoRet = aux.get(aux.size()-2).tipoRet;
			}
			else {
				aux.get(aux.size()-4).tipoRet = "vacio";
			}
			aux.get(aux.size()-4).tipo = aux.get(aux.size()-2).tipo;
	
			aux.pop();
			aux.pop();
			aux.pop();
			break;


			
		case "40": //{	H.tipo = T.tipo; }
			
			aux.get(aux.size()-2).tipo = aux.get(aux.size()-1).tipo;

			aux.pop();
			break;
			
		case "41"://{	H.tipo = void;	}

			aux.get(aux.size()-2).tipo = "vacio";

			aux.pop();
			break;
			
		case "42.1": //{	zonaDec = true; InsertTipoTS(id.pos, T.tipo); InsertDespTS(id.pos, desp); desp = desp + T.tamaño; zonaDec = false;}
			pts.getActual().setZonaDec(true);
			break;
		
		case"42.2":

			pts.getActual().añadirTipo(aux.get(aux.size()-1).pos, aux.get(aux.size()-2).tipo);
			desplazamientos.add(pts.getActual().desplazamiento);
			variables.addLast((aux.get(aux.size()-1).pos));
			params++;
			if(tipoParam.equals("")) {
				tipoParam = aux.get(aux.size()-2).tipo;
			}
			else {
			tipoParam=tipoParam +","+ aux.get(aux.size()-2).tipo;
			}
			aux.get(aux.size()-3).nParam = params;
			aux.get(aux.size()-3).tipoParam = this.tipoParam;
			
			pts.getActual().desplazamiento = pts.getActual().desplazamiento+aux.get(aux.size()-2).tamaño;
			//INSERTAMOS DESPLAZAMIENTOS EN SUS SITIOS
			for (int i = 0; i < variables.size(); i++) {
				pts.getActual().añadirDesplazamiento(variables.get(i), desplazamientos.get(variables.size()-i-1));
			}
			//reseteamos variables y desplazamientos
			desplazamientos = new LinkedList<Integer>();
			variables = new LinkedList<Integer>();
			aux.pop();
			aux.pop();
		
			break;
			
		
		
		case"44.1"://{	zonaDec = true;
			pts.getActual().setZonaDec(true);
			break;
			
		case "44.2":// InsertTipoTS(id.pos, T.tipo); InsertDespTS(id.pos, desp); desp = desp + T.tamaño; zonaDec = false;}
			pts.getActual().añadirTipo(aux.get(aux.size()-1).pos, aux.get(aux.size()-2).tipo);
			desplazamientos.add(pts.getActual().desplazamiento);
			variables.addLast((aux.get(aux.size()-1).pos));
			params++;
			if(tipoParam.equals("")) {tipoParam=aux.get(aux.size()-2).tipo;					}
			else 					 {tipoParam=tipoParam +","+ aux.get(aux.size()-2).tipo;	}
			pts.getActual().desplazamiento = pts.getActual().desplazamiento+aux.get(aux.size()-2).tamaño;
			
			aux.pop();
			aux.pop();
			aux.pop();
			aux.pop();
			break;
			
			
		case"46":
		
			if(aux.get(aux.size()-2).tipoRet!=null){
				aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-2).tipoRet;
			}
			else if(aux.get(aux.size()-1).tipoRet!=null){
				
				aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-1).tipoRet;
			}
			
			
			if(aux.get(aux.size()-2).tipo!="no_ok" && aux.get(aux.size()-1).tipo!="no_ok") {
				aux.get(aux.size()-3).tipo = aux.get(aux.size()-2).tipo;
				if(aux.get(aux.size()-2).tipoRet!=null){
					aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-2).tipoRet;
				}
				else if(aux.get(aux.size()-1).tipoRet!=null){
					
					aux.get(aux.size()-3).tipoRet = aux.get(aux.size()-1).tipoRet;
				}
		
			
			}


			aux.pop();
			aux.pop();
			break;
			
			
			
			
			
	}	
		
		return new PilaAuxTS_tabla(aux,pts,tabla_a_destruir);
}
}

