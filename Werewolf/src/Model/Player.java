/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author fauzanrifqy
 */
public class Player {
    private int id, port;       //id pemain
    private String name;        //Nama pemain
    private boolean role;       //True : Civilian, False : Werewolf
    private boolean ready;
    private boolean alive;
    private String ip="192.168.1.1";
    
    Player(int iid, String inname, String iip, int iport){
        id = iid;
        name = inname;
        role = true;
        ready = false;
        alive = true;
        ip = iip;
        port = iport;
    }
    
    Player(int iid, String inname, String IPAddress){
        id = iid;
        name = inname;
        role = true;
        ready = false;
        alive = true;
    }
    
    Player(String inname, boolean inrole){
        name = inname;
        role = inrole;
        ready = false;
        alive = true;
    }
    
    public void setPort(int inp){
        port = inp;
    }
    
    public int getPort(){
        return port;
    }
    
    public void setIP(String inp){
        ip = inp;
    }
    
    public String getIP(){
        return ip;
    }
    
    public void setDead(){
        alive = false;
    }
    
    public void Revive(){
        alive = true;
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public void setReady(){
        ready = true;
    }
    
    public void setNotReady(){
        ready = false;
    }
    
    public boolean isReady(){
        return ready;
    }
    
    public void setId(int inp){
        id = inp;
    }
    
    public int getId(){
        return id;
    }
    
    public void setName(String inp){
        name = inp;
    }
    
    public String getName(){
        return name;
    }
    
    public String getRole(){
        if(isCivil()){
            return "civilian";
        }else{
            return "werewolf";
        }
    }
    
    public boolean isWerewolf(){
        return !role;
    }
    
    public boolean isCivil(){
        return role;
    }
    
    public boolean isActive(){
        return (!getName().equals(""));
    }
    
    public void setRoleWerewolf(){
        role = false;
    }
    
    public void setRoleCivil(){
       role = true;
    }
    
    public void setRole(boolean inp){
        role = inp;
    }
}
