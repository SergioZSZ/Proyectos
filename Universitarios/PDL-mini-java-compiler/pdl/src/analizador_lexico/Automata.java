package analizador_lexico;

public class Automata {
	
	 private String[] t = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","�","o","p","q","r","s","t","u","v",
			  "w","x","y","z","A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "�","O", "P",
			  	"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

	private String[]d = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	private static estado estado_inicial = new estado(0, ' ');
    public static estado [][] matriz_est = new estado[20][20];
    
  
    	public Automata() {
    	this.estado_inicial=estado_inicial;
    	matriz();

    	}
    	
    	 private static  boolean esLetra(char l) {
    		  int r = l;
     		  return(r>=65 && r<=90  || r>=97 && r<=122);
    	  }
    	  
    	  private static boolean esDigito(char dig) {
    		 int n = dig;
    		  return(n>=48 && n<=57);
     	  }
    	  
    	  public static estado getInicial(){
              return estado_inicial;
          }
    	  
      		
    	public static int cambio(String simbolo) {
    		int num = 0;
    		
    		if(esLetra(simbolo.charAt(0))) {num=6;}
    		else if(esDigito(simbolo.charAt(0))) {num=7;}
    		else {
    		switch(simbolo) {
    		case "'": num = 1; break;		case "*": num = 2;	break;	case "=": num = 3;	break;	case "/": num = 4;break;
    		case "&": num = 5;	break;			case "+": num = 8;break;
    		case ";": num = 9;break;		case ",": num = 10;	break; case "(": num = 11;break;	case ")": num = 12;break;
    		case "{": num = 13;break;	case "}": num = 14;break;	case ">": num = 15;break;	case " ": num = 16;break;
    		case "_": num = 17; break;
    		default: num = 0;break;
    		
    		} 
    		}
    		
    		return num;
    	}

    
    	public static estado getNextEstado(int estadoActual, char l) { //c
    		int c;
    	c = cambio(String.valueOf(l));
    	
    	if (c!=0) { return matriz_est[estadoActual][c] ;	}
    	else {return matriz_est[0][0];	}
    	
    	}
    	
    	public void matriz() {

    		matriz_est[0][0] = new estado(100,' ') ;
			/*  '	*/
			
			matriz_est[0][1] = new estado(1,'B') ;
			matriz_est[1][1] = new estado(19,'C') ;
			matriz_est[2][1] = new estado(50,' ') ; // = ERROR
			matriz_est[3][1] = new estado(65,' ') ; // = ERROR
			matriz_est[4][1] = new estado(4,'H') ;
			matriz_est[5][1] = new estado(4,'K') ;
			matriz_est[6][1] = new estado(80,' ') ; 
			matriz_est[7][1] = new estado(22,'�') ;
			matriz_est[8][1] = new estado(23,'a') ; //a = AA
			
			/*  *   */
			matriz_est[0][2] = new estado(2,'D') ;
            matriz_est[1][2] = new estado(1,'W') ;
            matriz_est[2][2] = new estado(51,' ') ; //e = ERROR
            matriz_est[3][2] = new estado(4,'G') ;
            matriz_est[4][2] = new estado(5,'I') ;
            matriz_est[5][2] = new estado(5,'J') ;
            matriz_est[6][2] = new estado(81,' ') ; //e = ERROR
            matriz_est[7][2] = new estado(22,'�') ;
            matriz_est[8][2] = new estado(23,'a') ; //a = AA
			
			/*  =   */
			matriz_est[0][3] = new estado(24,'b') ; //b = BB
            matriz_est[1][3] = new estado(1,'W') ;
            matriz_est[2][3] = new estado(20,'E') ;
            matriz_est[3][3] = new estado(66,' ') ; //e = ERROR
            matriz_est[4][3] = new estado(4,'H') ;
            matriz_est[5][3] = new estado(4,'K') ;
            matriz_est[6][3] = new estado(82,' ') ; //e = ERROR
            matriz_est[7][3] = new estado(22,'�') ;
            matriz_est[8][3] = new estado(23,'a') ; //a = AA
			
			/*  /   */
			matriz_est[0][4] = new estado(3,'F') ;
            matriz_est[1][4] = new estado(1,'W') ;
            matriz_est[2][4] = new estado(52,' ') ;
            matriz_est[3][4] = new estado(67,' ') ;
            matriz_est[4][4] = new estado(4,'H') ;
            matriz_est[5][4] = new estado(0,'c') ; //c = CC
            matriz_est[6][4] = new estado(83,' ') ; //e = ERROR
            matriz_est[7][4] = new estado(22,'�') ;
            matriz_est[8][4] = new estado(23,'a') ; //a = AA
			
			/*  &   */
			matriz_est[0][5] = new estado(6,'L') ;
            matriz_est[1][5] = new estado(1,'W') ;
            matriz_est[2][5] = new estado(53,' ') ;//e = ERROR
            matriz_est[3][5] = new estado(68,' ') ;//e = ERROR
            matriz_est[4][5] = new estado(4,'H') ;
            matriz_est[5][5] = new estado(4,'K') ;
            matriz_est[6][5] = new estado(21,'M') ;
            matriz_est[7][5] = new estado(22,'�') ;
            matriz_est[8][5] = new estado(23,'a') ; //a = AA
			
			/*  t   */
			matriz_est[0][6] = new estado(7,'N') ;
            matriz_est[1][6] = new estado(1,'W') ;
            matriz_est[2][6] = new estado(54,' ') ; //e = ERROR
            matriz_est[3][6] = new estado(69,' ') ; //e = ERROR
            matriz_est[4][6] = new estado(4,'H') ;
            matriz_est[5][6] = new estado(4,'K') ;
            matriz_est[6][6] = new estado(84,' ') ; //e = ERROR
            matriz_est[7][6] = new estado(7,'X') ;
            matriz_est[8][6] = new estado(95,'a') ; //a = AA
			
			
			/*  d   */
			matriz_est[0][7] = new estado(8,'Y') ;
            matriz_est[1][7] = new estado(1,'W') ;
            matriz_est[2][7] = new estado(55,' ') ; //e = ERROR
            matriz_est[3][7] = new estado(70,' ') ; //e = ERROR
            matriz_est[4][7] = new estado(4,'H') ;
            matriz_est[5][7] = new estado(4,'K') ;
            matriz_est[6][7] = new estado(85,' ') ; //e = ERROR
            matriz_est[7][7] = new estado(7,'X') ;
            matriz_est[8][7] = new estado(8,'Z') ;
			
			/*  +  */
			matriz_est[0][8] = new estado(11,'O') ;
            matriz_est[1][8] = new estado(1,'W') ;
            matriz_est[2][8] = new estado(56,' ') ; //e = ERROR
            matriz_est[3][8] = new estado(71,' ') ; //e = ERROR
            matriz_est[4][8] = new estado(4,'H') ;
            matriz_est[5][8] = new estado(4,'K') ;
            matriz_est[6][8] = new estado(86,' ') ; //e = ERROR
            matriz_est[7][8] = new estado(22,'�') ;
            matriz_est[8][8] = new estado(23,'a') ; //a = AA
			
			/*  ;   */
			matriz_est[0][9] = new estado(12,'P') ;
            matriz_est[1][9] = new estado(1,'W') ;
            matriz_est[2][9] = new estado(57,' ') ; //e = ERROR
            matriz_est[3][9] = new estado(72,' ') ; //e = ERROR
            matriz_est[4][9] = new estado(4,'H') ;
            matriz_est[5][9] = new estado(4,'K') ;
            matriz_est[6][9] = new estado(87,' ') ; //e = ERROR
            matriz_est[7][9] = new estado(22,'�') ;
            matriz_est[8][9] = new estado(23,'a') ; //a = AA
			
			/*  ,	*/
			
			matriz_est[0][10] =new estado(13, 'Q' );
			matriz_est[1][10] = new estado(1, 'W' );
			matriz_est[2][10] = new estado(58, ' ' );
			matriz_est[3][10] = new estado(73, ' ' );
			matriz_est[4][10] = new estado(4, 'H' );
			matriz_est[5][10] = new estado(4, 'K' );
			matriz_est[6][10] = new estado(88, ' ' );
			matriz_est[7][10] = new estado(22, '�' );
			matriz_est[8][10] = new estado(23, 'a' ); //a = AA
			
			/*  (	*/
			
			matriz_est[0][11] = new estado(14, 'R') ;
			matriz_est[1][11] = new estado(1, 'W') ;
			matriz_est[2][11] = new estado(59, ' ');
			matriz_est[3][11] = new estado(74 , ' ');
			matriz_est[4][11] = new estado(4 , 'H');
			matriz_est[5][11] = new estado( 4 , 'K');
			matriz_est[6][11] = new estado( 89 , ' ');
			matriz_est[7][11] = new estado(22 , '�');
			matriz_est[8][11] = new estado(23 , 'a'); //a = AA 
			
			/*  )	*/
			
			matriz_est[0][12] = new estado( 15, 'S') ;
			matriz_est[1][12] = new estado(1 , 'W');
			matriz_est[2][12] = new estado( 60 , ' ');
			matriz_est[3][12] = new estado( 75 , ' ');
			matriz_est[4][12] = new estado(4 , 'H');
			matriz_est[5][12] = new estado(4 , 'K');
			matriz_est[6][12] = new estado(90 , ' ');
			matriz_est[7][12] = new estado(22 , '�');
			matriz_est[8][12] = new estado(23 , 'a'); //a = AA
			
			/*  {	*/
			
			matriz_est[0][13] = new estado(16, 'T') ;
			matriz_est[1][13] = new estado(1, 'W') ;
			matriz_est[2][13] = new estado(61, ' ');
			matriz_est[3][13] = new estado(76 , ' ');
			matriz_est[4][13] = new estado(4 , 'H');
			matriz_est[5][13] = new estado(4 , 'K');
			matriz_est[6][13] = new estado(91 , ' ');
			matriz_est[7][13] = new estado(22 , '�');
			matriz_est[8][13] = new estado(23 , 'a'); //a = AA
			
			
			/*  }	*/
			
			matriz_est[0][14] = new estado(17, 'U') ;
			matriz_est[1][14] = new estado(1 , 'W');
			matriz_est[2][14] = new estado(62 , ' ');
			matriz_est[3][14] = new estado(77 , ' ');
			matriz_est[4][14] = new estado(4 , 'H');
			matriz_est[5][14] = new estado(4 , 'K');
			matriz_est[6][14] = new estado(92 , ' ');
			matriz_est[7][14] = new estado(22 , '�');
			matriz_est[8][14] = new estado(23 , 'a'); //a = AA
			
			/*  >	*/
			
			matriz_est[0][15] = new estado(18, 'V') ;
			matriz_est[1][15] = new estado(1 , 'W');
			matriz_est[2][15] = new estado(63 , ' ');
			matriz_est[3][15] = new estado(78 , ' ');
			matriz_est[4][15] = new estado(4 , 'H');
			matriz_est[5][15] = new estado(4 , 'K');
			matriz_est[6][15] = new estado(93 , ' ');
			matriz_est[7][15] = new estado(22 , '�');
			matriz_est[8][15] = new estado(23 , 'a'); //a = AA
			
			/*  del	*/
			
			matriz_est[0][16] = new estado(0, 'A') ;
			matriz_est[1][16] = new estado(1 , 'W');
			matriz_est[2][16] = new estado(64 , ' ');
			matriz_est[3][16] = new estado(79 , ' ');
			matriz_est[4][16] = new estado(4 , 'H');
			matriz_est[5][16] = new estado(4 , 'K');
			matriz_est[6][16] = new estado(94 , ' ');
			matriz_est[7][16] = new estado(22 , '�');
			matriz_est[8][16] = new estado(23 , 'a'); //a = AA
			
			/* _ */
			  matriz_est[7][17] = new estado(7,'X') ;
    	}
    		
}


    		
  
    	
    

