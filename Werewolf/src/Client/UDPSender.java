/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;
import java.io.*;
import java.net.*;
import java.util.Random;
/**
 *
 * @author Hp
 */
public class UDPSender implements Runnable{
    BufferedReader userInput;
    DatagramSocket client;
    InetAddress IPAddress;
    byte[] sendData;
    String message;
    DatagramPacket packet;
    String IP;
    Integer port;
    
    UDPSender(String _IP, Integer _port, String _message){
        IP = _IP;
        port =_port;
        message = _message;
    }
    
    @Override
    public void run(){
        userInput = new BufferedReader(new InputStreamReader(System.in));
        try{
            client = new DatagramSocket();
            IPAddress = InetAddress.getByName(IP);
            sendData = new byte[1024];
            sendData = message.getBytes();
            packet = new DatagramPacket(sendData,sendData.length,IPAddress,port);
            
            Random random = new Random();
            double rand = random.nextDouble();
            if(rand < 0.85)
                client.send(packet);
            else
                System.out.println("paket anda tidak terkirim");
            client.close();
        }catch(SocketException e){
            e.printStackTrace();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
