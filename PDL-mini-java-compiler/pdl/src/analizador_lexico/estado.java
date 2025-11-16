package analizador_lexico;

public class estado {
    int estado_cod;
     char accion;
    char simbolo;
    int regla;
    int simbolos;
    String implica;
    public estado(int cod, char accion){
      this.estado_cod = cod;
      this.accion = accion; 
      }
    
    public estado(int regla, char simbolo, String implica) {
    	this.regla=regla;
    	this.simbolo=simbolo;
    	this.implica=implica;
    }
    
    public estado(int simbolos, String implica) {
    	this.simbolos=simbolos;
    	this.implica=implica;
    }
      
      
   
    public Boolean esFinal(){
        return estado_cod>=10 && estado_cod <=24;
      }
    
    public int getCodEstado(){
      return estado_cod;
    }
    
    public char getAccion(){
        return accion;
      }
    public char getsimbolo() {
    	return simbolo;
    }
    public String getImplica() {
    	return implica;
    }
    public int getRegla() {
    	return regla;
    }

}
