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
public class UDPSender {
    BufferedReader userInput;
    DatagramSocket client;
    InetAddress IPAddress;
    byte[] sendData;
    String message;
    DatagramPacket packet;
    
    UDPSender(String IP, int port){
        
        userInput = new BufferedReader(new InputStreamReader(System.in));
        try{
            client = new DatagramSocket();
            IPAddress = InetAddress.getByName(IP);
            sendData = new byte[1024];
            message = userInput.readLine();
            sendData = message.getBytes();
            packet = new DatagramPacket(sendData,sendData.length,IPAddress,port);
            client.send(packet);
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
