/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.*;
import java.net.*;
import javax.swing.JTextArea;

/**
 *
 * @author Hp
 */
public class UDPListener implements Runnable{
    private int port;
    private int localPort;
    private DatagramSocket server;
    private byte[] receiveData;
    private JTextArea messageArea;
    private InetSocketAddress address;
    
    public UDPListener(String _address,int _port, JTextArea _messageArea){
        port = _port;
        messageArea = _messageArea;
        try{
            server = new DatagramSocket(null);
            address = new InetSocketAddress(_address,port);
            server.bind(address);  
            localPort = server.getLocalPort();
            System.out.println("Local port anda = " + localPort);
        }catch(SocketException e){
            e.printStackTrace();
        }
    }
    
    public InetSocketAddress getAddress(){
        return address;
    }
    
    public int getLocalPort(){
        return localPort;
    }
    
    @Override
    public void run(){
        while(true){    
            try{
                receiveData = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receiveData,receiveData.length);
                server.receive(packet);
                String message = new String(packet.getData(),0, packet.getLength());
                //message dapat diproses
                System.out.println(message);
                messageArea.append("<"+packet.getPort()+">       :  " + message + "\n");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}

