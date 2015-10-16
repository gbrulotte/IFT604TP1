package com.example.ift604.hockeyapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by P-O on 2015-10-12.
 */
public class UDPHelper {

    DatagramSocket _socket;
    InetAddress _ipAddress;
    int _port;

    public UDPHelper(String ipAddress, int port) throws SocketException, UnknownHostException {
        _port = port;
        _socket = new DatagramSocket();
        _ipAddress = InetAddress.getByName(ipAddress);
    }

    public String sendAndReceive(String toSend) {
        String toReturn = null;

        if (send(toSend)) {
            toReturn = receive();
        }

        _socket.close();

        return toReturn.trim();
    }

    private boolean send(String data) {
        try {
            byte[] sendData = data.getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, _ipAddress, _port);
            _socket.send(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String receive() {
        String data = null;

        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            _socket.receive(packet);
            data = new String (packet.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
