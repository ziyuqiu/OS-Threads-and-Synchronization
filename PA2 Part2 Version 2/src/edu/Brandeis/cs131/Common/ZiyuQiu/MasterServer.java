package edu.Brandeis.cs131.Common.ZiyuQiu;

import java.util.HashMap;
import java.util.LinkedList;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Log.Log;
import edu.Brandeis.cs131.Common.Abstract.Server;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterServer extends Server {
    private final Map<Integer, List<Client>> mapQueues = new HashMap<Integer, List<Client>>();
    private final Map<Integer, Server> mapServers = new HashMap<Integer, Server>();
    final Lock serverLock = new ReentrantLock();
    final Lock queueLock = new ReentrantLock();
    final Condition serverCond  = serverLock.newCondition();     

    public MasterServer(String name, Collection<Server> servers, Log log) {
        super(name, log);
        Iterator<Server> iter = servers.iterator();
        //set up servers
        while (iter.hasNext()) {
            this.addServer(iter.next());
        }
    }

    public void addServer(Server server) {
        int location = mapQueues.size();
        this.mapServers.put(location, server);
        this.mapQueues.put(location, new LinkedList<Client>());
    }

    @Override
    //distribute clients to their respective basic servers
    public boolean connectInner(Client client) {
    	//The client uses the getKey() method to
    	int key = getKey(client);
    	//determine what server it has been assigned access to
    	Server assignedServer = mapServers.get(key);
    	//clientQueue preserves a queue of clients waiting to connect
    	List<Client> clientQueue = mapQueues.get(key);
    	
    	serverLock.lock();
    	try{
    		queueLock.lock();
		    try{
		    	//If the queue for this server is empty, 
		    	//which means no client is waiting in line
		    	if(clientQueue.isEmpty()){
		    		//try to connect the client to the server
		    		if(assignedServer.connect(client)){
		    			//if it is successfully connected, return true
		    			return true;
		    		} 
		    		//else if the client fails to connect due to a server condition being violated
		    		//or if there is any client waiting in line
		    	} else {
			       	//the client adds itself to the tail of the queue
			    	clientQueue.add(client);
			   		//and wait for its turn
			   		try {
						serverCond.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}		    		
		   		}
		    } finally {
		    	queueLock.unlock();
		    }
	    	
		    while(true){
			    //The head of the queue is allowed to proceed. 
				Client head = clientQueue.get(0);
				//Since all threads(clients) waiting in the queue will be waken up
				//identify whether it is the one which is aimed to be signaled (the head of the queue)
				if(client == head){
					//After it is confirmed that it's the one being called (head of queue)
				    //That client will try to make a request to that server
					if(assignedServer.connect(head)){
					//After a client successfully connects to a server 
					//it should remove itself from the queue and 
					    queueLock.lock();
					    try{
					    	clientQueue.remove(0);
					    } finally {
					    	queueLock.unlock();
					    }
					//inform the new head of the queue so it may attempt to connect. 
					    serverCond.signalAll();
					    return true;		    	
					} else {
					    //If it fails to connect due to a server condition being violated,
					    //the client must wait					
						try {
							serverCond.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}					
				    }
				}
		    }
    	} finally {
    		serverLock.unlock();
    	}		
    }

    @Override
    public void disconnectInner(Client client) {
        //When a Client exits the MasterServer by calling disconnect on a MasterServer instance
        Server assignedServer = mapServers.get(getKey(client));
        serverLock.lock();
        try{
        	//the MasterServer must call disconnect() on the BasicServer the client is currently connected to,
            assignedServer.disconnect(client);
            //possibly allowing the head of the server queue to gain access.
        	serverCond.signalAll();
        } finally {
        	serverLock.unlock();
        }
    }

	//returns a number from 0- mapServers.size -1
    // MUST be used when calling get() on mapServers or mapQueues
    private int getKey(Client client) {
        return client.getSpeed() % mapServers.size();
    }
}
