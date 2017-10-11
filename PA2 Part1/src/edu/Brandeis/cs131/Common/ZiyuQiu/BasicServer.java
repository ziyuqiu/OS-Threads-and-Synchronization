package edu.Brandeis.cs131.Common.ZiyuQiu;

import java.util.LinkedList;
import java.util.List;

import edu.Brandeis.cs131.Common.Abstract.Client;
import edu.Brandeis.cs131.Common.Abstract.Server;

public class BasicServer extends Server {
	//a list which preserves clients in process
	List<Client> clientList = new LinkedList<Client>();
	
	//Constructor
	public BasicServer(String name) {
		super(name);
	}

	@Override
	public synchronized boolean connectInner(Client client) {
		//An instance of a basic server will not service:
			//(1) more than two shared clients at the same time.
			//(2) more than one basic client at a time.
			//(3) a shared and basic client at the same time.
			//(4) two shared clients of the same industry.
			if(clientList.size() ==0 || //no client in process
				clientList.size() == 1 &&					
					//The basic server cannot process a shared and basic client at the same time
					//so the one in process and the new one cannot be the same type
					//however, the server cannot serve more than one basic client at a time (2)
					//thus neither the one in process nor the new one can be basic client
					//(3)
					clientList.get(0) instanceof SharedClient &&
					client instanceof SharedClient &&
					//The basic server cannot process two shared clients of the same industry
					//so the one in process and the new one cannot be the same industry
					//(4)
					! (clientList.get(0).getIndustry().equals(client.getIndustry()))				
				){
				clientList.add(client);
				return true;
			} //else  
				// situation (1) which is 2 shared clients in processing list also falls into else
				// because as long as clientList.size() >=2 it will return false
				return false;
	}

	@Override
	public synchronized void disconnectInner(Client client) {
		clientList.remove(client);
	}
}
