/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.StandardMessage;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Adz
 */
public class ClientProtocol {
    
    public JSONObject joinGameMessage(String username, String udpAddress, int udpPort){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_JOIN);
        data.put(StandardMessage.MESSAGE_USERNAME, username);
        data.put(StandardMessage.MESSAGE_UDP_ADDRESS, udpAddress);
        data.put(StandardMessage.MESSAGE_UDP_PORT, udpPort);
        
        return data;
    }
    
    public JSONObject leaveGameMessage(){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_LEAVE);
        
        return data;
    }
    
    public JSONObject readyUpMessage(){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_READY);
        
        return data;
    }
    
    public JSONObject sendPromise(int prevAcceptedValue){
        JSONObject data = new JSONObject();
        data.put(StandardMessage.MESSAGE_STATUS, StandardMessage.PARAM_OK);
        data.put(StandardMessage.MESSAGE_DESCRIPTION, StandardMessage.PARAM_ACCEPTED);
        data.put(StandardMessage.MESSAGE_PREVIOUS_ACCEPTED, prevAcceptedValue);
        return data;
    }
    
    public JSONObject sendAccepted(int acceptedValue){
        JSONObject data = new JSONObject();
        data.put(StandardMessage.MESSAGE_METHOD,StandardMessage.PARAM_ACCEPTED_PROPOSAL);
        data.put(StandardMessage.MESSAGE_KPU_ID,acceptedValue);
        data.put(StandardMessage.MESSAGE_DESCRIPTION,StandardMessage.PARAM_ACCEPTED_PROPOSAL);
        return data;
    }
    
    public JSONObject confirmAccepted(){
        JSONObject data = new JSONObject();
        data.put(StandardMessage.MESSAGE_STATUS,StandardMessage.PARAM_OK);
        data.put(StandardMessage.MESSAGE_DESCRIPTION,StandardMessage.PARAM_ACCEPTED);
        return data;
    }
    
    public JSONObject listClientMessage(){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_CLIENT_ADDRESS);
        
        return data;
    }
    
    public JSONObject prepareProposalMessage(int proposalNumber, int playerId){
        JSONObject data = new JSONObject();
        
        JSONArray proposalId = new JSONArray();
        proposalId.add(proposalNumber);
        proposalId.add(playerId);
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_PREPARE_PROPOSAL);
        data.put(StandardMessage.MESSAGE_PROPOSAL_ID, proposalId);
        
        return data;
    }
    
    public JSONObject acceptProposalMessage(int proposalNumber, int playerId, int proposedValue){
        JSONObject data = new JSONObject();
        
        JSONArray proposalId = new JSONArray();
        proposalId.add(proposalNumber);
        proposalId.add(playerId);
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_ACCEPT_PROPOSAL);
        data.put(StandardMessage.MESSAGE_PROPOSAL_ID, proposalId);
        data.put(StandardMessage.MESSAGE_KPU_ID, Integer.toString(proposedValue));
        
        return data;
    }
    
    public JSONObject acceptedProposalMessage(int playerId){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_ACCEPTED_PROPOSAL);
        data.put(StandardMessage.MESSAGE_KPU_ID, Integer.toString(playerId));
        data.put(StandardMessage.MESSAGE_DESCRIPTION, "KPU is selected");
        
        return data;
    }
    
    public JSONObject killWerewolfVoteMessage(int playerId){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_WEREWOLF);
        data.put(StandardMessage.MESSAGE_PLAYER_ID, playerId);
        
        return data;
    }
    
    public JSONObject infoWerewolfKilledMessage(int voteStatus, int playerKilled, ArrayList<ArrayList<Integer>> voteResult){
        JSONObject data = new JSONObject();
        
        if(voteStatus == 1){
            data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_RESULT_WEREWOLF);
            data.put(StandardMessage.MESSAGE_VOTE_STATUS, voteStatus);
            data.put(StandardMessage.MESSAGE_PLAYER_KILLED, playerKilled);
            data.put(StandardMessage.MESSAGE_VOTE_RESULT, voteResult);
        }
        else if(voteStatus == -1){
            data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_RESULT);
            data.put(StandardMessage.MESSAGE_VOTE_STATUS, voteStatus);
            data.put(StandardMessage.MESSAGE_VOTE_RESULT, voteResult);
        }
        
        return data;
    }
    
    public JSONObject killCivilianVoteMessage(int playerId){
        JSONObject data = new JSONObject();
        
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_CIVILIAN);
        data.put(StandardMessage.MESSAGE_PLAYER_ID, playerId);
        
        return data;
    }

    public JSONObject infoCivilianKilledMessage(int voteStatus, int playerKilled, ArrayList<ArrayList<Integer>> voteResult){
        JSONObject data = new JSONObject();
        
        if(voteStatus == 1){
            data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_RESULT_WEREWOLF);
            data.put(StandardMessage.MESSAGE_VOTE_STATUS, voteStatus);
            data.put(StandardMessage.MESSAGE_PLAYER_KILLED, playerKilled);
            data.put(StandardMessage.MESSAGE_VOTE_RESULT, voteResult);
        }
        else if(voteStatus == -1){
            data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_VOTE_RESULT);
            data.put(StandardMessage.MESSAGE_VOTE_STATUS, voteStatus);
            data.put(StandardMessage.MESSAGE_VOTE_RESULT, voteResult);
        }
        
        return data;
    }
           
}
