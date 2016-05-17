/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.StandardMessage;
import Paxos.Acceptor;
import Paxos.ProposalID;
import Paxos.Proposer;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Hp
 */
public class UDPListener implements Runnable{
    private int port;
    private int localPort;
    private DatagramSocket server;
    private byte[] receiveData;
//    private JTextArea messageArea;
    private InetSocketAddress address;
    private int playerId;
    private ArrayList<ArrayList<Integer>> voteResult;
    
    private int werewolfCount;
    private int civilianCount;
    
    private int count;
    
    Acceptor acceptor;
    Proposer proposer;
    
//    public UDPListener(String _address,int _port, JTextArea _messageArea){
//        port = _port;
//        messageArea = _messageArea;
//        try{
//            server = new DatagramSocket(null);
//            address = new InetSocketAddress(_address,port);
//            server.bind(address);  
//            localPort = server.getLocalPort();
//            System.out.println("Local port anda = " + localPort);
//        }catch(SocketException e){
//            e.printStackTrace();
//        }
//    }
    
    public ArrayList<ArrayList<Integer>> getVoteResults(){
        return voteResult;
    }
    
    public void resetCount(){
        count = 0;
    }
    
    public void resetVoteResults(){
        voteResult = new ArrayList<ArrayList<Integer>>();
    }
    
    public int getWerewolfCount(){
        return werewolfCount;
    }
    
    public int getCivilianCount(){
        return civilianCount;
    }
    
    public void setWerewolfCount(int n){
        werewolfCount = n;
    }
    
    public void setCivilianCount(int n){
        civilianCount = n;
    }
    
    public UDPListener(String _address,int _port, Messenger messenger){
        port = _port;
        count = 0;
        playerId = 0;
        werewolfCount = 2;
        try{
            server = new DatagramSocket(null);
            address = new InetSocketAddress(_address,port);
            server.bind(address);  
            localPort = server.getLocalPort();
            System.out.println("Local port anda = " + localPort);
        }catch(SocketException e){
            e.printStackTrace();
        }
        acceptor = new Acceptor(messenger);
        voteResult = new ArrayList<>();
    }
    
    @Override
    public void run(){
        JSONParser parser = new JSONParser();
        JSONObject json;
        while(true){    
            try{
                receiveData = new byte[1024];
                DatagramPacket packet = new DatagramPacket(receiveData,receiveData.length);
                server.receive(packet);
                String message = new String(packet.getData(),0, packet.getLength());
                //message dapat diproses
                System.out.println("Client :: Receive :: "+message);
//                messageArea.append("<"+packet.getPort()+">       :  " + message + "\n");
                
                try {
                    Object obj = parser.parse(message);
                    json = (JSONObject)obj;
                    
                    if(json.containsKey(StandardMessage.MESSAGE_METHOD)){
                        String method = json.get(StandardMessage.MESSAGE_METHOD).toString();
                        if(method.equals(StandardMessage.PARAM_PREPARE_PROPOSAL)){ // terima prepare proposal
                            JSONArray proposalId = (JSONArray) json.get(StandardMessage.MESSAGE_PROPOSAL_ID);
                            int proposalNumber = Integer.parseInt(proposalId.get(0).toString());
                            int proposerId = Integer.parseInt(proposalId.get(1).toString());
                            acceptor.receivePrepare(proposalNumber, proposerId);
                            // kirim prepare berupa atribute proposer
                        }
                        else if(method.equals(StandardMessage.PARAM_ACCEPT_PROPOSAL)){
                            JSONArray proposalId = (JSONArray) json.get(StandardMessage.MESSAGE_PROPOSAL_ID);
                            int proposalNumber = Integer.parseInt(proposalId.get(0).toString());
                            int proposerId = Integer.parseInt(proposalId.get(1).toString());
                            int kpuId = Integer.parseInt(json.get(StandardMessage.MESSAGE_KPU_ID).toString());
                            acceptor.receiveAccept(proposerId, new ProposalID(proposalNumber, proposerId), kpuId);
                            // kpuId = proposer yang terpilih
                        }
                        else if(method.equals(StandardMessage.PARAM_VOTE_WEREWOLF)){
                            int victimId = Integer.parseInt(json.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                            for(int i = 0; i < voteResult.size(); i++){
                                if(voteResult.get(i).get(0).equals(victimId)){
                                    int count = voteResult.get(i).get(1);
                                    count++;
                                    werewolfCount--;
                                    voteResult.get(i).set(1, count);
                                    break;
                                }
                            }
                            System.out.println("Client :: "+werewolfCount+" more vote to go");
                        }
                        else if(method.equals(StandardMessage.PARAM_VOTE_CIVILIAN)){
                            int victimId = Integer.parseInt(json.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                            for(int i = 0; i < voteResult.size(); i++){
                                if(voteResult.get(i).get(0).equals(victimId)){
                                    int count = voteResult.get(i).get(1);
                                    count++;
                                    civilianCount--;
                                    voteResult.get(i).set(1, count);
                                    break;
                                }
                            }
                            System.out.println("Client :: "+civilianCount+" more vote to go");
                        }
                        
                        
                    }
                    else if(json.containsKey(StandardMessage.MESSAGE_STATUS)){
                        String status = json.get(StandardMessage.MESSAGE_STATUS).toString();
                        if(status.equals(StandardMessage.PARAM_OK)){
                            if(json.containsKey(StandardMessage.MESSAGE_PREVIOUS_ACCEPTED)){
                                int prevAcceptedValue = Integer.parseInt(json.get(StandardMessage.MESSAGE_PREVIOUS_ACCEPTED).toString());
                                //proposer.receivePromise(playerId, proposalID, prevAcceptedID, prevAcceptedValue);
                                proposer.receivePromise(prevAcceptedValue);
                            }
                            else{
                                proposer.receivePromise();
                            }
                        }
                        else if(status.equals(StandardMessage.PARAM_FAIL)){
                            if(count==0){
                               proposer.sendCheckToServer();
                               count++;
                            }
                        }
                    }
                    
                } catch (ParseException ex) {
                    Logger.getLogger(UDPListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public InetSocketAddress getAddress(){
        return address;
    }
    
    public int getLocalPort(){
        return localPort;
    }
    
    public void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    
    public void setProposer(Proposer proposer){
        this.proposer = proposer;
    }
    
    public void setVoteResult(ArrayList<JSONObject> allClients){
        for(int i = 0; i < allClients.size(); i++){
            ArrayList<Integer> client = new ArrayList<>();
            client.add(Integer.parseInt(allClients.get(i).get("player_id").toString()));
            client.add(0);
            voteResult.add(client);
        }
    }
    
}

