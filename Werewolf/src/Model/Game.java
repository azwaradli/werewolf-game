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
    
    public Game(){
        players = new ArrayList();
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
    
    public boolean addPlayer(String name){
        if(isNameUnique(name)){
            players.add(new Player(stPlayerId++, name));
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
