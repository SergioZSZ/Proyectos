// Servidor que implementa la interfaz remota MQSrv
package broker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import mqprot.MQSrv;
import mqprot.Queue;
import mqprot.QueueType;
import mqprot.Client;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class Broker extends UnicastRemoteObject implements MQSrv  {
	
    public static final long serialVersionUID=1234567890L;
    private Collection<Client> clientes;
    private Map<String,Queue> colas;
    public Map<Client,Integer>pendientes;

    public Broker() throws RemoteException {
    	this.clientes= new LinkedList<Client>();
    	this.colas= new HashMap<>();
    	this.pendientes=new HashMap<>();
    }
    
   

    public int getVersion() throws RemoteException {
        return MQSrv.version;
    }
    
    public synchronized void addClient(Client cl) throws RemoteException {
    	clientes.add(cl);
    }
    
    public synchronized void removeClient(Client cl) throws RemoteException {
    	clientes.remove(cl);
    }
    
    public Collection <Client> clientList() throws RemoteException {
        return clientes;
    }
    
    public synchronized void broadcast(byte[] m) throws RemoteException {
    	for(Client c : clientes) {
    		c.deliver(null, m);
    		pendientes.put(c,pendientes.getOrDefault(c,0)+1);
    	}
    }
    
    public synchronized Queue createQueue(String name, QueueType qc) throws RemoteException {
    	//si ya existe una cola con ese nombre se comprueba su tipo, si coincide
    	//no se crea una cola nueva sino que se devuelve esa
    	
    	if(colas.containsKey(name)) {
        	Queue colaAcomprobar;
        	colaAcomprobar= colas.get(name);
        	
        	if(colaAcomprobar.getQueueType().equals(qc)) {
        		return colaAcomprobar;
        	}
        	else { //si no es del mismo tipo se considera error devolviendo null
        		return null;
        	}
        }
    	//si no existe la cola en cuestion se crea una con esos valores
    	Queue ncola = new QueueImpl(this,name,qc);
    	colas.put(name, ncola);
    	return ncola;
    }
    
    public Collection <Queue> queueList() throws RemoteException {
    	//devolvemos linkedList del valor de las colas
        return new LinkedList<>(colas.values());
    }
    
    public Map <Client, Integer> clientsQueueLength() throws RemoteException {
        return pendientes;
    }
    public synchronized void ack(Client cl) throws RemoteException {
		pendientes.put(cl,pendientes.getOrDefault(cl,1)-1);
	//con esto no funciona	if(pendientes.get(cl)<=0) {	pendientes.remove(cl);	}
    }
    static public void main (String args[])  {
        if (args.length!=1) {
            System.err.println("Usage: Broker registryPortNumber");
            return;
        }
        try {
            Broker brk = new Broker();
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            registry.rebind("MQ", brk);
        }
        catch (Exception e) {
            System.err.println("Broker exception: " + e.toString());
            System.exit(1);
        }
    }
}
