
package lector;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import TS.PILA_TABLAS;
import analizador_lexico.Tokens;
import analizador_sintactico.AnSintactico;


public class lector {	
    public static void main(String[] args) throws IOException {
    	 System.out.println(new File(".").getAbsolutePath());
        // ARCHIVO DEL CUAL SE REALIZA EL ANÁLISIS
        // Se asume que ejecutas el programa desde la raíz del proyecto
        // y que existe la carpeta "lector" con los txt dentro.
		File fd = new File("../../lector/entrada_programa.txt");        FileReader fr = new FileReader(fd);
        BufferedReader br = new BufferedReader(fr);

        // DIRECCIONES DE LOS ARCHIVOS DE SALIDA DESDE SRC
        File fichero_tokens   = new File("../../lector/tokens.txt");
		File fichero_parser   = new File("../../lector/parse.txt");
		File fichero_TS       = new File("../../lector/TS.txt");
		File fichero_errores  = new File("../../lector/errores.txt");

        FileWriter fw  = new FileWriter(fichero_tokens);
        FileWriter fw2 = new FileWriter(fichero_parser);
        FileWriter fw3 = new FileWriter(fichero_TS);
        FileWriter fw4 = new FileWriter(fichero_errores);

		
				
			System.out.println("*** INICIO LECTOR ***");
			System.out.println();
			
				String linea_read;
				PILA_TABLAS pts = new PILA_TABLAS();
				AnSintactico s = new AnSintactico(pts);
				
				//METEMOS EL TEXTO 
				while((linea_read=br.readLine())!=null) {
					s.al.setTextoLinea(linea_read);
				}
				
	String parser =	s.algoritmo();
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	if(s.error) {
		
		System.out.println("	ERROR DETECTADO : Generando fichero 'errores.txt'");
		System.out.println("	...\n	...\n	...");
		fw4.write(parser);
		fw4.close();
		
		System.out.println("	'errores.txt' generado");
	}
	else {
		fw4.close();
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	System.out.println("	GENERANDO FICHEROS: 'tokens.txt',  'parse.txt','TS.txt'");
	System.out.println("	...\n	...\n	...");
	LinkedList<Tokens> tokens;
	tokens = s.getListaTokens();
	
	int nl=0;
	while(tokens.getFirst().type!="$"){
			if(tokens.getFirst().getLinea()!=nl) { fw.write("\n"); }
			
			if(tokens.getFirst().type.equals("ID") || tokens.getFirst().type.equals("constEnt")) {
				fw.write("<" +tokens.getFirst().getType() + "," + tokens.getFirst().getValue() + ">\n");
			}
			else {
				fw.write("<"+tokens.getFirst().getType() + "," + tokens.getFirst().getLex() + ">\n");
			}
			nl= tokens.getFirst().getLinea();
			tokens.removeFirst();
	}
	
	fw.close();
	

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	fw2.write("descendente " + parser);
	fw2.close();
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	PILA_TABLAS t = s.getTablas();
		String tabla;
		int x=1;
		while(!t.esVacia()) {
			if(x==1) {
				
		fw3.write("CONTENIDO TS " + t.pila.getLast().nombre + " # " + x +":\n \n");
				
				tabla = t.pila.getLast().volcarTS();
				fw3.write(tabla);
			
		t.pila.removeLast();
		
		fw3.write("-------------------------------------------------------------------------- \n");
		x++;
		}
		else {
	
			fw3.write("CONTENIDO TS " + t.pila.getFirst().nombre + " # " + x +":\n \n");
			
			tabla = t.pila.getFirst().volcarTS();
			fw3.write(tabla);
		
	t.pila.removeFirst();
	fw3.write("-------------------------------------------------------------------------- \n");
	x++;
		}
		
	}
		fw3.close();
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		System.out.println("	FICHEROS GENERADOS: 'tokens.txt', 'parse.txt', 'TS.txt'");
		
	}
	System.out.println();
	System.out.println("***  FIN LECTOR   ***");
	br.close();
	fr.close();
	}
	
	
}
	
 

