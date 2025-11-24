// Clase de cliente que proporciona los métodos local del API
package mq;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import mqprot.MQSrv;
import mqprot.Client;
import mqprot.Queue;

public class MQClient extends UnicastRemoteObject implements Client  {
    public static final long serialVersionUID=1234567890L;
    private String name;
    private LinkedList<Message> mensajes;
    

    // constructor
    public MQClient(String name) throws RemoteException {
    	this.name = name;
    	this.mensajes= new LinkedList<Message>();
    }
    public String getName() throws RemoteException {
        return name;
    }
    public synchronized void deliver(String queue, byte[] m) throws RemoteException {
    	Message newMensaje = new Message(queue, m);
    	mensajes.add(newMensaje);
    }
    
    public synchronized Message poll() {
    	if(mensajes.isEmpty()) {	return null;	}
    	
    	Message mensaje = mensajes.removeFirst();
        return mensaje;
    }
    public MQSrv MQconnect(String host, String port) throws RemoteException, NotBoundException {
        //obtenemos referencia a el registry con ese host y puerto
    	Registry regist=LocateRegistry.getRegistry(host, Integer.parseInt(port));
  /* El registry es el "directorio" donde los servicios RMI se registran
		el host+port deben coincidir con donde se inició el registry¿(start_rmiregistry.sh)
*/		
    	MQSrv server = (MQSrv)regist.lookup("MQ");
/* el server que buscamos lo obtendremos buscando en el registro el broker(server)
 * y teniendo que castearlo como el objeto MQSrv para tratar con el
 * */
    	return server;
    	/*resumen de flujo
    	*Cliente → Registry: "Dame el servicio 'MQServer'"
    	Registry → Cliente: "Aquí tienes el stub del broker"
    	Cliente → Broker: Llamadas a métodos a través del stub
    	*/
    	
    }
    // función que serializa un objeto en un array de bytes
    public byte [] serializeMessage(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(o);
        return bos.toByteArray();
    }
}
