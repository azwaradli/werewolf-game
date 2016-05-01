/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;

import java.util.ArrayList;

/**
 *
 * @author Hp
 */
public class ProposalID {
    private int id;
    private int playerID;
    
    
    public ProposalID(ArrayList<Integer> playerIDList){
        id = playerIDList.get(0);
        playerID = playerIDList.get(1);
    }
    
    public ProposalID(int _id, int _playerID){
        id = _id;
        playerID = _playerID;
    }
    
    public int getID(){
        return id;
    }
    
    public int getPlayerID(){
        return playerID;
    }
    
    public void setID(int _id){
        id = _id;
    }
    
    public void incrementID(){
        id++;
    }
    
    public int compare(ProposalID rhs) {
        if (this.id == rhs.id) {
            if (rhs.getPlayerID() == playerID){
                return 0;
            }
            else if (playerID < rhs.getPlayerID()) {
                return -1;
            }
            else {
                return 1;
            }
        }
        else if (this.id < rhs.id) {
            return -1;
        }
        else {
            return 1;
        }
    }
}
