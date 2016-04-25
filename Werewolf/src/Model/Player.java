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
    private int id;             //id pemain
    private String name;        //Nama pemain
    private boolean role;       //True : Civilian, False : Werewolf
    private String address;
    private Integer port;
    private boolean ready;

    
    Player(int iid, String inname, String IPAddress){
        id = iid;
        name = inname;
        role = true;
        port = Integer.parseInt(IPAddress.split(":")[1]);
        address = IPAddress.split(":")[0].substring(1);
        ready = false;

    }
    
    Player(String inname, boolean inrole){
        name = inname;
        role = inrole;
        ready = false;
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
    
    public boolean isWerewolf(){
        return !role;
    }
    
    public boolean isCivil(){
        return role;
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
    
    public String getAddress(){
        return address;
    }
    
    public Integer getPort(){
        return port;
    }
}
