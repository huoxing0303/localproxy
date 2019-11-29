package com.hx.proxy;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class ReadWriteRunnable implements Runnable {
	private InputStream is;
	private OutputStream os;

	public ReadWriteRunnable(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	@Override
	public void run() {
		byte[] b = new byte[32768];
		int readLen = 0;
		boolean shutdown = false;
		while (!shutdown) {
			try {
				readLen = is.read(b);
				if (readLen == -1) {
					// will cause related input stream get SocketExceptin
					close(os);
					break;
				}
				os.write(b, 0, readLen);
			} catch (Exception e) {
				shutdown = true;
				close(os);// notify opposite input stream read count = -1
				if (!(e instanceof SocketException)) {
					System.out.print("[" + Thread.currentThread().getName() + "]");
					e.printStackTrace();
				}
			}
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

}
