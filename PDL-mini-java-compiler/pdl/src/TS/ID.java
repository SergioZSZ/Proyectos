package TS;

public class ID {

	private String lexema;
	private String tipo;
	public int pos;
	private int despl;
	public int numParam;
	public String tipoParam;
	private String TipoRetorno;
	private String Param;
	private String etiqFun;
	private String ModoParam;
	
	public ID(String lexema) {
		this.lexema=lexema;
		this.pos= 0;
		this.tipo="-";
		this.despl=0;
		this.numParam=0;
		this.tipoParam="-";
		this.TipoRetorno="-";
		this.Param="-";
		this.etiqFun="-";
		this.ModoParam="-";
	}
	
	//setters 
	public void tipo(String tipo)	 {	this.tipo=tipo;			}
	public void despl(int despl)	 	 {	this.despl=despl;		}
	public void numParam(int num) 	 {	this.numParam=num;		}
	public void tipoParam(String tipo){	this.tipoParam=tipo;	}
	public void tipoRet(String modo)  {	this.TipoRetorno=modo;	}
	public void param(String param)   {	this.Param=param;		}
	public void etiq(String etiq)	 {	this.etiqFun=etiq;		}
	public void mparam(String param)	 {	this.ModoParam=param; 	}
	
	//getters
	public String getLexema() 	{	return lexema;		}
	public String getTipo() 	{ 	return tipo;		}
	public int	  getDespl() 	{ 	return despl;		}
	public int 	  getNumParam() { 	return numParam;	}
	public String getTipoParam(){ 	return tipoParam;	}
	public String getMParam()  	{ 	return ModoParam;	}
	public String getTipoRet() 	{ 	return TipoRetorno;	}
	public String getParam() 	{ 	return Param;		}
	public String getEtiq() 	{ 	return etiqFun;		}
}
