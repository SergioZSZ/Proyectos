#include <stdio.h>
#include "manager.h"
#include "common.h"
#include "common_srv.h"
#include "srv_addr_arr.h"
#include <netinet/in.h>  
#include <arpa/inet.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/uio.h>
#include <pthread.h>



// información que se la pasa el thread creado
typedef struct thread_info {
    int socket; // añadir los campos necesarios
    struct sockaddr_in clnt_addr;
    struct srv_addr_arr *trabajadores;
} thread_info;




void *connection_handler(void *arg){
    int res;
    int  datos[3];
    thread_info *thinf = arg; // argumento recibido

    if ((res=recv(thinf->socket, datos, sizeof(datos), MSG_WAITALL))!=sizeof(datos)) {
        if (res!=0) perror("error en read");
        close(thinf->socket);
        return NULL;
    }

    int datoshost[3];


    //caso worker
    if(ntohl(datos[2])==0){

        //pasamos a formato host lo necesario de los datos

    datoshost[0] =ntohl(datos[0]);  //id del worker 
    datoshost[1] = datos[1]; //puerto srv del worker
    datoshost[2] = ntohl(datos[2]); //identificador worker

    //alta a traves de la dir ip y puerto en formato red 
        srv_addr_arr_add(thinf->trabajadores, thinf->clnt_addr.sin_addr.s_addr, datoshost[1]);
        srv_addr_arr_print("después de alta", thinf->trabajadores);
        char* ok ="OK";
        if (write(thinf->socket, ok, strlen(ok) + 1)!=strlen(ok) + 1) {
            perror("error en write");
        }
        close(thinf->socket);
        free(thinf);
        printf("conexión del cliente cerrada\n");
        return NULL;
    }

    //caso cliente
    else if (ntohl(datos[2])==1){

        //pasamos a formato host lo necesario de los datos
        datoshost[0] =ntohl(datos[0]);  //id del client
        datoshost[1] = ntohl(datos[1]); //nº workers que solicita
        datoshost[2] = ntohl(datos[2]); //identificador client
            
//printf("\npeticion cliente %i, nº workers = %i\n",datoshost[0],datoshost[1]);
    
        //caso not_enought_workers
            int error = htonl(-666);
            int disponibles = srv_addr_arr_size(thinf->trabajadores);
            if(disponibles< datoshost[1]){
                if(write(thinf->socket, &error, sizeof(error)) != sizeof(error)) {   perror("error en write");  }
                close(thinf->socket);
                free(thinf);
                return NULL;
            }

        //realizar reserva, cerrar socket y liberar thinf
            int reservados[datoshost[1]];
            if(srv_addr_arr_alloc(thinf->trabajadores, datoshost[1],reservados)==-1){
                if(write(thinf->socket, &error, sizeof(error)) != sizeof(error)) {   perror("error en write");  }
                close(thinf->socket);
                free(thinf);
                return NULL;
            }
    
//printf("\nworkers reservados:\n");
            
            int ip[datoshost[1]];
            unsigned short puerto[datoshost[1]];
            printf("\n");
            for(int i = 0; i < datoshost[1]; i++){
                if(srv_addr_arr_get(thinf->trabajadores, reservados[i], &ip[i], &puerto[i]) == -1){
                    perror("ERROR CON LOS DATOS DEL WORKER");    return 1;   }
                //estructura ip por si quieres comprobar las ips
                struct in_addr ip_addr;
                ip[i] = htonl(ip[i]);
                ip_addr.s_addr = ip[i];            
                //formato red
                printf("IP: %s  PORT: %hu\n",inet_ntoa(ip_addr), puerto[i]);
            }
            //enviamos
            if(write(thinf->socket, ip, sizeof(ip)) != sizeof(ip)) { perror("ERROR ENVIO IPS");  return 1;}
            if(write(thinf->socket, puerto, sizeof(puerto)) != sizeof(puerto)) {    perror("ERROR ENVIO PUERTOS");  return 1;}
            
            printf("Todos los workers enviados al cliente\n\n");
            //printf("\ndatos de los trabajadores enviados\n");
            srv_addr_arr_print("después de reserva", thinf->trabajadores); 

            //cuando recibimos respuesta del cliente para liberar liberamos y cerramos socket
            char ok;
            if(read(thinf->socket,&ok, sizeof(ok))<=0){ perror("ERROR AL RECIBIR RESPUESTA DE CIERRE CLIENTE"); return 1;}
            srv_addr_arr_free(thinf->trabajadores,datoshost[1],reservados);
            srv_addr_arr_print("después de liberar",thinf->trabajadores);
            close(thinf->socket);
            free(thinf);

            printf("conexion con el cliente cerrada\n");
            return NULL;
        }

}


int main(int argc, char *argv[]) {
    int s, s_conec;
    unsigned int addr_size;
    struct sockaddr_in clnt_addr;
    

    if (argc!=2) {
        fprintf(stderr, "Usage: %s port\n", argv[0]);
        return 1;
    }
    struct srv_addr_arr *trabajadores = srv_addr_arr_create();



    // inicializa el socket y lo prepara para aceptar conexiones
    if ((s=create_socket_srv(atoi(argv[1]), NULL)) < 0) return 1;

    // prepara atributos adecuados para crear thread "detached"
    pthread_t thid;
    pthread_attr_t atrib_th;
    pthread_attr_init(&atrib_th); // evita pthread_join
    pthread_attr_setdetachstate(&atrib_th, PTHREAD_CREATE_DETACHED);
    while(1) {
        addr_size=sizeof(clnt_addr);    
        // acepta la conexión
        if ((s_conec=accept(s, (struct sockaddr *)&clnt_addr, &addr_size))<0){
            perror("error en accept");
            close(s);
            return 1;
        }
        printf("conectado cliente con ip %s y puerto %u (formato red)\n",
                inet_ntoa(clnt_addr.sin_addr), clnt_addr.sin_port);
        // crea el thread de servicio
        thread_info *thinf = malloc(sizeof(thread_info));
        thinf->socket=s_conec;
        thinf->clnt_addr = clnt_addr;
        thinf->trabajadores = trabajadores;
        pthread_create(&thid, &atrib_th, connection_handler, thinf);
        
    }
    close(s); // cierra el socket general
    return 0;
}

