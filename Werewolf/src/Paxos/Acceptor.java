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
    private int prevAcceptedValue; // ignore sementara
    private int prevAcceptedId;
    private int prevAcceptedProposalNum;
    
    public Acceptor(Messenger _messenger){
        messenger = _messenger;
        prevAcceptedId = -1;
        prevAcceptedProposalNum = -1;
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
    
    public void receivePrepare(int proposalNumber, int proposerId) throws IOException{   
        /*if(promisedID == null || proposalID.getID() > promisedID.getID()){
            promisedID = proposalID;
        }
        messenger.sendPromise(proposerId,prevAcceptedValue,acceptedValue);*/
        if(prevAcceptedProposalNum == -1){
            prevAcceptedProposalNum = proposalNumber;
            prevAcceptedId = proposerId;
            promisedID = new ProposalID(proposalNumber, proposerId);
            messenger.sendPromise(proposerId); // without previousAcceptedValue
        }
        else{
            if(prevAcceptedProposalNum > proposalNumber){
                messenger.sendRejected(proposerId);
            }
            else if(prevAcceptedProposalNum < proposalNumber){
                messenger.sendPromise(proposerId, prevAcceptedId);
                prevAcceptedProposalNum = proposalNumber;
                prevAcceptedId = proposerId;
                promisedID = new ProposalID(proposalNumber, proposerId);
            }
            else{ // prevAcceptedNumber == proposalNumber
                if(prevAcceptedId > proposerId){
                    messenger.sendRejected(proposerId);
                }
                else if(prevAcceptedId < proposerId){
                    messenger.sendPromise(proposerId, prevAcceptedId);
                    prevAcceptedProposalNum = proposalNumber;
                    prevAcceptedId = proposerId;
                    promisedID = new ProposalID(proposalNumber, proposerId);
                }
                else{
                    messenger.sendError(proposerId);
                }
            }
        }
    }
    
    public void receiveAccept(int senderID,ProposalID proposalID,int value) throws IOException{
        if (promisedID == null || proposalID.getID() >= promisedID.getID() /*|| proposalID.equals(promisedID)*/) {
            promisedID    = proposalID;
            acceptedID    = proposalID;
            prevAcceptedValue = acceptedValue;
            acceptedValue = value;
            try{
                messenger.sendAccepted(acceptedID,acceptedValue);
                System.out.println("proposal accepted");
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    
}
