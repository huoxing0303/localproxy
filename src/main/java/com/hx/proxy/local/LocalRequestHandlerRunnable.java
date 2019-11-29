package com.hx.proxy.local;

import java.net.Socket;

import com.hx.proxy.RequestHandlerRunnable;

public class LocalRequestHandlerRunnable extends RequestHandlerRunnable {
	private String remoteIp;
	private String targetIp;
	private int targetPort;
	
	private int dynamicPort;
	private int count;

	public LocalRequestHandlerRunnable(String remoteIp, String targetIp, int targetPort,int dynamicPort, int count) {
		this.remoteIp = remoteIp;
		this.targetIp = targetIp;
		this.targetPort = targetPort;
		this.dynamicPort = dynamicPort;
		this.count = count;
	}

	@Override
	protected Socket getClientSocket() throws Exception {
		System.out.println("connect to "+remoteIp+":" + dynamicPort);
		return new Socket(remoteIp, dynamicPort);
	}

	@Override
	protected String getClientThreadName() {
		return "localClient" + count;
	}

	@Override
	protected Socket getTargetSocket() throws Exception {
		return new Socket(targetIp, targetPort);
	}

	@Override
	protected String getTargetThreadName() {
		return "localTarget" + count;
	}

}
