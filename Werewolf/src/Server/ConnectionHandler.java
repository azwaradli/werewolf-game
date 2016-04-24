/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.io.*;
import java.net.*;

/**
 *
 * @author Hp
 */
public class ConnectionHandler implements Runnable{
    private Socket server;
    
    public ConnectionHandler(Socket _server){
        this.server = _server;
    }
    
    @Override
    public void run(){
        try{
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            DataInputStream in = new DataInputStream(server.getInputStream());
        
            Boolean end = false;
            while(!end){
                
            
            }
            server.close();
        }catch(IOException e){
            
        }
    }
}
