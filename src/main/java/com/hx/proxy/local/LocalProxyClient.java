package com.hx.proxy.local;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalProxyClient {
	private String remoteIp; // 47.100.220.18
	private int remoteHelpdeskPort; //9528
	private String targetIp; // 127.0.0.1
	private int targetPort; // 8080
	
	public LocalProxyClient(String remoteIp, int remoteHelpdeskPort, String targetIp, int targetPort) {
		this.remoteIp = remoteIp;
		this.remoteHelpdeskPort = remoteHelpdeskPort;
		this.targetIp = targetIp;
		this.targetPort = targetPort;
	}

	public void start() {
		Socket remoteProxySocket = null;
		InputStream remoteProxyIs = null;
		OutputStream remoteProxyOs = null;
		boolean shutdown = false;
		int port = remoteHelpdeskPort; 
		try {
			remoteProxySocket = new Socket(remoteIp, port);
			System.out.println("local proxy client started.");
			System.out.println("local help desk connected to " + port + ".");
			
			remoteProxyIs = remoteProxySocket.getInputStream();
			remoteProxyOs = remoteProxySocket.getOutputStream();
			
//			Runnable consoleRunnable = new ConsoleRunnable(remoteProxySocket);
//			Thread consoleThread = new Thread(consoleRunnable);
//			consoleThread.setName("console");
//			consoleThread.start();
			
			AtomicInteger ai = new AtomicInteger();
			byte[] b = new byte[4096];
			int readCnt = 0;
			while (!shutdown) {
				readCnt = remoteProxyIs.read(b);
				if(readCnt == -1) {
					break;
				}
				remoteProxyOs.write(b, 0, readCnt);//acknowledgement
				//parse dynamic remote port
				int dynamicPort = Integer.parseInt(new String(b, 0, readCnt));
				
				Runnable handlerRunnable = new LocalRequestHandlerRunnable(remoteIp, targetIp, targetPort, dynamicPort, ai.decrementAndGet());
				Thread handlerThread = new Thread(handlerRunnable);
				handlerThread.setName("localRequestHandler-" + ai.get());
				handlerThread.start();
			}
		} catch (Throwable t) {
			if (t instanceof IOException && remoteProxySocket != null && remoteProxySocket.isClosed()) {
				System.out.println("local proxy client closed.");
			} else {
				t.printStackTrace();
			}
		} finally {
			close(remoteProxySocket);
		}
	}
	
	private void close(Closeable c) {
		if(c!=null) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/local.properties"));
		
		String remoteIp = properties.getProperty("remote.ip");
		int remoteHelpdeskPort = Integer.parseInt(properties.getProperty("remote.helpdesk.port"));
		
		String targetIp = properties.getProperty("target.ip");
		int targetPort = Integer.parseInt(properties.getProperty("target.port"));
		
		System.out.println("remote.ip:"+remoteIp);
		System.out.println("remote.helpdesk.port:"+remoteHelpdeskPort);
		System.out.println("target.ip:"+targetIp);
		System.out.println("target.port:"+targetPort);
		
		LocalProxyClient server = new LocalProxyClient(remoteIp, remoteHelpdeskPort, targetIp, targetPort);
		server.start();
	}
}
