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
    private int acceptedValue;
    private int prevAcceptedValue;
    
    public Acceptor(Messenger _messenger){
        messenger = _messenger;
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
    
    public void receivePrepare(int senderID, ProposalID proposalID) throws IOException{   
        if(promisedID ==null || proposalID.getID() > promisedID.getID()){
            promisedID = proposalID;
        }
        messenger.sendPromise(senderID,prevAcceptedValue,acceptedValue);
    }
    
    public void receiveAccept(int senderID,ProposalID proposalID,int value) throws IOException{
        if (promisedID == null || proposalID.getID() > promisedID.getID() || proposalID.equals(promisedID)) {
            promisedID    = proposalID;
            acceptedID    = proposalID;
            prevAcceptedValue = acceptedValue;
            acceptedValue = value;
            try{
            messenger.sendAccepted(acceptedID,acceptedValue);
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    
}
