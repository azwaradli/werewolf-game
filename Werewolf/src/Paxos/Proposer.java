/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

/**
 *
 * @author Adz
 */
public class Proposer {
    String proposerUID;
    int quorumSize;
    String proposal_id; // [<proposal-number>, <player-id>]
    
    //messenger
    //Proposal_id
    //proposedValue = KPU_id
    
    
    public void Proposer(String proposerUID, String proposal_id){
        this.proposerUID = proposerUID;
        this.proposal_id = proposal_id;
    }
    
    public void setProposal(Object obj){
        
    }
    
}
