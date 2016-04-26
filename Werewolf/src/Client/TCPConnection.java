/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.net.*;
import java.io.*;

/**
 *
 * @author Hp
 */
public class TCPConnection implements Runnable{
    public String serverAddress;
    public int port;
    public ClientProtocol clientProtocol;
    
    public TCPConnection(String _serverAddress, int _port){
        serverAddress = _serverAddress;
        port = _port;
        clientProtocol = new ClientProtocol();
    }
    @Override
    public void run(){
        // Make connection and initialize stream
        try{
            Socket socket = new Socket(serverAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(clientProtocol.joinGameMessage("tes")); // testing send message
            
            // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            System.out.println("TCP "+line);
//            if (line.startsWith("SUBMITNAME")) {
//                out.println(getName());
//            } else if (line.startsWith("NAMEACCEPTED")) {
//                textField.setEditable(true);
//            } else if (line.startsWith("MESSAGE")) {
//                messageArea.append(line.substring(8) + "\n");
//            } else if(line.startsWith("REFRESHLISTPLAYERS")){
//                playerList.setText("");
//            }else if(line.startsWith("LISTPLAYERS")){
//                playerList.append(line.substring(12) + "\n");
//            }
        }
        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
}
