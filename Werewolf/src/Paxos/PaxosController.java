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
    Proposer proposer;
    
    public PaxosController(int _playerId, TCPConnection connection){
        playerId = _playerId;
        pidTerbesar = connection.getBiggestPID();
        pidTerbesarKedua = connection.getSecondBiggest();
        messenger = new Messenger(connection);
        proposer = new Proposer(messenger, playerId);
    }
    
    public void run(){
        decideRole();
        runPaxos();
    }
    
    public void decideRole(){
        System.out.println(pidTerbesar + " " + pidTerbesarKedua);
        if(playerId == pidTerbesar || playerId == pidTerbesarKedua){
            role = 1;
            System.out.println(playerId+ " sebagai proposer");}
        else{
            role = 0;
            System.out.println(playerId + " sebagai acceptor");}
    }
    
    public void runPaxos(){
        if(role == 1)
            runProposer();        
    }
    
    public void runProposer(){
        proposer.setProposal(playerId);
        proposer.prepare();
    }
}
