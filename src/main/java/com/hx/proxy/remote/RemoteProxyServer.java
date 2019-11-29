package com.hx.proxy.remote;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteProxyServer {
	private int remoteAccessPort;
	private int remoteHelpdeskPort;

	public RemoteProxyServer(int remoteAccessPort, int remoteHelpdeskPort) {
		this.remoteAccessPort = remoteAccessPort;
		this.remoteHelpdeskPort = remoteHelpdeskPort;
	}

	public void start() {
		ServerSocket serverSocket = null;
		RemoteHelpdesk remoteHelpdesk = null;
		int port = remoteAccessPort;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("remote proxy server started at " + port + ".");
			remoteHelpdesk = new RemoteHelpdesk(remoteHelpdeskPort);
			remoteHelpdesk.init();

//			Runnable consoleRunnable = new ConsoleRunnable(serverSocket);
//			Thread consoleThread = new Thread(consoleRunnable);
//			consoleThread.setName("console");
//			consoleThread.start();

			boolean shutdown = false;
			AtomicInteger ai = new AtomicInteger();
			while (!shutdown) {
				Socket clientSocket = serverSocket.accept();
				Runnable handlerRunnable = new RemoteRequestHandlerRunnable(clientSocket, remoteHelpdesk, ai.decrementAndGet());
				Thread handlerThread = new Thread(handlerRunnable);
				handlerThread.setName("remoteRequestHandler-" + ai.get());
				handlerThread.start();
			}
		} catch (Throwable t) {
			if (t instanceof IOException && serverSocket.isClosed()) {
				System.out.println("remote proxy server closed.");
			} else {
				t.printStackTrace();
			}
		} finally {
			close(serverSocket);
			remoteHelpdesk.shutdown();
		}
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

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/remote.properties"));
		int remoteAccessPort = Integer.parseInt(properties.getProperty("remote.access.port"));
		int remoteHelpdeskPort = Integer.parseInt(properties.getProperty("remote.helpdesk.port"));
				
		System.out.println("remote.access.port:"+remoteAccessPort);
		System.out.println("remote.helpdesk.port:"+remoteHelpdeskPort);
		
		RemoteProxyServer ps = new RemoteProxyServer(remoteAccessPort, remoteHelpdeskPort);
		ps.start();
	}
}
