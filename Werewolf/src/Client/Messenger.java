/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.StandardMessage;
import Paxos.ProposalID;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Adz
 */
public class Messenger {
    ClientProtocol clientProtocol;
    TCPConnection connection;
    UDPSender udpSender;
    String message;
    
    public Messenger(TCPConnection _connection){
        connection = _connection;
        clientProtocol = new ClientProtocol();
        message = new String();
    }
    
    public void prepareProposal(ProposalID proposalID){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.prepareProposalMessage(proposalID.getID(), proposalID.getPlayerID());
        message = obj.toString();
        sendToAll();
        System.out.println("Client :: Send :: prepare proposal");
    }
    
    public void sendVoteCivilian(int target){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killCivilianVoteMessage(target);
        message = obj.toString();
        sendToOne(connection.getKpuID());
    }
    
    public void sendVoteWerewolf(int target){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killWerewolfVoteMessage(target);
        message = obj.toString();
        sendToOne(connection.getKpuID());
    }
    
    public void acceptProposal(ProposalID proposalID, int proposedValue){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.acceptProposalMessage(proposalID.getID(), proposalID.getPlayerID(), proposedValue);
        message = obj.toString();
        System.out.println(message);
        sendToAll();
        connection.check();
        System.out.println("Client :: Send :: accept proposal");
    }
    
    public void killWerewolfVote(int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killWerewolfVoteMessage(playerId);
        message = obj.toString();
    }
    
    public void killCivilianVote(int playerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.killCivilianVoteMessage(playerId);
        message = obj.toString();
    }
    
    public String getMessage(){
        return message;
    }
    
    public void sendPromise(int proposerID, int prevAcceptedValue){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.sendPromiseMessage(prevAcceptedValue);
        message = obj.toString();
        sendToOne(proposerID);
        System.out.println("Client :: Send :: promise with prevAcceptedValue");
    }
    
    public void sendPromise(int proposerId) {
        JSONObject obj = new JSONObject();
        obj = clientProtocol.sendPromiseMessage();
        message = obj.toString();
        sendToOne(proposerId);
        System.out.println("Client :: Send :: promise only");
    }
    
    public void sendRejected(int proposerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.sendRejectedMessage();
        message = obj.toString();
        sendToOne(proposerId);
        System.out.println("Client :: Send :: rejected");
    }
    
    public void sendError(int proposerId){
        JSONObject obj = new JSONObject();
        obj = clientProtocol.sendErrorMessage("Error cuy");
        message = obj.toString();
        sendToOne(proposerId);
        System.out.println("Client :: Send :: error");
    }
    
    public void sendAccepted(ProposalID proposalID, int acceptedValue) throws IOException, InterruptedException {
        JSONObject obj = new JSONObject();
        obj = clientProtocol.sendAcceptedMessage(acceptedValue);
        message = obj.toString();
        if(connection.sendAccepted(message)){
            obj = clientProtocol.confirmAcceptedMessage();
            message = obj.toString();
            sendToOne(proposalID.getPlayerID());
            System.out.println("Client :: Send :: accepted");
        }
        else{
            System.out.println("Client :: Send Accepted Failes");
        }
    }
    
    public void sendToAll(){
        connection.listClient();
        while(!connection.isReady()){
            //busy waiting
            try {
                Thread.sleep(500);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        connection.setDataReady(false);
        for(JSONObject c : connection.getListPlayers()){
            int id = Integer.parseInt(c.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
            if((id!=connection.getBiggestPID())&&(id!=connection.getSecondBiggest())){
                String IPAddress = c.get(StandardMessage.MESSAGE_ADDRESS).toString();
                int port = Integer.parseInt(c.get(StandardMessage.MESSAGE_PORT).toString());
                UDPSender udpSender = new UDPSender(IPAddress, port, message);
                Thread t3 = new Thread(udpSender);
                t3.start();
            }
        }
    }
    
    public void sendToOne(int pid){
        connection.listClient();
        while(!connection.isReady()){
            //busy waiting
            try {
                Thread.sleep(500);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        connection.setDataReady(false);
        for(JSONObject c : connection.getListPlayers()){
            int id = Integer.parseInt(c.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
            if(id==pid){
                String IPAddress = c.get(StandardMessage.MESSAGE_ADDRESS).toString();
                int port = Integer.parseInt(c.get(StandardMessage.MESSAGE_PORT).toString());
                UDPSender udpSender = new UDPSender(IPAddress, port, message);
                Thread t3 = new Thread(udpSender);
                t3.start();
            }
        }
    }

}
