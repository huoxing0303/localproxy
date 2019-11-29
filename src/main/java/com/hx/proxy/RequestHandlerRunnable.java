package com.hx.proxy;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class RequestHandlerRunnable implements Runnable {
	protected abstract Socket getClientSocket() throws Exception;
	protected abstract String getClientThreadName();
	protected abstract Socket getTargetSocket() throws Exception;
	protected abstract String getTargetThreadName();

	@Override
	public void run() {
		Socket clientSocket = null;
		Socket targetSocket = null;
		InputStream clientIs = null;
		OutputStream targetOs = null;
		InputStream targetIs = null;
		OutputStream clientOs = null;
		try {
			targetSocket = getTargetSocket();
			clientSocket = getClientSocket();
			clientIs = clientSocket.getInputStream();
			targetOs = targetSocket.getOutputStream();
			
			Runnable clientRunnable = new ReadWriteRunnable(clientIs, targetOs);
			Thread clientThread = new Thread(clientRunnable);
			String clientThreadName = getClientThreadName();
			if(clientThreadName!=null && clientThreadName.trim().length() > 0) {
				clientThread.setName(clientThreadName);
			}
			
			targetIs = targetSocket.getInputStream();
			clientOs = clientSocket.getOutputStream();
			Runnable targetRunnable = new ReadWriteRunnable(targetIs, clientOs);
			Thread targetThread = new Thread(targetRunnable);
			String targetThreadName = getTargetThreadName();
			if(targetThreadName!=null && targetThreadName.trim().length() > 0) {
				targetThread.setName(targetThreadName);
			}
			
			clientThread.start();
			targetThread.start();
			clientThread.join();
			targetThread.join();
			
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			close(clientSocket);
			close(targetSocket);
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

}
