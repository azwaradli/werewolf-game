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
    public ClientProtocol clientProtocol;
    public int localPort;
    private int biggestPID;
    private int secondBiggestPID;
    private ArrayList<JSONObject> AllClients;
    
    private String state = "";
    private boolean timeChanged = false;
    private boolean dataReady = false;
    
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    
    /* atribut player di client */
    int player_id;
    String time, role;
    ArrayList<String> friend;
    
    public TCPConnection(String _serverAddress, int _port){
        serverAddress = _serverAddress;
        port = _port;
        clientProtocol = new ClientProtocol();
        AllClients = new ArrayList<JSONObject>();
    }
    
    public int getLocalPort(){
        return localPort;
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
    
    public boolean isEnded(){
        return state.equals("END");
    }
    
    public boolean isTimeChanged(){
        return timeChanged;
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
                                System.out.println("Client :: Status :: OK");

                                if(json.containsKey(StandardMessage.MESSAGE_PLAYER_ID)){
                                    String playerid = json.get(StandardMessage.MESSAGE_PLAYER_ID).toString();
                                    System.out.println("Client :: Player ID :: "+playerid);
                                    player_id = Integer.parseInt(playerid);
                                }
                                else if(json.containsKey(StandardMessage.MESSAGE_CLIENTS)){
                                    System.out.println("Client :: List Client");
                                    JSONArray playersInfo = (JSONArray) json.get(StandardMessage.MESSAGE_CLIENTS);

                                    ArrayList<JSONObject> tempInfo = new ArrayList<JSONObject>();
                                    JSONObject clientInfo = (JSONObject) parser.parse(playersInfo.get(0).toString());
                                    tempInfo.add(clientInfo);

                                    biggestPID = Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
                                    secondBiggestPID = 0;
                                    for(int i = 1; i<playersInfo.size();i++){
                                        clientInfo = (JSONObject) parser.parse(playersInfo.get(i).toString());
                                        tempInfo.add(clientInfo);
                                        int temp = Integer.parseInt(clientInfo.get(StandardMessage.MESSAGE_PLAYER_ID).toString());
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
                                else if(json.containsValue("thanks for playing")){
                                    state = "END";
                                    break;
                                }
                            }
                        }
                        else if(json.containsKey(StandardMessage.MESSAGE_METHOD)){
                            if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_START)){
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
    
    public void acceptedProposal(){
        out.println(clientProtocol.acceptedProposalMessage(player_id));
    }
    
    public boolean sendAccepted(String message){
        boolean ret = false;
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
    
    public void infoWerewolfKilled(int voteStatus, int playerKilled, ArrayList<ArrayList<Integer>> voteResult){
        out.println(clientProtocol.infoWerewolfKilledMessage(voteStatus, playerKilled, voteResult));
    }
    
    public void infoCivilianKilled(int voteStatus, int playerKilled, ArrayList<ArrayList<Integer>> voteResult){
        out.println(clientProtocol.infoCivilianKilledMessage(voteStatus, playerKilled, voteResult));
    }
}
