/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author fauzanrifqy
 */
public class Game {
    private ArrayList<Player> players;
    private int stPlayerId=0;
    private boolean day = false;
    private boolean started = false;
    private int idLeader = -1, gconflict = 0;
    private ArrayList<Integer> preparatorLead = new ArrayList();
    
    public Game(){
        players = new ArrayList();
    }
    
    public int getConfirmPlayerSize(){
        return preparatorLead.size();
    }
    
    public int getNotConfirmPlayerSize(){
        return players.size()-preparatorLead.size();
    }
    
    public int getLeaderId(){
        return idLeader;
    }
    
    public void setLeaderId(int inp){
        idLeader = inp;
    }
    
    public int prepareLeader(int inp){
        preparatorLead.add(inp);
        return checkLeader();
    }
    
    public int checkLeader(){
        if(preparatorLead.size() == players.size()){
            return makeLeader();
        }
        return -1;
    }
    
    //Mengembalikan nilai konflik yang terjadi
    private int makeLeader(){
        int temp = preparatorLead.get(0);
        int cconflict = 0, cagree = 0;
        for(int i=1; i < preparatorLead.size(); i++){
            if(preparatorLead.get(i) == temp){
                cagree++;
            }else{
                cconflict++;
            }
        }
        idLeader = temp;
        gconflict = cconflict;
        return cconflict;
    }
    
    public int getConflict(){
        return gconflict;
    }
    
    public boolean isStarted(){
        return started;
    }
    
    public boolean isDay(){
        return day;
    }
    
    public boolean isNight(){
        return !day;
    }
    
    public Integer getPlayerId(String name){
        for(Player player : players){
            if(player.getName().equals(name)){
                return player.getId();
            }
        }
        return null;
    }
    
    public Player getPlayer(int inp){
        return players.get(inp);
    }
    
    public ArrayList<Player> getPlayer(){
        return players;
    }
    
    public ArrayList<Player> getPlayerReady(){
        ArrayList<Player> tempPlayers = new ArrayList();
        for(Player player : players){
            if(player.isReady()){
                tempPlayers.add(player);
            }
        }
        return tempPlayers;
    }
    
    public ArrayList<Player> getPlayerNotReady(){
        ArrayList<Player> tempPlayers = new ArrayList();
        for(Player player : players){
            if(!player.isReady()){
                tempPlayers.add(player);
            }
        }
        return tempPlayers;
    }
    
    public int playerSize(){
        return players.size();
    }
    
    public int playerReadySize(){
        int temp = 0;
        for(Player player : players){
            if(player.isReady()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerNotReadySize(){
        int temp = 0;
        for(Player player : players){
            if(!player.isReady()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerAliveSize(){
        int temp = 0;
        for(Player player : players){
            if(player.isAlive()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerDeadSize(){
        int temp = 0;
        for(Player player : players){
            if(!player.isAlive()){
                temp++;
            }
        }
        return temp;
    }
    
    public boolean addPlayer(String name, String ip, int port){
        if(isNameUnique(name)){
            players.add(new Player(stPlayerId++, name, ip, port));
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isNameUnique(String name){
        for(Player player : players){
            if(player.getName().equals(name)){
                return false;
            }
        }
        return true;
    }
    
    public boolean deletePlayer(String name){
        for(Player player : players){
            if(player.getName().equals(name)){
                players.remove(player);
                return true;
            }
        }
        return false;
    }
    
    public boolean deletePlayer(int id){
        for(Player player : players){
            if(player.getId() == id){
                players.remove(player);
                return true;
            }
        }
        return false;
    }
    
    public boolean getReady(int id){
        for(Player player : players){
            if(player.getId() == id){
                player.setReady();
                return true;
            }
        }
        return false;
    }
    
    public boolean cancelReady(int id){
        for(Player player : players){
            if(player.getId() == id){
                player.setNotReady();
                return true;
            }
        }
        return false;
    }
    
    public void gameStart(){
        started = true;
    }
    
}
