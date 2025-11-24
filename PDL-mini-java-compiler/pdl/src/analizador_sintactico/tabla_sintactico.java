package analizador_sintactico;

import analizador_lexico.Tokens;
import analizador_lexico.estado;

public class tabla_sintactico {

	private static estado estado_inicial = new estado(' ', " ");
	private static estado[][] matriz_est = new estado[26][26];

	public tabla_sintactico() {
		matriz();

	}

	public static estado getInicial() {
		return estado_inicial;
	}

	public estado getEstado(Tokens token, String letra) {
		int valorNumerico = 0;
		int valorNumericoLetra = 0;

		switch (token.getType()) {
		case "&&":
			valorNumerico = 0;
			break;
		case "(":
			valorNumerico = 1;
			break;
		case ")":
			valorNumerico = 2;
			break;
		case "*=":
			valorNumerico = 3;
			break;
		case "+":
			valorNumerico = 4;
			break;
		case ",":
			valorNumerico = 5;
			break;
		case ";":
			valorNumerico = 6;
			break;
		case "=":
			valorNumerico = 7;
			break;
		case ">":
			valorNumerico = 8;
			break;
		case "boolean":
			valorNumerico = 9;
			break;
		case "cadena":
			valorNumerico = 10;
			break;
		case "constEnt":
			valorNumerico = 11;
			break;
		case "for":
			valorNumerico = 12;
			break;
		case "function":
			valorNumerico = 13;
			break;
		case "get":
			valorNumerico = 14;
			break;
		case "ID":
			valorNumerico = 15;
			break;
		case "if":
			valorNumerico = 16;
			break;
		case "int":
			valorNumerico = 17;
			break;
		case "let":
			valorNumerico = 18;
			break;
		case "put":
			valorNumerico = 19;
			break;
		case "return":
			valorNumerico = 20;
			break;
		case "string":
			valorNumerico = 21;
			break;
		case "void":
			valorNumerico = 22;
			break;
		case "{":
			valorNumerico = 23;
			break;
		case "}":
			valorNumerico = 24;
			break;
		case "$":
			valorNumerico = 25;
			break;
		}
		switch (letra) {
		case "A":
			valorNumericoLetra = 0;
			break;
		case "B":
			valorNumericoLetra = 1;
			break;
		case "C":
			valorNumericoLetra = 2;
			break;
		case "E":
			valorNumericoLetra = 3;
			break;
		case "F":
			valorNumericoLetra = 4;
			break;
		case "H":
			valorNumericoLetra = 5;
			break;
		case "K":
			valorNumericoLetra = 6;
			break;
		case "L":
			valorNumericoLetra = 7;
			break;
		case "M":
			valorNumericoLetra = 8;
			break;
		case "N":
			valorNumericoLetra = 9;
			break;
		case "O":
			valorNumericoLetra = 10;
			break;
		case "P":
			valorNumericoLetra = 11;
			break;
		case "Q":
			valorNumericoLetra = 12;
			break;
		case "R":
			valorNumericoLetra = 13;
			break;
		case "S":
			valorNumericoLetra = 14;
			break;
		case "T":
			valorNumericoLetra = 15;
			break;
		case "U":
			valorNumericoLetra = 16;
			break;
		case "V":
			valorNumericoLetra = 17;
			break;
		case "W":
			valorNumericoLetra = 18;
			break;
		case "X":
			valorNumericoLetra = 19;
			break;
		case "Y":
			valorNumericoLetra = 20;
			break;
		case "Z":
			valorNumericoLetra = 21;
			break;
		case "D":
			valorNumericoLetra = 22;
			break;
		}
		// System.out.println(valorNumerico);
		// System.out.println(valorNumericoLetra);
		return matriz_est[valorNumerico][valorNumericoLetra];

	}

	public void matriz() {

		/* 0 && */

		matriz_est[0][9] = new estado(15, 'N',"lambda 6");
		matriz_est[0][10] = new estado(5, 'O', "&& R O 5");
		matriz_est[0][18] = new estado(9, 'W', "lambda 6");
		matriz_est[0][20] = new estado(12, 'Y', "lambda 6");

		/* 1 *( */
		matriz_est[1][3] = new estado(4, 'E', "R O 4");
		matriz_est[1][7] = new estado(26, 'L', "E Q 26");
		matriz_est[1][8] = new estado(22, 'M', "( L ) 22");
		matriz_est[1][9] = new estado(14, 'N', "( L ) 14");
		matriz_est[1][13] = new estado(7, 'R', "U W 7");
		matriz_est[1][16] = new estado(10, 'U', "V Y 10");
		matriz_est[1][17] = new estado(16, 'V', "( E ) 16");
		matriz_est[1][19] = new estado(30, 'X', "E 30");

		/* 2 ) */
		
		matriz_est[2][6] = new estado(45, 'K', "lambda 3");
		matriz_est[2][7] = new estado(27, 'L', "lambda 6");
		matriz_est[2][9] = new estado(15, 'N',"lambda 6");
		matriz_est[2][10] = new estado(6, 'O', "lambda 6");
		matriz_est[2][12] = new estado(29, 'Q', "lambda 6");
		matriz_est[2][18] = new estado(9, 'W', "lambda 6");
		matriz_est[2][20] = new estado(12, 'Y', "lambda 6");



		/* 3 *= */
		matriz_est[3][8] = new estado(21, 'M', "*= E 21");

		/* 4 + */
		matriz_est[4][9] = new estado(15, 'N',"lambda 6");
		matriz_est[4][20] = new estado(11, 'Y', "+ V Y 11");

		/* 5 , */
		matriz_est[5][6] = new estado(44, 'K', "44.1 , T ID K 44.2");
		matriz_est[5][9] = new estado(15, 'N',"lambda 6");
		matriz_est[5][10] = new estado(6, 'O', "lambda 6");
		matriz_est[5][12] = new estado(28, 'Q', ", E Q 28");
		matriz_est[5][18] = new estado(9, 'W', "lambda 6");
		matriz_est[5][20] = new estado(12, 'Y', "lambda 6");

		/* 6 ; */
		matriz_est[6][9] = new estado(15, 'N',"lambda 6");
		matriz_est[6][10] = new estado(6, 'O', "lambda 6");
		matriz_est[6][18] = new estado(9, 'W', "lambda 6");
		matriz_est[6][20] = new estado(12, 'Y', "lambda 6");
		/* 7 = */
		matriz_est[7][8] = new estado(20, 'M', "= E 20");

		/* 8 > */
		matriz_est[8][9] = new estado(15, 'N',"lambda 6");
		matriz_est[8][18] = new estado(8, 'W', "> U W 8");
		matriz_est[8][20] = new estado(12, 'Y', "lambda 6");

		/* 9 boolean */

		matriz_est[9][0] = new estado(42, 'Α', "42.1 T ID K 42.2");
		matriz_est[9][5] = new estado(40, 'H', "T 40");
		matriz_est[9][15] = new estado(36, 'T', "boolean 36");

		/* 10 cadena */
		matriz_est[10][3] = new estado(4, 'E', "R O 4");
		matriz_est[10][7] = new estado(26, 'L', "E Q 26");
		matriz_est[10][13] = new estado(7, 'R', "U W 7");
		matriz_est[10][16] = new estado(10, 'U', "V Y 10");
		matriz_est[10][17] = new estado(18, 'V', "cadena 18");
		matriz_est[10][19] = new estado(30, 'X', "E 30");

		/* 11 constEnt */
		matriz_est[11][3] = new estado(4, 'E', "R O 4");
		matriz_est[11][7] = new estado(26, 'L', "E Q 26");
		matriz_est[11][13] = new estado(7, 'R', "U W 7");
		matriz_est[11][16] = new estado(10, 'U', "V Y 10");
		matriz_est[11][17] = new estado(17, 'V', "constEnt 17");
		matriz_est[11][19] = new estado(30, 'X', "E 30");

		/* 12 for */
		matriz_est[12][1] = new estado(34, 'B', "for ( ID M ; E ; ID M ) Z 34");
		matriz_est[12][2] = new estado(46, 'C', "B C 46");
		matriz_est[12][9] = new estado(15, 'N',"lambda 6");
		matriz_est[12][10] = new estado(6, 'O', "lambda 6");
		matriz_est[12][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[12][18] = new estado(9, 'W', "lambda 6");
		matriz_est[12][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[12][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 13 function */
		matriz_est[13][4] = new estado(38, 'F', "function 38.1 ID 38.2 H ( A ) 38.3 Z 38.4");
		matriz_est[13][9] = new estado(15, 'N',"lambda 6");
		matriz_est[13][10] = new estado(6, 'O', "lambda 6");
		matriz_est[13][11] = new estado(2, 'P', "F P 1");// añadidas semanticas
		matriz_est[13][18] = new estado(9, 'W', "lambda 6");
		matriz_est[13][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[13][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 14 get */
		matriz_est[14][1] = new estado(33, 'B', "S 33");
		matriz_est[14][2] = new estado(46, 'C', "B C 46");
		matriz_est[14][9] = new estado(15, 'N',"lambda 6");
		matriz_est[14][10] = new estado(6, 'O', "lambda 6");
		matriz_est[14][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[14][14] = new estado(24, 'S', "get ID ; 24");
		matriz_est[14][18] = new estado(9, 'W', "lambda 6");
		matriz_est[14][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[14][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 15 id */
		matriz_est[15][1] = new estado(33, 'B', "S 33");
		matriz_est[15][2] = new estado(46, 'C', "B C 46");
		matriz_est[15][3] = new estado(4, 'E', "R O 4");
		matriz_est[15][7] = new estado(26, 'L', "E Q 26");
		matriz_est[15][9] = new estado(15, 'N',"lambda 6");
		matriz_est[15][10] = new estado(6, 'O', "lambda 6");
		matriz_est[15][11] = new estado(1, 'P', "B P 1");
		matriz_est[15][13] = new estado(7, 'R', "U W 7");
		matriz_est[15][14] = new estado(19, 'S', "ID M ; 19");
		matriz_est[15][16] = new estado(10, 'U', "V Y 10");
		matriz_est[15][17] = new estado(13, 'V', "ID N 13");
		matriz_est[15][18] = new estado(9, 'W', "lambda 6");
		matriz_est[15][19] = new estado(30, 'X', "E 30");
		matriz_est[15][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[15][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 16 if */
		matriz_est[16][1] = new estado(31, 'B', "if ( E ) S 31");
		matriz_est[16][2] = new estado(46, 'C', "B C 46");
		matriz_est[16][9] = new estado(15, 'N',"lambda 6");
		matriz_est[16][10] = new estado(6, 'O', "lambda 6");
		matriz_est[16][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[16][18] = new estado(9, 'W', "lambda 6");
		matriz_est[16][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[16][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 17 int */
		matriz_est[17][0] = new estado(42, 'A', "42.1 T ID K 42.2");
		matriz_est[17][5] = new estado(40, 'H', "T 40");
		matriz_est[17][15] = new estado(35, 'T', "int 35");

		/* 18 let */
		matriz_est[18][1] = new estado(32, 'B', "32.1 let ID T ; 32.2");
		matriz_est[18][2] = new estado(46, 'C', "B C 46");
		matriz_est[18][9] = new estado(15, 'N',"lambda 6");
		matriz_est[18][10] = new estado(6, 'O', "lambda 6");
		matriz_est[18][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[18][18] = new estado(9, 'W', "lambda 6");
		matriz_est[18][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[18][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 19 put */
		matriz_est[19][1] = new estado(33, 'B', "S 33");
		matriz_est[19][2] = new estado(46, 'C', "B C 46");
		matriz_est[19][9] = new estado(15, 'N',"lambda 6");
		matriz_est[19][10] = new estado(6, 'O', "lambda 6");
		matriz_est[19][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[19][14] = new estado(23, 'S', "put E ; 23");
		matriz_est[19][18] = new estado(9, 'W', "lambda 6");
		matriz_est[19][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[19][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 20 return */
		matriz_est[20][1] = new estado(33, 'B', "S 33");
		matriz_est[20][2] = new estado(46, 'C', "B C 46");
		matriz_est[20][9] = new estado(15, 'N',"lambda 6");
		matriz_est[20][10] = new estado(6, 'O', "lambda 6");
		matriz_est[20][11] = new estado(1, 'P', "B P 1");// añadidas semanticas
		matriz_est[20][14] = new estado(25, 'S', "return X ; 25");
		matriz_est[20][18] = new estado(9, 'W', "lambda 6");
		matriz_est[20][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[20][22] = new estado(0, 'D', "0.1 P 0.2");

		/* 21 string */
		matriz_est[21][0] = new estado(42, 'A', "42.1 T ID K 42.2");
		matriz_est[21][5] = new estado(40, 'H', "T 40");
		matriz_est[21][15] = new estado(37, 'T', "string 37");

		/* 22 void */	
		matriz_est[22][0] = new estado(43, 'A', "void 41");
		matriz_est[22][5] = new estado(41, 'H', "void 41");

		/* 23 { */
		matriz_est[23][21] = new estado(39, 'Z', "{ C } 39");

		/* 24 } */
		matriz_est[24][2] = new estado(47, 'C', "lambda 6");
		matriz_est[24][9] = new estado(15, 'N',"lambda 6");
		matriz_est[24][10] = new estado(6, 'O', "lambda 6");
		matriz_est[24][18] = new estado(9, 'W', "lambda 6");
		matriz_est[24][20] = new estado(12, 'Y', "lambda 6");

		/* 25 $ */
		matriz_est[25][9] = new estado(15, 'N',"lambda 6");
		matriz_est[25][10] = new estado(6, 'O', "lambda 6");
		matriz_est[25][11] = new estado(3, 'P', "lambda 3");
		matriz_est[25][18] = new estado(9, 'W', "lambda 6");
		matriz_est[25][20] = new estado(12, 'Y', "lambda 6");
		matriz_est[25][22] = new estado(0, 'D', "0.1 P 0.2");

	}
}