package com.hx.proxy;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleRunnable implements Runnable {
	private Closeable c;

	public ConsoleRunnable(Closeable c) {
		this.c = c;
	}

	@Override
	public void run() {
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			boolean shutdown = false;
			isr = new InputStreamReader(System.in);
			br = new BufferedReader(isr);
			while(!shutdown) {
				String cmmand = br.readLine();
				if(cmmand.trim().equalsIgnoreCase("shutdown")) {
					shutdown = true;
					c.close();
				}else {
					System.out.println("unknow command:" + cmmand);
					System.out.println("accpet command:shutdown");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(isr);
			close(br);
		}
	}
	
	private static void close(Closeable c) {
		if(c!=null) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
