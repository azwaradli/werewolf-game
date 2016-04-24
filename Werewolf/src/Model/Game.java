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
    
    public int playerSize(){
        return players.size();
    }
    
    public boolean addPlayer(String name){
        if(!players.contains(name)){
            players.add(new Player(stPlayerId++, name));
            return true;
        }else{
            return false;
        }
    }
    
    public void deletePlayer(String name){
        players.remove(name);
    }
    
    public void gameStart(){
        started = true;
    }
    
}
