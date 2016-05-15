/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author fauzanrifqy
 */
public class Game {
    private ArrayList<Player> players;
    private int stPlayerId=0;
    private int daycounter = 0;
    private boolean day = true;
    private boolean started = false, began = false;
    private int idLeader = -1, gconflict = 0, idSender=-1;
    private ArrayList<Integer> preparatorLead = new ArrayList();
    private ArrayList<Integer> werewolfId = new ArrayList();
    
    private int WEREWOLF_AMOUNT = 2;
    
    public Game(){
        players = new ArrayList();
    }
    
    public void killPlayer(int id){
        players.get(id).setDead();
    }
    
    private ArrayList<Integer> listRandInt(int min, int max, int amount){
        ArrayList<Integer> arrtemp = new ArrayList();
        for(int i=0; i < amount; i++){
            int temp = randInt(min, max);
            while(arrtemp.contains(temp)){
                temp++;
                temp = temp % max;
            }
            arrtemp.add(temp);
        }
        return arrtemp;
    }
    
    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    public void randomingWerewolf(){
        werewolfId = listRandInt(0, playerSize()-1, WEREWOLF_AMOUNT);
        int temp =0;
        for(Player player : players){
            if(player.isActive()){
                for(int i : werewolfId){
                    if(temp == i){
                        System.out.println("Random :: werewolf "+player.getName());
                        player.setRoleWerewolf();
                        break;
                    }else{
                        System.out.println("Random :: civil "+player.getName());
                    }
                }
                temp++;
            }
        }
    }
    
    public int getConfirmPlayerSize(){
        return preparatorLead.size();
    }
    
    public int getNotConfirmPlayerSize(){
        return playerSize()-preparatorLead.size();
    }
    
    public int getLeaderId(){
        return idLeader;
    }
    
    public void setLeaderId(int inp){
        idLeader = inp;
    }
    
    public void resetLeader(){
        preparatorLead.clear();
    }
    
    public int prepareLeader(int inp){
        preparatorLead.add(inp);
        return checkLeader();
    }
    
    public int checkLeader(){
        if(preparatorLead.size() == playerSize()-2&&preparatorLead.size()>0){
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
    
    public boolean isBegan(){
        return began;
    }
    
    public boolean isStarted(){
        return started;
    }
    
    public int getDayCounter(){
        return daycounter;
    }
    
    public String getDay(){
        if(isDay()){
            return "day";
        }else{
            return "night";
        }
    }
    
    public void changeDay(){
        day = !day;
        if(day){
            daycounter++;
        }
    }
    
    public boolean isDay(){
        return day;
    }
    
    public boolean isNight(){
        return !day;
    }
    
    public Integer getSenderId(){
        return idSender;
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
        ArrayList<Player> temp = new ArrayList();
        for(Player player : players){
            if(player.isActive()){
                temp.add(player);
            }
        }
        return temp;
    }
    
    public ArrayList<Player> getPlayerReady(){
        ArrayList<Player> tempPlayers = new ArrayList();
        for(Player player : getPlayer()){
            if(player.isReady()){
                tempPlayers.add(player);
            }
        }
        return tempPlayers;
    }
    
    public ArrayList<Player> getPlayerNotReady(){
        ArrayList<Player> tempPlayers = new ArrayList();
        for(Player player : getPlayer()){
            if(!player.isReady()){
                tempPlayers.add(player);
            }
        }
        return tempPlayers;
    }
    
    public int playerSize(){
        return getPlayer().size();
    }
    
    public int playerReadySize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(player.isReady()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerNotReadySize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(!player.isReady()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerAliveSize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(player.isAlive()){
                temp++;
            }
        }
        return temp;
    }
    
    public int playerDeadSize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(!player.isAlive()){
                temp++;
            }
        }
        return temp;
    }
    
    public int civilianSize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(player.isCivil()){
                temp++;
            }
        }
        return temp;
    }
    
    public int werewolfSize(){
        int temp = 0;
        for(Player player : getPlayer()){
            if(player.isWerewolf()){
                temp++;
            }
        }
        return temp;
    }
    
    public boolean addPlayer(String name, String ip, int port){
        if(isNameUnique(name)&&!name.equals("")){
            players.add(new Player(stPlayerId++, name, ip, port));
            if(idSender == -1){
                idSender = stPlayerId;
            }
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
                player.setName("");
                if(player.getId() == idSender){
                    idSender = getPlayer().get(0).getId();
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean deletePlayer(int id){
        for(Player player : players){
            if(player.getId() == id){
                player.setName("");
                if(player.getId() == idSender){
                    idSender = getPlayer().get(0).getId();
                }
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
    
    public void began(){
        began = true;
        randomingWerewolf();
        daycounter++;
    }
    
    public void start(){
        started = false;
    }
    
    public boolean isGameOver(){
        if(isStarted()){
            if(werewolfSize() == 0){
                return true;
            }else if(civilianSize() == 0){
                return true;
            }else if(civilianSize() == werewolfSize()){
                return true;
            }
            return false;
        }
        return false;
    }
    
    public void resetGame(){
        daycounter = 0;
        day = true;
        began = false;
        idLeader = -1;
        gconflict = 0;
        idSender= -1;
        preparatorLead.clear();
        werewolfId.clear();
        for(Player player : players){
            player.setNotReady();
        }
    }
    
    public String getWinner(){
        if(werewolfSize() == 0){
            return "civilian";
        }else if(civilianSize() == 0){
            return "werewolf";
        }else if(civilianSize() == 1 && werewolfSize() == 1){
            return "werewolf";
        }
        return "none";
    }
    
    public ArrayList<Player> getFriends(int mid){
        
        ArrayList<Player> temp = new ArrayList();
        for(Player player : getPlayer()){
            if((player.isCivil() == getPlayer(mid).isCivil())&&(player.getId()!=mid)){
                temp.add(player);
            }
        }
        return temp;
    }
    
    public String messageStartGame(int mid){
        ArrayList<Player> friends = getFriends(mid);
        
        try {
            JSONObject obj = new JSONObject();
            obj.put("method", "start");
            obj.put("time", getDay());
            obj.put("role", getPlayer(mid).getRole());
            
            ArrayList<String> jsonFriends = new ArrayList<String>();
            for(Player player : friends){
                jsonFriends.add(player.getName());
            }
            
            obj.put("friend", jsonFriends);
            
            obj.put("description", "Game is started.");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String messageChangePhase(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("method", "change_phase");
            obj.put("time", getDay());
            obj.put("day", getDayCounter());
            
            obj.put("description", "Game changed phase.");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String messageVoteNow(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("method", "vote_now");
            obj.put("phase", getDay());
            
            obj.put("description", "Vote now.");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String messageGameOver(String winner){
        try {
            JSONObject obj = new JSONObject();
            obj.put("method", "game_over");
            obj.put("winner", winner);
            obj.put("description", "Yeay!!");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void outDataVote(JSONArray jsonhasil, String idp){
        if(jsonhasil!=null){
            for(int i = 0; i < jsonhasil.size(); i++){
                JSONArray temp = (JSONArray)jsonhasil.get(i);
                for(int j = 0; j < temp.size(); j++){
                    Player tempplayer = getPlayer(Integer.parseInt(temp.get(0).toString()));
                    System.out.println("Server :: "+tempplayer.getName()+" voted by "+temp.get(1).toString()+" player");
                }
            }
        }
        System.out.println("Server :: "+getPlayer(Integer.parseInt(idp))+" is dead.");
    }
    
}
