// Servidor que implementa la interfaz remota Queue
package broker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import mqprot.Queue;
import mqprot.QueueType;
import mqprot.Client;

class QueueImpl extends UnicastRemoteObject implements Queue  {
    
	public static final long serialVersionUID=1234567890L;
    private final Broker b;
	private final String qname;
    private final QueueType qtype;
    private Collection<Client> clientes;
    
    public QueueImpl(Broker b, String qname, QueueType qtype) throws RemoteException {
    	this.b=b;
    	this.qname=qname;
    	this.qtype=qtype;
    	this.clientes = new LinkedList<Client>();
    }
    
    public String getName() throws RemoteException {
        return qname;
    }
    public QueueType getQueueType() throws RemoteException {
        return qtype;
    }
    public void bind(Client cl) throws RemoteException {
    	clientes.add(cl);
    }
    
    public void unbind(Client cl) throws RemoteException {
    	clientes.remove(cl);	
    }
    
    public Collection <Client> clientList() throws RemoteException {
        return new LinkedList<>(clientes);
    }
    public void send(byte[] m) throws RemoteException {
    	//caso Publicar/subscribir
    	if (this.qtype.equals(QueueType.PUBSUB)){
    		for(Client c : clientes) {
        		c.deliver(this.getName(), m);
        		b.pendientes.put(c,b.pendientes.getOrDefault(c,0)+1);;
        	}
    	}
    	else {
    		Map<Client,Integer> cargas= b.clientsQueueLength();
    		Client clientemenos=null;
    		int menor = Integer.MAX_VALUE;
    		for (Map.Entry<Client,Integer> entry : cargas.entrySet()) {
    		    if (entry.getValue()< menor) {
    		        menor = entry.getValue();
    		        clientemenos = entry.getKey();
    		    }
    		}
    		clientemenos.deliver(this.getName(), m);
    		b.pendientes.put(clientemenos,b.pendientes.getOrDefault(clientemenos,0)+1);;
    	}
    	
    }
}
