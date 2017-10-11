Ziyu Qiu
COSI 131a
PA2 Task1 README
Oct.23 2016

For this assignment I modified the MyClient, BasicClient, SharedClient,  BasicServer and ConcreteFactory files.

For myClient, 
	I simply implement a constructer, which has name and industry as parameters, randomly generated speed from 0-9 and requestLevel as 3.

For BasicClient and SharedClient, 
	I wrote a constructer to pass in the name and industry; 
	and override the toString method, where the only difference is type (BASIC for BasicClient, SHARED for SharedClient).

For BasicServer, 
	I added a field: List<Client> clientList which preserves clients in process.
	Then I made both connetInner and disconnectInner synchronized methods.
		Thus it is impossible for two invocations of synchronized methods on the same object to interleave and changes to the state of the object are visible to all threads.
		(Reference for this paragraph: https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html)
	For connectInner, I considered the following situations:
		a. if there is no client in process, 
			the new client can always get into the list;
		b. if there is one client in process,
			The basic server cannot process a shared and basic client at the same time, so the one in process and the new one cannot be the same type. However, the server cannot serve more than one basic client at a time (2). Thus neither the one in process, nor the new one can be basic client (3). So both of them should be SharedClient. In addition, the basic server cannot process two SharedClients of the same industry, so the one in process and the new one cannot be the same industry (4).
			* As a result, it must fulfill the following conditions:
				1 client in process
				the client in process is SharedClient
				the incoming client is SharedClient
				the client in process and the incoming client not the same industry
		If it meets either situation a. or b., add the new client into list and return true
		c. if there are 2 clients in process already, or it does not meet situation a. and b.,
			the new client cannot get into the list, return false
	For disconnectInner, simply remove the client from the clientList.

I hereby attest that the submitted work (modification over provided code) is mine.