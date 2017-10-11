Ziyu Qiu
COSI 131a
PA2 Task2 README
Oct.31 2016

    For this assignment I didn't modify any file other than the MasterServer.java.

    For the masterServer, I first set up few fields:
	mapQueues which preserves a map of queue(clients in wait list);
	mapServers which preserves a map of BasicServers;
	serverLock which is the Reentrant lock for the server;
	queueLock which is the Reentrant lock for the queue;
	serverCond which is the condition for serverLock;
    I mainly focused on the connectInner method. Basically, I followed the instructions given by the PA description. At the very beginning, I retrieved the key, the corresponding server, and the waitlist of clients for the server. Then I added a lock to the server, so that other threads cannot access the server meanwhile. Each client is a thread.
    This part is for the situation that a new client arrives. I added a lock to the queue while I am dealing with the queue to keep it synchronized. If the queue for this server is empty, which means there is no client waiting in line, then try to connect the client to the server. If it is successfully connected, return true. Else if the client fails to connect due to a server condition being violated or if there is any client waiting in the line, then add the client to the tail of the queue and wait for its turn. Avoid busy waiting by letting this thread to sleep (by calling the await function of the server condition). After finishing all these steps, unlock the queue.
    Next part is for the situation that it is waken up. It is waken up when a client on the same server finished its job. Since all threads(clients) waiting in the queue will be waken up, the client needs to identify whether it is the one which is aimed to be signaled (the head of the queue).The head of the queue is allowed to proceed, so if it is the head of the queue, that client will try to connect to the server. After a client successfully connects to a server, it removes itself from the queue and inform the new head of the queue so it may attempt to connect. However, if the head client fails to connect to the server due to a server condition being violated, the client must wait and let the thread sleep to avoid busy waiting. Unlock the server after every step is done.
    For disconnectInner method, simply lock the server, disconnect the client from the server it was assigned to, signalAll to wake up the sleeping thread, then release the lock.

    I hereby attest that the submitted work (modification over provided code) is mine.