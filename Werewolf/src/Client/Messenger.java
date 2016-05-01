/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Paxos.ProposalID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Adz
 */
public class Messenger {
    ClientProtocol clientProtocol;
    UDPSender udpSender;
    String message;
    
    public Messenger(){
        clientProtocol = new ClientProtocol();
        message = new String();
    }
    
    public void prepareProposal(ProposalID proposalID){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.prepareProposalMessage(proposalID.getID(), proposalID.getPlayerID());
        message = obj.toString();
        
        sendToAll();
    }
    
    public void acceptProposal(ProposalID proposalID, int proposedValue){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.acceptProposalMessage(proposalID.getID(), proposalID.getPlayerID(), proposedValue);
        message = obj.toString();
    }
    
    public void killWerewolfVote(int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killWerewolfVoteMessage(playerId);
        message = obj.toString();
    }
    
    
    public void sendPromise(int proposerUID, int prevAcceptedValue, int acceptedValue){
        JSONObject obj = new JSONObject();
        obj.put("status", "ok");
        obj.put("description", "accepted");
        obj.put("previous_accepted", prevAcceptedValue);
    
        message = obj.toString();
        sendToAll();
    }
    
    public void killCivilianVote(int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killCivilianVoteMessage(playerId);
        message = obj.toString();
    }
    
    public String getMessage(){
        return message;
    }
    
    
    public void sendToAll(){
        
    }
    
    public void sendToOne(String IPAddress, int port){
        UDPSender udpSender = new UDPSender(IPAddress, port, message);
        Thread t3 = new Thread(udpSender);
        t3.start();
    }
}
