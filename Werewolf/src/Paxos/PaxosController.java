/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

import Client.Messenger;
import Client.TCPConnection;
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
    Proposer proposer;
    
    public PaxosController(int playerId, int pidTerbesar, int pidTerbesarKedua, String address, int port, TCPConnection _connection){
        this.playerId = playerId;
        this.pidTerbesar = pidTerbesar;
        this.pidTerbesarKedua = pidTerbesarKedua;
        messenger = new Messenger(_connection);
        listen = new UDPListener(address, port);
        proposer = new Proposer(messenger, playerId);
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
        proposer.setProposal(playerId);
        proposer.prepare();
    }
    
    public void runAcceptor(){
        
    }
}
