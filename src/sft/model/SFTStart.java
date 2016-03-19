package sft.model;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SFTStart {
	public static boolean scanPort_DOS(String host, int port) throws UnknownHostException {
		boolean flag = false;
		InetAddress theAddress = InetAddress.getByName(host);
		Socket socket = null;
		try {
			socket = new Socket(theAddress, port);
			socket.close();
			flag = true;
		} catch (Exception localException) {
		}
		return flag;
	}
}
