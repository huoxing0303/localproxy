package com.hx.proxy.remote;

import java.net.Socket;

import com.hx.proxy.RequestHandlerRunnable;

public class RemoteRequestHandlerRunnable extends RequestHandlerRunnable {
	private Socket clientSocket;
	private RemoteHelpdesk remoteHelpdesk;
	private int count;

	public RemoteRequestHandlerRunnable(Socket clientSocket, RemoteHelpdesk remoteHelpdesk, int count) {
		this.clientSocket = clientSocket;
		this.remoteHelpdesk = remoteHelpdesk;
		this.count = count;
	}

	@Override
	protected Socket getClientSocket() {
		return clientSocket;
	}

	@Override
	protected String getClientThreadName() {
		return "remoteClient" + count;
	}

	@Override
	protected Socket getTargetSocket() throws Exception{
		return remoteHelpdesk.getTargetSocket();
	}

	@Override
	protected String getTargetThreadName() {
		return "remoteTarget" + count;
	}
	
}
