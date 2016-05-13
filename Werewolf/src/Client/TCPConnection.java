/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import Client.UDPListener;
import Model.StandardMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Hp
 */
public class TCPConnection implements Runnable{
    public String serverAddress;
    public int port;
    public int kpuID;
    public ClientProtocol clientProtocol;
    public int localPort;
    private int biggestPID;
    private int secondBiggestPID;
    private int day;
    private ArrayList<JSONObject> AllClients;
    private String phase;
    
    private int werewolfCount;
    private int civilianCount;
    
    private String state = "";
    private boolean dataReady = false;
    
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    
    /* atribut player di client */
    int player_id;
    boolean is_alive;
    String time, role;
    ArrayList<String> friend;
    
    public TCPConnection(String _serverAddress, int _port){
        is_alive = true;
        serverAddress = _serverAddress;
        kpuID = 0;
        port = _port;
        day = 0;
        clientProtocol = new ClientProtocol();
        AllClients = new ArrayList<JSONObject>();
        time="";
        phase = "day";
        role ="civilian";
    }
    
    public int getKpuID(){
        return kpuID;
    } 
    
    public int getLocalPort(){
        return localPort;
    }
    
    public String getPhase(){
        return phase;
    }
    
    public boolean isAlive(){
        return is_alive;
    }
    
    public String getAddress(){
        return serverAddress;
    }
    
    public int getPlayerId(){
        return player_id;
    }
    
    public String getTime(){
        return time;
    }
    
    public void setDataReady(boolean ready){
        dataReady = ready;
    }
    
    public boolean isStarted(){
        return state.equals("START");
    }
    
    public int getWerewolfCount(){
        return werewolfCount;
    }
    
    public int getCivilianCount(){
        return civilianCount;
    }
    
    public boolean isEnded(){
        return state.equals("END");
    }
    
    public boolean isReady(){
        return dataReady;
    }
    
    public String getRole(){
        return role;
    }
    
    public ArrayList<String> getFriend(){
        return friend;
    }
    
    public ArrayList<JSONObject> getListPlayers(){
        return AllClients;
    }
    
    public int getBiggestPID(){
        return biggestPID;
    }
    
    public int getSecondBiggest(){
        return secondBiggestPID;
    }
    
    public boolean isPlayerExist(int pid){
        boolean ret = false;
        for(int i = 0;i<AllClients.size();i++){
            int client = Integer.parseInt(AllClients.get(i).get("player_id").toString());
            if(client == pid){
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public String listClients(){
        String ret = "";
        for(int i = 0;i<AllClients.size();i++){
            if(AllClients.get(i).get("is_alive").toString().equals("1")){
                String client_id = AllClients.get(i).get("player_id").toString();
                String player_name = AllClients.get(i).get("username").toString();
                ret = ret + client_id + " " + player_name + "\n";
            }
        }
        return ret;
    }
    
    @Override
    public void run(){
        // Make connection and initialize stream
        try{
            socket = new Socket(serverAddress, port);
            localPort = socket.getLocalPort();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            String getjson;
            JSONObject json;
            JSONParser parser = new JSONParser();
            
            // Process all messages from server, according to the protocol.
            while (true) {
                getjson = in.readLine();
                System.out.println("TCP "+getjson);
                
                if(getjson!=null){
                    try {
                        Object obj = parser.parse(getjson);
                        json = (JSONObject)obj;

                        if(json.containsKey(StandardMessage.MESSAGE_STATUS)){
                            if(json.get(StandardMessage.MESSAGE_STATUS).equals(StandardMessage.PARAM_OK)){
//                                System.out.println("Client :: Status :: OK");

                                if(json.containsKey(StandardMessage.MESSAGE_PLAYER_ID)){
                                    String playerid = json.get(StandardMessage.MESSAGE_PLAYER_ID).toString();
//                                    System.out.println("Client :: Player ID :: "+playerid);
                                    player_id = Integer.parseInt(playerid);
                                }
                                else if(json.containsKey(StandardMessage.MESSAGE_CLIENTS)){
//                                    System.out.println("Client :: List Client");
                                    JSONArray playersInfo = (JSONArray) json.get(StandardMessage.MESSAGE_CLIENTS);

                                    ArrayList<JSONObject> tempInfo = new ArrayList<JSONObject>();
                                    JSONObject clientInfo = (JSONObject) parser.parse(playersInfo.get(0).toString());
                                    tempInfo.add(clientInfo);

                                    biggestPID = Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                                    secondBiggestPID = 0;
                                    werewolfCount = 0;
                                    civilianCount = 0;
                                    for(int i = 1; i<playersInfo.size();i++){
                                        clientInfo = (JSONObject) parser.parse(playersInfo.get(i).toString());
                                        tempInfo.add(clientInfo);
                                        
                                        if(Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ALIVE).toString())==1){
                                            if(clientInfo.get(StandardMessage.MESSAGE_ROLE).toString()==("werewolf")){
                                                werewolfCount++;
                                            }else{
                                                civilianCount++;
                                            }
                                        }
                                        int temp = Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                                        if(temp==player_id){
                                            if(Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ALIVE).toString())==0){
                                                is_alive = false;
                                            }
                                            else{
                                                is_alive = true;
                                            }
                                        }
                                        
                                        if(temp > biggestPID){
                                            biggestPID = temp;
                                        }
                                        
                                    }
                                    for(int i = 0; i<playersInfo.size();i++){
                                        clientInfo = (JSONObject) parser.parse(playersInfo.get(i).toString());
                                        int temp = Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                                        if((temp > secondBiggestPID)&&(temp < biggestPID)){
                                            secondBiggestPID = temp;
                                        }
                                    }
                                    AllClients = tempInfo;
                                    dataReady = true;
//                                    System.out.println(AllClients +" " + biggestPID+ " "+secondBiggestPID);
                                }
                                else if(json.containsValue(StandardMessage.KPU_SELECTED)){
                                    kpuID = Integer.parseInt(json.get(StandardMessage.KPU_ID).toString());
                                }
                                else if(json.containsValue("thanks for playing")){
                                    state = "END";
                                    break;
                                }
                            }
                        }
                        else if(json.containsKey(StandardMessage.MESSAGE_METHOD)){
                            String method = json.get(StandardMessage.MESSAGE_METHOD).toString();
                            if(method.equals(StandardMessage.PARAM_START)){
                                time = json.get(StandardMessage.MESSAGE_TIME).toString();
                                role = json.get(StandardMessage.MESSAGE_ROLE).toString();
                                System.out.println("Client :: Start Game");
                                if(json.containsKey(StandardMessage.MESSAGE_FRIEND)){
                                    JSONArray jarray = new JSONArray();
                                    jarray = (JSONArray) json.get(StandardMessage.MESSAGE_FRIEND);
                                    friend = new ArrayList<String>();
                                    for(int i = 0; i<jarray.size();i++){
                                        friend.add(jarray.get(i).toString());
                                    }
                                    System.out.println(friend);
                                }
                                state = "START";
                                out.println(clientProtocol.statusOK().toString());
                            }
                            else if(method.equals(StandardMessage.PARAM_CHANGE_PHASE)){
                                time = json.get(StandardMessage.MESSAGE_TIME).toString();
                                day = Integer.parseInt(json.get(StandardMessage.MESSAGE_DAYS).toString());
                                System.out.println("Client :: Change Phase");
                                out.println(clientProtocol.statusOK().toString());
                            }
                            else if(method.equals(StandardMessage.PARAM_VOTE_NOW)){
                                phase = json.get(StandardMessage.MESSAGE_PHASE).toString();
                                out.println(clientProtocol.statusOK().toString());
                                dataReady=true;
                            }
                        }
                        else{
                            System.out.println("Client :: Status :: "+json.get(StandardMessage.MESSAGE_STATUS).toString());
                            System.out.println("Client :: Description :: "+json.get(StandardMessage.MESSAGE_DESCRIPTION).toString());
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(TCPConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            socket.close();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void joinGame(int udpPort){
        Scanner sc = new Scanner(System.in);
        System.out.print("Insert username: ");
        String username = sc.nextLine();
        out.println(clientProtocol.joinGameMessage(username, serverAddress, udpPort)); // testing send message
    }
    
    public void leaveGame(){
        out.println(clientProtocol.leaveGameMessage());
    }
    
    public void readyUp(){
        out.println(clientProtocol.readyUpMessage());
    }
    
    public void listClient(){
        out.println(clientProtocol.listClientMessage());
    }
    
    public void infoWerewolfKilled(ArrayList<ArrayList<Integer>> voteResult, UDPListener udpListener){
        out.println(clientProtocol.infoWerewolfKilledMessage(voteResult));
        udpListener.resetVoteResults();
    }
    
    public void infoCivilianKilled(ArrayList<ArrayList<Integer>> voteResult, UDPListener udpListener){
        out.println(clientProtocol.infoCivilianKilledMessage(voteResult));
        udpListener.resetVoteResults();
    }
    
    public void acceptedProposal(){
        out.println(clientProtocol.acceptedProposalMessage(player_id));
    }
    
    public boolean sendAccepted(String message){
        boolean ret = false;
        System.out.println("Client :: Sending : "+message );
        out.println(message);
        try{
            JSONParser parser = new JSONParser();
            String getjson = in.readLine();
            JSONObject json = (JSONObject) parser.parse(getjson);
            if(json.get("status").equals("ok"))
                ret = true;
        }catch(IOException e){
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }
        return true;
    }
    
    public void check(){
        out.println(clientProtocol.waitingVote());
    }
    
}
