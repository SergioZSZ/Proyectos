#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/uio.h>
#include "worker.h"
#include "manager.h"
#include "common.h"
#include "common_cln.h"
#include <string.h>
#include <arpa/inet.h> //comprobaciones entre otras cosas
#include <sys/select.h>


//cierra todos los sockets
void closesockets(int* s, int nworkers){
    for(int i=0; i<nworkers;i++){   close(s[i]);    }
}

//manda un mensaje primero enviando su longitud para coger el nº de bytes exactos en el worker
int send_dato(int sock, const char *str) {
    int len = strlen(str) + 1;
    if(write(sock, &len, sizeof(int))<=0) {return 1;}  //primero long
    if(write(sock, str, len)<=0) {return 1;}           //luego string

    return 0;
}

//envia una tarea al worker
int envio_tarea(int sockets, char* program, char* input, char* output, int blocksize, int idblock, int numargs, char** args){
    //envio del ejecutable, fichero entrada y fichero salida, bloques que ocupa fichero e id_bloq
    
    if(send_dato(sockets,program)!=0){perror("ERROR ENVIO PROGRAM"); return 1 ;}
    if(send_dato(sockets,input)!=0){perror("ERROR ENVIO INPUT"); return 1 ;}
    if(send_dato(sockets,output)!=0){perror("ERROR ENVIO OUTPUT"); return 1 ;}
    if(write(sockets,&blocksize,sizeof(int))!=sizeof(int)){perror("ERROR ENVIO TAM_BLOQ");  return 1 ;}
    if(write(sockets,&idblock,sizeof(int))!=sizeof(int)){perror("ERROR ENVIO ID_BLOQ"); return 1 ;}
    if(write(sockets,&numargs,sizeof(int))!=sizeof(int)){perror("ERROR ENVIO Nº ARGS"); return 1 ;}

    for (int i= 0; i<numargs; i++) {
        if (send_dato(sockets, args[i]) != 0) { perror("ERROR ENVIO ARGS");  return 1;   }
    }

    return 0;
}





static int file_info(const char *f, int *is_reg_file, int *is_dir, int *is_readable, int *is_writable, int *is_executable, size_t *fsize) {
    struct stat st;
    if (stat(f, &st) == -1) return -1;
    if (is_reg_file) *is_reg_file = S_ISREG(st.st_mode);
    if (is_dir) *is_dir = S_ISDIR(st.st_mode);
    if (is_readable) *is_readable = access(f, R_OK) == 0;
    if (is_writable) *is_writable = access(f, W_OK) == 0;
    if (is_executable) *is_executable = access(f, X_OK) == 0;
    if (fsize) *fsize = st.st_size;
    return 0;
}

int main(int argc, char *argv[]) {
    if (argc<8) {
        fprintf(stderr, "Usage: %s manager_host manager_port num_workers blocksize input output program args...\n", argv[0]);
        return 1;
    }
    // recogida de argumentos
    int nworkers = atoi(argv[3]);
    int blocksize = atoi(argv[4]);
    char * input = argv[5];
    char * output = argv[6];
    char * program = argv[7];
    int numargs = argc - 8; //nº de args
    char **args = &argv[8]; //argumentos del programa
    // verificación de argumentos
    if (nworkers < 1) {
        fprintf(stderr, "número de workers inválido\n"); return 1;
    }
    if (blocksize < 1) {
        fprintf(stderr, "blocksize inválido\n"); return 1;
    }
    int res, is_dir, is_reg_file, is_readable, is_writable, is_executable;
    size_t input_size;
    res = file_info(input, &is_reg_file, NULL, &is_readable, NULL, NULL, &input_size);
    if ((res == -1) || !is_reg_file  || !is_readable) {
        fprintf(stderr, "fichero de entrada inválido\n"); return 1;
    }
    res = file_info(output, NULL, &is_dir, NULL, &is_writable, NULL, NULL);
    if ((res == -1) || !is_dir || !is_writable) {
        fprintf(stderr, "directorio de salida inválido\n"); return 1;
    }
    res = file_info(program, &is_reg_file, NULL, NULL, NULL, &is_executable, NULL);
    if ((res == -1) || !is_reg_file || !is_executable) {
        fprintf(stderr, "programa inválido\n"); return 1;
    }


    //conexion con el gestor
    int gestor_socker;
    if((gestor_socker = create_socket_cln_by_name(argv[1],argv[2]))< 0 ) { perror ("ERROR CONEXION CON EL GESTOR"); return 1;  }

    //reserva y envio al gestor
    int reserva[3];
    reserva[0]=htonl(getpid()); //id cliente
    reserva[1]=htonl(nworkers);//trabajadores a reservar
    reserva[2]=htonl(1);       //1 = cliente
    
    //printf("solicitud reserva: %i,%i\n",getpid(),nworkers);

    if(write(gestor_socker,reserva,sizeof(reserva))!=sizeof(reserva)){perror("ERROR ENVIO RESERVA"); close(gestor_socker); return 1;}
    
    //printf("solicitud reserva enviada\n");
    
    //comprobacion si codigo -666
    int comprobacion_error;
    int bytes = read(gestor_socker, &comprobacion_error, sizeof(comprobacion_error));
    if (bytes<= 0) {  perror("ERROR RECEPCION RESPUESTA");    close(gestor_socker);   return 1;   }
    comprobacion_error=ntohl(comprobacion_error);
    
    if(comprobacion_error == -666) {    
        perror("NO HAY SUFICIENTES WORKERS");
        close(gestor_socker);   return 2;
    }

    //recivir respuesta(ip y puerto trabajador) sin errores
    int respuestaip[nworkers];
    unsigned short respuestaport[nworkers];
    respuestaip[0]=comprobacion_error; //si no es -666 será la primera ip

    if(nworkers>1){
        bytes = read(gestor_socker, &respuestaip[1],(nworkers-1)* sizeof(int)); //lectura resto ips
        if(bytes<= 0) {  perror("ERROR RECEPCION RESPUESTA IP");    close(gestor_socker);   return 1; }
        //ponemos las ips en formato ntohl
        for(int i=1; i<nworkers;i++){   respuestaip[i]=ntohl(respuestaip[i]); }
    }
    bytes= read(gestor_socker, respuestaport, nworkers*sizeof(unsigned short)); //lectura puertos
    if(bytes <= 0) {  perror("ERROR RECEPCION RESPUESTA PORT");    close(gestor_socker);   return 1; }



    /************comprobacion de ip primera ****/
    
    printf("\nWorkers asignados por el gestor:\n");
    struct in_addr ip_addr;
        for(int i=0; i<nworkers;i++){
            ip_addr.s_addr = respuestaip[i];
            printf("    Worker %d: IP %s   Port %hu\n", i,inet_ntoa(ip_addr),respuestaport[i]);
        }
    
    /***************************************** */

    //conexion workers guardandolos en un "array" y estamos enviando al gestor un k para
    //cuando termine de trabajar los workers que los libere
    char ok = 'k';
    int *sockets = malloc(sizeof(int)*nworkers); 

    //sacamos el size de la entrada para pasarlos
    int fd = open(input, O_RDONLY);
    if (fd == -1) { perror("ERROR ABRIENDO FICHERO ENTRADA");   return 1;   }
    struct stat st;
    fstat(fd,&st);
    int tamfd= st.st_size;
    close(fd);
    //tamaño de un bloque de mapreduce 128MiB, bloques que ocupa la entrada
    int bloques = tamfd/blocksize; 
    if(tamfd%blocksize>0){  bloques++;} //sumamos uno si tienen mas resto

    
    for (int i = 0; i < nworkers; i++) {
     //creacion y conexion con el socket del trabajador
        sockets[i]=create_socket_cln_by_addr(respuestaip[i],respuestaport[i]);
        if(sockets[i]<0){ perror("ERROR CONEXION WORKER "); closesockets(sockets,nworkers);    return 1 ; }
//printf("Creado socket pos %d\n",i);
        }


    //envio de todos los bloques como tarea
    int idblock= 0 ;

    //caso solo 1 worker
    int error_envio = 0;
    if(nworkers==1){
    for (int i = 0; i < bloques; i++) {  
        //enviamos tarea, si error se acaba
        error_envio=envio_tarea(sockets[0],program,input,output,blocksize,idblock,numargs,args);
        
        if(error_envio!=0){ closesockets(sockets,nworkers);return 1;}
        idblock++;    //incrementa idblock para siguientes bloques
       
        printf("\nTarea enviada a worker 0:\nprogram: %s|input:%s|output:%s|tambloque:%d|id bloque:%d\n",program, input, output, blocksize, idblock-1);
       
    }
       shutdown(sockets[0], SHUT_WR);//solo 1 worker, solo shut del sockets0

       //respuestas 
    for (int i = 0; i < bloques; i++) {
        char res_trab;
        if(read(sockets[0], &res_trab,1)<=0){perror("ERROR RESPUESTA TRABAJADOR"); 
            close(gestor_socker);   close(sockets)
            ;return 1;
        }
//printf("\nrecivida respuesta Worker %d\n",i);
    }
    }

    //mas workers que tareas o a la par
    else if(nworkers>=bloques){
        for (int i = 0; i < bloques; i++) {  

            error_envio=envio_tarea(sockets[i],program,input,output,blocksize,idblock,numargs,args);

            if(error_envio!=0){ closesockets(sockets,nworkers); return 1; }
            idblock++;    //incrementa idblock para siguientes bloques
            shutdown(sockets[i], SHUT_WR);//para cortar el while del worker

printf("\nTarea enviada a worker %d:\nprogram: %s|input:%s|output:%s|tambloque:%d|id bloque:%d\n",i,program, input, output, blocksize, idblock-1);
           }
           //respuestas
           for (int i = 0; i < bloques; i++) {
            char res_trab;
            if(read(sockets[i], &res_trab,1)<=0){perror("ERROR RESPUESTA TRABAJADOR"); 
                close(gestor_socker);   closesockets(sockets,nworkers);
                return 1;
            }
    //printf("\nrecivida respuesta Worker %d\n",i);
        }
    }

    //caso mas tareas que workers
    else if(nworkers<bloques){
        
        int smax = sockets[0];
        for (int i=1; i<nworkers; i++) {
            //descriptor mas alto
            if (sockets[i] > smax) {  smax = sockets[i];    }
        }

        fd_set desc_sockets;
        
        //enviamos primeros bloques
        for (int i = 0; i < nworkers; i++) {  
            
            error_envio=envio_tarea(sockets[i],program,input,output,blocksize,idblock,numargs,args);

            if(error_envio!=0){ closesockets(sockets,nworkers); return 1; }
            idblock++;    //incrementa idblock para siguientes bloques

            shutdown(sockets[i], SHUT_WR);//para cortar el while del worker

            printf("\nTarea enviada a worker %d:\nprogram: %s|input:%s|output:%s|tambloque:%d|id bloque:%d\n",i,program, input, output, blocksize, idblock-1);
               

}
        FD_ZERO(&desc_sockets); 
        for (int i=0; i<nworkers;i++) { FD_SET(sockets[i], &desc_sockets);  }

        int tareas_asignadas = nworkers; //tareas ya asignadas por ahora

    //esperamos respuestas
    fd_set desc_sockets_copia;
    int ndesc;
    while (tareas_asignadas < bloques) {
        //select modifica la variable: sacamos una copia
        desc_sockets_copia=desc_sockets;
    //esperamos actividad de entrada en descriptores supervisados;
	// puede haber actividad por recepción mensaje o cierre de conexión
    if ((ndesc=select(smax+1 , &desc_sockets_copia, NULL, NULL, NULL))<0) {
        perror("error en select"); closesockets(sockets,nworkers); return 1;
}     
    
	// select indica que hay ndesc descriptores con actividad
    for (int i = 0; i < nworkers && ndesc > 0; i++) {
        if (FD_ISSET(sockets[i], &desc_sockets_copia)) {// descriptor i activo?
            ndesc--;
            int tarea_completada;
            //recibimos el bloque procesado(la respuesta que siempre seran mas bytes que 0 si ok)
            if (recv(sockets[i], &tarea_completada, sizeof(int),MSG_WAITALL) <= 0) {
                close(sockets[i]);
                FD_CLR(sockets[i], &desc_sockets); //se deja de supervisar
            }
            else {
                //tarea completada, asignamos nueva tarea si quedan por hacer
                if (tareas_asignadas < bloques) {
                    tareas_asignadas++;
                    //conectamos otra vez socket
                    close(sockets[i]);
                    sockets[i]=create_socket_cln_by_addr(respuestaip[i],respuestaport[i]);
                    if(sockets[i]<0){ perror("ERROR CONEXION WORKER "); close(sockets);    return 1 ; }
                    FD_SET(sockets[i], &desc_sockets); // añadir nuevo descriptor al set
                if (sockets[i] > smax) smax = sockets[i];
                    
                                    //enviamos bloque

                    error_envio=envio_tarea(sockets[i],program,input,output,blocksize,idblock,numargs,args);

                    if(error_envio!=0){ closesockets(sockets,nworkers); return 1; }
                    idblock++;
                    shutdown(sockets[i], SHUT_WR);//para cortar el while del worker

                    printf("\nTarea enviada a worker %d:\nprogram: %s|input:%s|output:%s|tambloque:%d|id bloque:%d\n",i,program, input, output, blocksize, idblock-1);               
                    

    }
            }
        }
    }
}
}


        //liberacion trabajador/es
    //printf("Enviando para liberacion de workers al gestor\n");
    if(write(gestor_socker,&ok,sizeof(char))!=sizeof(char)){perror("ERROR ENVIO LIBERACION"); 
    close(gestor_socker); 
    closesockets(sockets,nworkers);return 1;}
    
    

    //liberacion sockets
    closesockets(sockets,nworkers);

printf("0\n");

    return 0;
}

