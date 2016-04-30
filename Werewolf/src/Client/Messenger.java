/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import org.json.simple.JSONObject;

/**
 *
 * @author Adz
 */
public class Messenger {
    ClientProtocol clientProtocol;
    String message;
    
    public Messenger(){
        clientProtocol = new ClientProtocol();
        message = new String();
    }
    
    public void prepareProposal(int proposalNumber, int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.prepareProposalMessage(proposalNumber, playerId);
        message = obj.toString();
        
    }
    
    public void acceptProposal(int proposalNumber, int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.acceptProposalMessage(proposalNumber, playerId);
        message = obj.toString();
    }
    
    public void killWerewolfVote(int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killWerewolfVoteMessage(playerId);
        message = obj.toString();
    }
    
    public String getMessage(){
        return message;
    }
}
