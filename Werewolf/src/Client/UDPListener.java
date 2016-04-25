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
public class UDPListener {
    private int port;
    private DatagramSocket server;
    private byte[] receiveData;
    
    public UDPListener(int _port){
        port = _port;
        receiveData = new byte[1024];
        try{
            server = new DatagramSocket(port);
        }catch(SocketException e){
            e.printStackTrace();
        }
        while(true){
            DatagramPacket packet = new DatagramPacket(receiveData,receiveData.length);
            try{
                server.receive(packet);
            }catch(IOException e){
                e.printStackTrace();
            }
            String message = new String(packet.getData());
            //message dapat diproses
        }
    }
}

