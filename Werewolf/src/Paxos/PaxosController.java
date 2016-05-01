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
    int numClient;
    int role;
    
    Messenger messenger;
    UDPListener listen;
    
    public PaxosController(int playerId, int numClient, String address, int port){
        this.playerId = playerId;
        this.numClient = numClient;
        messenger = new Messenger();
        listen = new UDPListener(address, port);
    }
    
    public void decideRole(){
        if(numClient - playerId <= 1)
            role = 1; // proposer
        else
            role = 0; // acceptor
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
