/*-
 * main.c
 * Minishell C source
 * Shows how to use "obtain_order" input interface function.
 *
 * Copyright (c) 1993-2002-2019, Francisco Rosales <frosal@fi.upm.es>
 * Todos los derechos reservados.
 *
 * Publicado bajo Licencia de Proyecto Educativo Práctico
 * <http://laurel.datsi.fi.upm.es/~ssoo/LICENCIA/LPEP>
 *
 * Queda prohibida la difusión total o parcial por cualquier
 * medio del material entregado al alumno para la realización
 * de este proyecto o de cualquier material derivado de este,
 * incluyendo la solución particular que desarrolle el alumno.
 *
 * DO NOT MODIFY ANYTHING OVER THIS LINE
 * THIS FILE IS TO BE MODIFIED
 */

//BIBLIOTECAS
#include <stddef.h>			/* NULL */
#include <stdio.h>			/* setbuf, printf */
#include <stdlib.h>			/*exit*/
#include <unistd.h>      	/*getpid,getppid*/
#include <sys/wait.h>		/*wait,waitpid*/
#include <signal.h>			/*señales*/
#include <sys/types.h>		/*tipo pid_t*/
#include <fcntl.h> 			/*ficheros*/
#include <string.h>			/*strcmp,strings en genreal*/
#include <sys/stat.h>		/*umask*/
#include <time.h>			/*clock_t, para mandato time*/
#include <ctype.h> 		/*isalnum, ver si alfanumerico en $*/


extern int obtain_order();		/* See parser.y for description */

//integración de funciones auxiliares
int redir(char *fd_name, int mode);
void cierrePipes(int pipefds[][2],int argvc);
void mandato_cd(char **argv);
void mandato_umask(char **argv);
void mandato_time(clock_t start, clock_t stop, time_t inic, time_t term);
char **metacar_$(char ***argvv, char**argv);
void mandato_read(char **argv, int secuencial, char *buffer) ;

//GLOBALES
pid_t pid;
int status;
int bgpid;
/************************************************************************************************************/
//main
int main(void)
{

//guardamos entrada/salidas default (herencia hijos)
int or_stdin = dup(STDIN_FILENO);
int or_stdout = dup(STDOUT_FILENO);
int or_stderr = dup(STDERR_FILENO);


//IGNORAR SEÑALES
	struct sigaction sa;
	sa.sa_handler=SIG_IGN;
	sigaction(SIGINT,&sa,NULL);
	sigaction(SIGQUIT,&sa,NULL);
	sigaction(SIGBUS,&sa,NULL);
	sigaction(SIGSEGV,&sa,NULL);


	//VARIABLES
	char ***argvv = NULL;
	int argvc;
	char **argv = NULL;
	int argc;
	char *filev[3] = { NULL, NULL, NULL };
	int bg;
	int ret;

	

	setbuf(stdout, NULL);			/* Unbuffered */
	setbuf(stdin, NULL);

	while (1) {
		
		fprintf(stderr, "%s", "msh> ");	/* Prompt */
		ret = obtain_order(&argvv, filev, &bg);  
		if (ret == 0) break;		/* EOF */ 
		if (ret == -1) continue;	/* Syntax error */ 
		argvc = ret - 1;		/* Line */	
		if (argvc == 0) continue;	/* Empty line */ 	

argv = argvv[0];
int hayTime=0;
int soloTime=0;
clock_t start, stop;
time_t 	inic,term;

//casos redirecciones para que hereden los hijos y metodos
	int fd = -2;
	if(filev[0])		{	fd = redir(filev[0],0);	}
	if(filev[1])	{	fd = redir(filev[1],1);	}
	if(filev[2]) 	{	fd = redir(filev[2],2);	}
//si se abre el fichero que realice el resto, si no de vuelta al prompt
	if(fd!=-1){	

//mandato time empieza el reloj a contar real y de user
if(strcmp(argv[0],"time")==0){
	hayTime=1; 
	time(&inic);	
	start = clock(); 
//si no hay argumento despues del time solo se ejecuta el tiempo del shell
	if(!argv[1]){;
		soloTime=1;
		mandato_time(start,stop,inic,term);
///si hubo redir volvemos a los estandar
		if(fd!=-2){
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);
		}
	}
}
//si no es solo time que siga el codigo
if(soloTime==0){			
//crear pipes para unir procesos si hay argvc(1 menos que mandatos)
	int pipefds[argvc - 1][2];
	for (int i = 0; i < argvc - 1; i++) {
    	    if (pipe(pipefds[i]) == -1) {
        	    perror("***Error al generar pipes generales***\n");
            	exit(1);
        	}
    	}

	
	if(hayTime==1 && argv++){} //puntero asiguiente de time
	
//sustitucion metacaracteres $ si hay 
	argv = metacar_$(argvv, argv);
	if(argv==NULL){//error en el metodo
		status=1;
	}
	else{
//mandato read
if(strcmp(argv[0],"read")==0){
	mandato_read(argv,0,NULL);
	if(hayTime==1)	{	mandato_time(start,stop,inic,term);	}
	///si hubo redir volvemos a los estandar
		if(fd!=-2){
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);
		}
	}
//mandato cd
else if(strcmp(argv[0],"cd")==0){
		mandato_cd(argv);
		if(hayTime==1)	{	mandato_time(start,stop,inic,term);	}
///si hubo redir volvemos a los estandar
		if(fd!=-2){
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);
		}
		
	}

//mandato umask
	else if(strcmp(argv[0],"umask")==0){ 
		mandato_umask(argv);
		if(hayTime==1)	{	mandato_time(start,stop,inic,term);	}
///si hubo redir volvemos a los estandar
		if(fd!=-2){
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);
		}
	

	}

	else{
		int piperead[2];
		pipe(piperead);
		int cont = 0;

//for para ejecucion de todos los mandatos
	for (int i = 0; (argv = argvv[i]); i++) {
		cont++;
		argv = argvv[i];
		if(hayTime==1 && argv++){}	//si hay time que el puntero argv apunte a la sig

//fork para creacion de hijos
	
		switch(pid=fork()){
		case -1:
		perror("***Error al intentar crear un proceso hijo***\n");
		exit(1);
	
//caso hijo 
		case 0:
		sa.sa_handler=SIG_DFL; ;
		sigaction(SIGBUS,&sa,NULL);
		sigaction(SIGSEGV,&sa,NULL);
//si no esta en background pueden ser interrumpidos por las señales SIGINT y SIGQUIT
		if(!bg){
			sa.sa_handler=SIG_DFL; 
			sigaction(SIGINT,&sa,NULL);
			sigaction(SIGQUIT,&sa,NULL);
		}
//entrada = salida anterior pipe si no es el primero
		if (i > 0) {
                if (dup2(pipefds[(i-1)][0], 0) == -1) {
                	perror("***Error al duplicar el pipe reader a la salida del anterior proceso***\n");
                    exit(1);
                }
            }

// salida = entrada sig pipe si no es el ultimo
            if (i < argvc - 1) {
				if(strcmp(argvv[i+1][0],"read")==0){//caso sig arg read para pasarle la entrada ant
					if(dup2(piperead[1],1)==-1){
                		perror("***Error al duplicar el pipe writter a la entrada del siguiente proceso***\n");
						exit(1);
					}
				}
				else{
                if (dup2(pipefds[i][1], 1) == -1) {
                	perror("***Error al duplicar el pipe writter a la entrada del siguiente proceso***\n");
                    exit(1);
                }
				}
            }

// Cerrar pipes hijo
			cierrePipes(pipefds,argvc);
			close(piperead[0]);
			close(piperead[1]);
//ejecucion de mandato
	if(strcmp(argv[0],"read")!=0){
		execvp(argv[0],argv);
		perror("***Error al intentar ejecutar el mandato escrito***\n");
		exit(1);
	}
	else{exit(0);}
	
	
//proceso padre
		default:
		//protocolo si bg o no
		if(!argvv[i+1]){
			if(bg){	 bgpid = pid;	printf("[%d]\n",bgpid);	}
		}	
	}//fin switch  
}//fin for recorrido argumentos pipe
//cerrar pipes padre y vuelta a standar
		cierrePipes(pipefds,argvc);

		

//protocolo status mandatos en primer plano(espera del ultimo)
	if(!bg){
			waitpid(pid,&status,NULL);

			//si read ultimo argumento(siendo el final de secuencia)
			if(strcmp(argvv[cont-1][0],"read")==0 && cont>1){
				char *buffer = (char *)malloc(1024);
				read(piperead[0],buffer,1024);
				close(piperead[1]);
				close(piperead[0]);
				mandato_read(argvv[cont-1],1,buffer);
				free(buffer);
			}
			else{
				close(piperead[1]);
				close(piperead[0]);
				}
			//mandato time si se ha ejecutado		
			if(hayTime==1){ 
				mandato_time(start,stop,inic,term);
			}
			//redir estandar
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);

	/*		BORRAR ESTE COMENTARIO SI EL CORRECTO LO DA BIEN, SI NO LO DA BIEN VOLVERLO A PONER

			if(WIFEXITED(status)){
				if(WEXITSTATUS(status)!=0){
					printf("EL PROCESO %d TERMINO ERRONEAMENTE DEVOLVIENDO %d\n",pid,WEXITSTATUS(status));
			}
			else if(WIFSIGNALED(status)>0){
				printf("EL PROCESO %d MURIO POR LA SEÑAL %d\n",pid, WTERMSIG(status));
				}
			}	*/
		}
	else{	//redir standar igual
		dup2(or_stdin, 0);	
		dup2(or_stdout, 1); 
		dup2(or_stderr, 2);
		}
	}//si no es mandato hecho propio
}//si la variable $ existia
}//si es solo time
}//si se pudo abrir fichero
}//fin while
	exit(0);
	return 0;

}
/************************************************************************************************************/

//FUNCIONES AUXILIARES

//Redirecciona la entrada/salida/salidaERROR dependiendo del modo a un fichero
int redir(char *name_fd, int mode){
	int fd;
	switch(mode){
		case 0:fd = open(name_fd,O_RDONLY);
			    if (fd == -1) {	perror("***Error al intentar abrir el fichero***\n");	return fd;	}						
				dup2(fd,0);	break;		//entrada solo lectura
		case 1:fd = open(name_fd,O_WRONLY|O_CREAT|O_TRUNC, 0666);	
				if (fd == -1) {	perror("***Error al intentar abrir/crear el fichero***\n");	return fd;	}
				dup2(fd,1);	break;		//salidas solo escritura
		case 2:fd = open(name_fd,O_WRONLY|O_CREAT|O_TRUNC, 0666);
				if (fd == -1) {	perror("***Error al intentar abrir/crear el fichero***\n");	return fd;	}
				dup2(fd,2);	break;
	}
	close(fd);
}
/************************************************************************************************************/
//cierra los pipes del array de pipes
void cierrePipes(int pipefds[][2],int argvc){
			for (int j = 0; j < argvc - 1; j++) {
                close(pipefds[j][0]);
				close(pipefds[j][1]);
            }
}
/********************************************************************************************************/
//realiza el mandato cd
void mandato_cd(char **argv){
	char *dir;
//casos cd sin argumentos o ~ o ~/
	if(!argv[1]){
		dir = getenv("HOME");
	}
//casos cd directorios
	else{
		dir = argv[1];
	}
//chdir para ir al nuevo dir + protocolo de error
		if(chdir(dir)!=0){
			perror("***Error, el directorio indicado no existe***\n");
			status=1;
		}
		else{
//tam max permitido para rutas y ruta
			int size_ruta = pathconf(".", _PC_PATH_MAX);
			char *ruta;
//mem dinamica para la ruta
			ruta = (char *)malloc(size_ruta);
    		if (ruta == NULL) {
				perror("***Error al reservar mem din de la ruta***\n");
				status=1;
			}
		else{
//getcwd para sacar por salida estandar + protocolo error
				getcwd(ruta,size_ruta);
				if(ruta!=NULL){		printf("%s\n",ruta);	}
				else		  {		perror("***Error al sacar por pantalla la ruta***\n"); status=1; }

				free(ruta);
			}
		}
}
/********************************************************************************************************/
//realiza el mandato umask
void mandato_umask(char **argv){
//si no hay argumentos da la mascara actual
	if(!argv[1]){
		mode_t aux = umask(0);	//hacemos umask para obtener el valor de la mascara
		printf("%o\n",aux);		//imprimimos
		umask(aux);				//devolvemos el valor perdido de la mascara
	}
//si hay 
	else{
		if(argv[2]){
			perror("***Error, umask solo acepta 1 argumento siendo este el valor de la mascara nueva***\n");
			status=1;
		}
		else{
//pasamos a num la mascara en octal, la cambiamos e imprimimos la anterior
			char *oct;
        	mode_t new_masc = strtol(argv[1], &oct, 8);	
			if (*oct != '\0'){
				perror("***Error, entrada octal no valida***\n");	
				status=1;
			}
			else{
			mode_t ant_masc = umask(new_masc);	
			printf("%o\n",ant_masc);	
			}
		}	
	}
}

/********************************************************************************************************/
//realiza el mandato time
void mandato_time(clock_t start, clock_t stop, time_t inic, time_t term){
	time(&term);
	stop = clock();
	double tiempo_user = (double)((stop-start)/CLOCKS_PER_SEC);	//lo da en tics por segundo, dividirlo entre la var esa
	double tiempo_real = (double)difftime(term,inic);		//los time con difftime
	double tiempo_system = tiempo_real-tiempo_user;		//tsistema = treal-tuser
	//sacamos parte entera y parte fraccion
	int ent_user = (int)tiempo_user;
	int ent_real = (int)tiempo_real;
	int ent_system = (int)tiempo_system;

	int fracc_user = (int)((tiempo_user-ent_user)*1000);
	int fracc_real = (int)((tiempo_real - ent_real)*1000);
	int frac_system =(int)((tiempo_system - ent_system)*1000);
	
	printf("%d.%03du %d.%03ds %d.%03dr\n", ent_user, fracc_user, 
											ent_system, frac_system, 
											ent_real, fracc_real);
}
/******************************************************************************************************* */
//sustitucion variables $ y caso ~
char **metacar_$(char ***argvv, char **argv) {
// inicializacion vars y var auxiliares para no modificar punteros
    char **argv_aux = argv;
    char ***argvv_aux = argvv;
    char *sustitucion=(char *)malloc(1024);
    int argc = 0;
	int argvc = 0;
	int error=0;
    //recorriendo los mandatos
    for (argvc = 0; (argv_aux = argvv_aux[argvc]); argvc++) {
    //recorriendo cada argumento del mandato
        for (argc = 0; argv_aux[argc]; argc++) {
	//caso argumento metacaracter tilde '~'
			if (strcmp(argv_aux[argc], "~") == 0) {
                char *home = getenv("HOME");
                if (home == NULL) {	
					perror("***Error al intentar sacar el valor de $HOME(Null)***\n");
					error=1;
				}
				else{
    //cambio ~ por  $HOME
                    argv_aux[argc] = strdup(home); 
					return argv;
                }
			}	
            char *new_arg = (char *)malloc(1024);
            if (new_arg == NULL) {
                perror("***Error al reservar mem din***\n");
                error=1;
            }
	//hay que asegurarse de que empieza vacío o dara error por la basura
			new_arg[0] = '\0';
            int i = 0;

	//recorremos argumento hasta fin linea
            while (argv_aux[argc][i] != '\0') {
	//si no encontramos $  añadimos al nuevo argumento
                if (argv_aux[argc][i] != '$') {
                    strncat(new_arg, &argv_aux[argc][i], 1);
                    i++;
                }
                else {
    //si encontramos $ buscamos la variable santando $
                    i++;  
                    char *var_name = (char *)malloc(1024);
					if (var_name == NULL) {
                		perror("***Error al reservar mem din***\n");
                		error=1;
            		}
					var_name[0]='\0'; //que empiece vacio y no con basura
                    int var_len = 0;
    //leer el nombre de la variable
                    while (isalnum(argv_aux[argc][i]) || argv_aux[argc][i] == '_') {
                        var_name[var_len++] = argv_aux[argc][i++];
                    }
			//vars especiales:
					int var_esp=0;
					char cad[20];
			//mypid
					if(strcmp(var_name,"mypid")==0){
						int mypid = getpid();
						sprintf(cad,"%d",mypid);
						sustitucion=cad;
						var_esp=1;
					}
			//status
					else if(strcmp(var_name,"status")==0){
						if(WIFEXITED(status)){
							sprintf(cad,"%d",WEXITSTATUS(status));
						}
						else if(WIFSIGNALED(status)){
							sprintf(cad,"%d",WIFSIGNALED(status));
						}
						sustitucion=cad;
						var_esp=1;
					}

			//bgpid
					else if(strcmp(var_name,"bgpid")==0){
						sprintf(cad,"%d",bgpid);
						sustitucion=cad;
						var_esp=1;
					}
					if(var_esp==1){
						strcat(new_arg, sustitucion);
					}
    //getenv para valor variable de entorno
                    
					else{
						sustitucion = getenv(var_name);
					
                    if (sustitucion != NULL) {
                        //copiar el valor de la variable al nuevo argumento
                        strcat(new_arg, sustitucion);
                    } 
					else {
						perror("***Error, la variable entorno escrita no existe***\n");
						error=1;	
                    }
					}
					free(var_name);	
					
                }
            }
			
            //sustituir el argumento original por el nuevo argumento con las sustituciones
            argv_aux[argc] = strdup(new_arg);
            //liberamos mem
			
			free(new_arg); 
        }
    }
	if(error==1){	return NULL;}
	//devolvemos puntero pero con sus dir de mem modificadas sutituyendo metacaracteres
    return argv;
}
/******************************************************************************************************* */
//mandato read
void mandato_read(char **argv, int secuencial, char *buffer) {

//inicializacion vars y var auxiliares para no modificar punteros
	char **argv_aux = argv;
	char *aux=(char *)malloc(1024);	
	char *valores= (char *)malloc(1024);
		
	//si es por secuencia valor del buffer, si no entrada por usuario
	if(secuencial==1){	valores = buffer;}		
	else			{	valores = fgets(aux,1024,stdin);	};
		if(!argv[1]){//no vars
		perror("***Error, se exige como minimo para  'read' una variable***\n");
		status=1;
		}
		else if(*valores=='\n'){//solo pulsar enter no hacer nada 
		}
		else if(!valores){
			perror("***Error al leer la entrada***\n");
			status=1;
			}
		
		
		else {
			
			//cambiamos el salto de linea(fgets)  por caracter fin de linea
			valores[strcspn(valores, "\n")] = '\0';
			char *token;
        	char **valores_array= (char **)malloc(1024); //para valores divididos
        	int num_valores = 0;
	//dividir por espacios y tabs los valores
        	token = strtok(valores, " \t");
       		 while (token!= NULL) {
				valores_array[num_valores] = token;	//guardamos por separado 
				num_valores++;
            	//dividimos e iteramos desde donde lo dejo 
				token = strtok(NULL, " \t");  
			}
			int argc = 0;
			int argvc = 1;
			char *ultimo_valor;
			//mientras haya valores y variables asigna,
			for (argvc; argv_aux[argvc]!= NULL && valores_array[argc]!=NULL; argvc++) {
				ultimo_valor = valores_array[argc];
            	if (argc < num_valores) {
                //asignamos val a var
              	  if (setenv(argv_aux[argvc],valores_array[argc],1)!= 0) {
                	    perror("***Error al intentar cargar la variable en el entorno con su valor***\n");
						status=1;
						}
					else{
               	 argc++;
					}	
       	 		}
			}
		char * res = (char *)malloc(1024);
		if (res == NULL) {
             perror("***Error al reservar mem din***\n");
		status=1;
		}

		res[0]='\0';
		strcat(res,ultimo_valor);
		//valores no asignados si hay a la ultima variable(se realiza si hay mas valores que variables)
		while(valores_array[argc]!=NULL){
			strcat(res," ");
			strcat(res,valores_array[argc]);
			argc++;
		}
		//si hay menos variables que valores el ultimo es la union del bucle anterior
		if(argvc-1 < argc){
			if (setenv(argv_aux[argvc-1],res,1)!= 0) {
            	perror("***Error al intentar cargar la variable entorno con su valor***\n");
                status=1;
				}
		}
		free(res);
		free(valores_array);
		free(aux);
    }
	}
