package com.hx.proxy.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteHelpdeskRunnable implements Runnable {
	private ServerSocket serverSocket;
	private RemoteHelpdesk remoteHelpdesk;
	private boolean shutdown = false;
	public RemoteHelpdeskRunnable (ServerSocket serverSocket, RemoteHelpdesk remoteHelpdesk) {
		this.serverSocket = serverSocket;
		this.remoteHelpdesk = remoteHelpdesk;
	}
	
	@Override
	public void run() {
		Socket helpdesksocket = null;
		while(!shutdown) {
			try {
				helpdesksocket = serverSocket.accept();
			} catch (Throwable t) {
				if (t instanceof IOException && serverSocket.isClosed()) {
					System.out.println("remote helpdesk server closed.");
				} else {
					t.printStackTrace();
				}
			}
			remoteHelpdesk.setHelpdeskSocket(helpdesksocket);
		}
	}
	
	public void shutdown() {
		shutdown = true;
	}
	
}
