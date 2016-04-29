/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.*;
import java.net.*;

/**
 *
 * @author Hp
 */
public class UDPListener implements Runnable{
    private int port;
    private int localPort;
    private DatagramSocket server;
    private byte[] receiveData;
    
    public UDPListener(String _address,int _port){
        port = _port;
        try{
            server = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(_address,port);
            server.bind(address);  
            localPort = server.getLocalPort();
            System.out.println("Local port anda = " + localPort);
        }catch(SocketException e){
            e.printStackTrace();
        }
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
                String message = new String(packet.getData());
                //message dapat diproses
                System.out.println(message);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}

