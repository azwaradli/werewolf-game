/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;
import Client.*;
import java.io.IOException;

/**
 *
 * @author Hp
 */
public class Acceptor {
    private Messenger messenger;
    private ProposalID promisedID;
    private ProposalID acceptedID;
    private TCPConnection connection;
    private int acceptedValue;
    private int prevAcceptedValue;
    
    public Acceptor(TCPConnection _connection){
        connection = _connection;
    }
    
    public ProposalID getPromisedID(){
        return promisedID;
    }
    
    public ProposalID getAcceptedID(){
        return acceptedID;
    }
    
    public int getAcceptedValue(){
        return acceptedValue;
    }
    
    public int getPrevAcceptedValue(){
        return prevAcceptedValue;
    }
    
    public void receivePrepare() throws IOException{
    
    }
    
}
