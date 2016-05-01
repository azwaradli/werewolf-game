/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

import Client.Messenger;
import Client.UDPListener;

/**
 *
 * @author Adz
 */
public class PaxosController {
    // attributes
    int playerId;
    int pidTerbesar;
    int pidTerbesarKedua;
    int role;
    
    Messenger messenger;
    UDPListener listen;
    
    public PaxosController(int playerId, int pidTerbesar, int pidTerbesarKedua, String address, int port){
        this.playerId = playerId;
        this.pidTerbesar = pidTerbesar;
        this.pidTerbesarKedua = pidTerbesarKedua;
        messenger = new Messenger();
        listen = new UDPListener(address, port);
    }
    
    public void decideRole(){
        if(playerId == pidTerbesar || playerId == pidTerbesarKedua)
            role = 1;
        else
            role = 0;
    }
    
    public void runPaxos(){
        if(role == 1)
            runProposer();
        else
            runAcceptor();
    }
    
    public void runProposer(){
        
    }
    
    public void runAcceptor(){
        
    }
}
