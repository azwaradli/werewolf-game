/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

import Client.Messenger;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Adz
 */
public class Proposer {
    Messenger messenger;
    int proposerUID;
    int quorumSize;
    int proposalNumber;
    int playerId;
    int proposedValue; // KPU_id
    ProposalID proposalID;
    ProposalID lastAcceptedID;
    HashSet<Integer> promisesReceived = new HashSet<Integer>();
    int counter;
    boolean promisedSend =false;
    
    public Proposer(Messenger messenger, int proposerUID){
        this.proposerUID = proposerUID;
        this.messenger = messenger;
        proposalID = new ProposalID(0, proposerUID);
        counter = 0;
        quorumSize = 0;
        proposedValue = -1;
    }
    
    public void setProposal(int playerId){
        if(proposedValue == -1)
            proposedValue = playerId;
    }
    
    public void setQuorum(int _quorum){
        quorumSize = _quorum;
    }
    
    /*public void receivePromise(int fromUID, ProposalID proposalID, ProposalID prevAcceptedID, int prevAcceptedValue) {
        if(!proposalID.equals(this.proposalID) || promisesReceived.contains(fromUID)) 
            return;
		
        promisesReceived.add(fromUID);

        if(lastAcceptedID == null || prevAcceptedID.getID() > lastAcceptedID.getID()){
            lastAcceptedID = prevAcceptedID;
            if (prevAcceptedValue != 0)
                proposedValue = prevAcceptedValue;
        }
        if (promisesReceived.size() > quorumSize){
            if (proposedValue != 0)
                messenger.acceptProposal(this.proposalID, this.proposedValue);
        }
    }*/
    
    public void receivePromise(int prevAcceptedValue){
        counter++;
        
        if(proposedValue == -1){
            proposedValue = prevAcceptedValue;
        }

        if((!promisedSend)&&(counter >= quorumSize)){
            if(proposedValue != -1){
                System.out.println("Counter  : "+counter + " " + quorumSize);
                messenger.acceptProposal(proposalID, proposedValue);
                promisedSend = true;
            }
            else{
                messenger.sendCheck();
            }
        }
    }
    
    public void receivePromise(){
        counter++;
        if((!promisedSend)&&(counter >= quorumSize)){
            if(proposedValue != -1){
                System.out.println("Counter  : "+counter + " " + quorumSize);        
                messenger.acceptProposal(proposalID, proposedValue);
                promisedSend = true;
            }
            else{
                sendCheckToServer();
            }
        }
    }
    
    public void prepare(){
        promisedSend = false;
        promisesReceived.clear();
        proposalID.incrementID();
        messenger.prepareProposal(proposalID);
    }
    
    public void sendCheckToServer(){
        messenger.sendCheck();
    }
}
