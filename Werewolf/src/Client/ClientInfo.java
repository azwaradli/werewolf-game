/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author Hp
 */
public class ClientInfo {
    private String IPAddress;
    private int port;
    
    
    
    public ClientInfo(String _IP, int _port ){
        IPAddress = _IP;
        port = _port;
    }
    
    public String getAddress(){
        return IPAddress;
    }
    
    public int getPort(){
        return port;
    }
}
