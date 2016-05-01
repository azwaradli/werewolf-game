/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

import Client.Messenger;
import java.util.ArrayList;

/**
 *
 * @author Adz
 */
public class Proposer {
    Messenger messenger;
    String proposerUID;
    int quorumSize;
    int proposalNumber;
    int playerId;
    int proposedValue = 0; // KPU_id
    //ProposalID proposalID;
    
    
    //messenger
    //Proposal_id
    //proposedValue = KPU_id
    
    
    public void Proposer(Messenger messenger, String proposerUID, String proposal_id){
        this.proposerUID = proposerUID;
        this.messenger = messenger;
        
    }
    
    public void setProposal(Object obj){
        
    }
    
}
