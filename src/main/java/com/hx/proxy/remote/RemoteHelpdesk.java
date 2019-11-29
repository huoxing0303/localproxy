package com.hx.proxy.remote;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteHelpdesk {
	private int remoteHelpdeskPort;
	private AtomicInteger ai = null;
	
	public RemoteHelpdesk(int remoteHelpdeskPort) {
		this.remoteHelpdeskPort = remoteHelpdeskPort;
		ai = new AtomicInteger(remoteHelpdeskPort);
	}
	
	private ServerSocket serverSocket;
	private Socket helpdeskSocket;
	private InputStream helpdeskIs;
	private OutputStream helpdeskOs;
	private RemoteHelpdeskRunnable remoteHelpdeskRunnable;
	
	public void init() throws Exception{
		serverSocket = new ServerSocket(ai.get());
		System.out.println("remote helpdesk started at " + ai.get() + ".");
		remoteHelpdeskRunnable = new RemoteHelpdeskRunnable(serverSocket, this);
		Thread thread = new Thread(remoteHelpdeskRunnable);
		thread.setName("remoteHelpdesk");
		thread.start();
	}

	public synchronized void setHelpdeskSocket(Socket helpdeskSocket) {
		this.helpdeskSocket = helpdeskSocket;
	}

	public synchronized Socket getTargetSocket() throws Exception {
		Socket targetSocket = null;
		ServerSocket dynamicServerSocket = null;

		try {
			if (helpdeskSocket == null || helpdeskSocket.isClosed()) {
				throw new RuntimeException("loca helpdesk not start yet.");
			}
			dynamicServerSocket = getDynamicServerSocket();
			int dynamicPortInt = dynamicServerSocket.getLocalPort();

			helpdeskOs = helpdeskSocket.getOutputStream();
			String dynamicPort = Integer.toString(dynamicPortInt);
			byte[] b = dynamicPort.getBytes();
			helpdeskOs.write(b);

			//System.out.println("reading acknowledgement from local helpdesk");
			helpdeskIs = helpdeskSocket.getInputStream();
			int readCnt = 0;
			do {
				readCnt = helpdeskIs.read(b);
			} while (readCnt < b.length);
			String acknowledgement = new String(b, 0, readCnt);
			//System.out.println("acknowledgement from local helpdesk:" + acknowledgement);
			if (!dynamicPort.equals(acknowledgement)) {
				throw new RuntimeException("acknowledgement inconsistency, " + dynamicPort + " vs " + acknowledgement);
			}

			targetSocket = dynamicServerSocket.accept();
		} finally {
			close(dynamicServerSocket);
		}

		return targetSocket;
	}

	private ServerSocket getDynamicServerSocket() {
		ServerSocket result = null;
		try {
			result = new ServerSocket(ai.incrementAndGet());
		} catch (Exception e) {
			if (e instanceof BindException) {
				System.out.println("port " + ai.get() + " used already.");
				result = getDynamicServerSocket();
			} else {
				e.printStackTrace();
			}
		}

		if(ai.get() > 10000) {
			ai.set(remoteHelpdeskPort);
		}
		
		return result;
	}

	public void shutdown() {
		remoteHelpdeskRunnable.shutdown();
		close(serverSocket);
	}

	private void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
