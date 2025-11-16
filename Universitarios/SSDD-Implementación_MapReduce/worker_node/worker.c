#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/uio.h>
#include "common_srv.h"
#include "worker.h"
#include "manager.h"
#include "common.h"
#include "common_srv.h"
#include "common_cln.h"
#include <libgen.h>//basename



char* read_dato(int sock) { //lector de senders
    int len;
    if(read(sock,&len,sizeof(int)) != sizeof(int)) {return NULL;}
    if(len==0){return NULL;}  //no mas writes por parte del client
    if(len==-666){  return NULL;}
    char* buf = malloc(len);
    if(read(sock, buf, len) != len) {free(buf); return NULL;}
    return buf;
}
//solo recibe una petición por conexión
void connection_handler(int s){
    int bloques = 0; //bloques enviados para luego hacer waits de cada proceso
while(1){
    bloques++;
    //guardamos lo que envia el maps
//printf("\nInicio reads\n");

    char* program = read_dato(s);
    if(program==NULL){  break;}  
//printf("\nprogram:    %s\n", program);
    char* input = read_dato(s);
    if(input==NULL){break; }  
//printf("input:  %s\n",input);
    char* output = read_dato(s);
    if(output==NULL){break;   }  
//printf("output:   %s\n",output);
    int blocksize;
    if(read(s,&blocksize,sizeof(int))!=sizeof(int)){perror("ERROR AL RECIBIR Nº BLOQUES");break;   } 
//printf("tam bloque:   %d\n",blocksize);
    int id_bloque;
    if(read(s,&id_bloque,sizeof(int))!=sizeof(int)){perror("ERROR AL RECIBIR ID_BLOQ");break;   } 
//printf("id bloque:   %d\n",id_bloque);

    int numargs;
    if(read(s,&numargs,sizeof(int))!=sizeof(int)){perror("ERROR AL RECIBIR NUMARGS");break;   } 

    char **args = malloc((numargs+2)*sizeof(char*));
    args[0] = program;
    for(int i=0; i<numargs; i++){
        args[i+1]=read_dato(s);
        if(args[i+1]==NULL){perror("ERROR AL RECIBIR ARGUMENTOS");break;   } 
    }
    args[numargs+1]=NULL;
    
 

printf("\nTarea recivida:\nprogram: %s|input:%s|output:%s|tambloque:%d|id bloque:%d\n",program, input, output, blocksize, id_bloque);



        //apertura entrada y creacion tuberia
        int entrada = open(input,O_RDONLY);
        if (entrada<0) {perror("ERROR AL ABRIR ENTRADA");    break;   }
        int pid;
        int fd[2];
        pipe(fd);
        off_t offset;
        int necesita_redireccion = strcmp(program, "../programas/create_token.sh") != 0;
        switch(pid=fork()){
            case -1:
		    perror("ERROR EN FORK\n");
		    exit(1);

            case 0:
            if(necesita_redireccion==0){
                execvp(program, args);
                perror("ERROR AL EJECUTAR EL PROGRAMA EN EL FORK\n");
                exit(1);
            }
                close(fd[1]);//escritura cerrada
printf("procesando bloque %d\n",id_bloque);
            //recibimos solo la parte a modificar
                dup2(fd[0], STDIN_FILENO); 
            //cambiamos el stdout creandolo si no estaba
                char* base= basename(input);
                char nueva_salida[256];
                
                snprintf(nueva_salida, sizeof(nueva_salida),"%s/%s-%05d",output,base,id_bloque);
                int fd2 = open(nueva_salida, O_WRONLY | O_CREAT |O_TRUNC, 0666);
                if(fd2< 0){    perror("ERROR CON LA SALIDA");   exit(1); }
                dup2(fd2, STDOUT_FILENO); 
                close(fd2);

                //ejecucion programa
                execvp(program, args);
                perror("ERROR AL EJECUTAR EL PROGRAMA EN EL FORK\n");
                exit(1);

            default:

if(necesita_redireccion!=0){
    //nos situaremos en la posicion de bloque que nos corresponde
    offset = blocksize*id_bloque;
    //enviamoms al hijo la entrada
    sendfile(fd[1], entrada, &offset, blocksize);
    close(fd[1]);
}

close(entrada);
        }//fin fork
    }//fin bucle

    //waits de los hijos y respuesta al client
    printf("\n");
    char ok = 'k';
    for(int i=0; i< bloques-1;i++){
        wait();
        printf("wait proceso DONE\n");

        //envio de ok al client
    if(write(s,&ok,1)<=0) { perror("ERROR ENVIAR RESPUESTA CIERRE CLIENTE");  break;  }
//printf("proceso terminado\n");
    }
    
    

    close(s);
    printf("\n");
    printf("conexión del cliente cerrada\n");
    
}




int main(int argc, char *argv[]) {
    if (argc!=3) {
        fprintf(stderr, "Usage: %s manager_host manager_port\n", argv[0]);
        return 1;
    }

    int s, s_conec, c;
    unsigned int addr_size;
    struct sockaddr_in clnt_addr,srv_addr;

    // inicializa el socket_server y lo prepara para aceptar conexiones
    if ((s=create_socket_srv(0, NULL)) < 0){    perror ("ERROR SOCKET S"); return 1;  }

    socklen_t len = sizeof(srv_addr);
    getsockname(s,(struct sockaddr *)&srv_addr, &len);
    //puerto server para enviarlo al manager
    int port_srv = srv_addr.sin_port;
    

    //socket para comunicacion con gestor
    if((c = create_socket_cln_by_name(argv[1],argv[2]))< 0 ) { perror ("ERROR SOCKET C"); return 1;  }
    getsockname(c,(struct sockaddr *)&clnt_addr, &len );
    int port_cl = clnt_addr.sin_port;
    
    //
    
    printf("Puerto asignado como clnt(formato red): %u\n", port_cl);
    printf("Puerto asignado como srvr(formato red): %u\n",port_srv);

    //da de alta en gestor
        int datosred[3];    //datos necesarios para el gestor

        datosred[0] = htonl(getpid()); // id del worker
        datosred[1] = port_srv; //puerto server a enviar
        datosred[2] = htonl(0);        // 0 = worker
        // Envia sus datos al gestor para darse de alta
        int byte = write(c, datosred, sizeof(datosred));
        if (byte != sizeof(datosred)) {
            perror("ERROR ENVIO DATOS AL GESTOR"); return 1; }
                    
        
    //comprobamos si gestor devuelve respuesta
    char respuesta[3];
    int res = read(c,respuesta,sizeof(respuesta));
    if (res <= 0) { perror("Error al recibir la respuesta del gestor"); return 1;   }

    //cierre socket
    close(c);

    while(1) {
        addr_size=sizeof(clnt_addr);
        // acepta la conexión
        if ((s_conec=accept(s, (struct sockaddr *)&clnt_addr, &addr_size))<0){
            perror("error en accept");
            close(s);
            return 1;
        }
        
    printf("conectado cliente con ip %s y puerto %u (formato red)\n",
                inet_ntoa(clnt_addr.sin_addr), ntohs(clnt_addr.sin_port));
	connection_handler(s_conec);
    }
    close(s); // cierra el socket general
    return 0;
}


