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
    TCPConnection connection;
    int role;
    
    Messenger messenger;
    Proposer proposer;
    
    public PaxosController(int _playerId, TCPConnection _connection){
        playerId = _playerId;
        connection = _connection;
        pidTerbesar = connection.getBiggestPID();
        pidTerbesarKedua = connection.getSecondBiggest();
        messenger = new Messenger(connection);
        proposer = new Proposer(messenger, playerId);
    }
    
    public Proposer getProposer(){
        return proposer;
    }
    
    public void run(){
        proposer.setQuorum((connection.getListPlayers().size()-2)/2);
        System.out.println("Jumlah Pemain : "+ connection.getListPlayers().size());
        System.out.println("Quorum : "+(connection.getListPlayers().size()-2)/2);
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
