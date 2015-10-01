package testServer;

import java.net.*;
import java.util.Scanner;

class TestServeur {
	public static void main(String args[]) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		try {
			String requestData = "ListerMatch";
			byte[] m = requestData.getBytes();
			DatagramPacket request = new DatagramPacket(m, requestData.length(), InetAddress.getByName("localhost"), 8080);
			socket.send(request);

			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			String data = new String(reply.getData());
			data = data.trim();
			System.out.println(data);

			Scanner sc = new Scanner(System.in);
			while (!requestData.equals("MiseAJour~")) {
				
				requestData = sc.nextLine();
				requestData = "MiseAJour~" + requestData;
				m = requestData.getBytes();
				request = new DatagramPacket(m, requestData.length(),
						InetAddress.getByName("localhost"), 8080);
				socket.send(request);

				buffer = new byte[1000];
				reply = new DatagramPacket(buffer, buffer.length);
				socket.receive(reply);
				data = new String(reply.getData());
				data = data.trim();
				System.out.println(data);
				Thread.sleep(5000);
			}
			
			sc.close();

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
}