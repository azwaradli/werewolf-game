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
    public int compare( ProposalID rhs ) {
        if ( equals(rhs) )
                return 0;
        if ( id < rhs.id || (id == rhs.id && playerID > rhs.playerID))
                return -1;
        return 1;
    }

    public boolean isGreaterThan( ProposalID rhs ) {
        return compare(rhs) > 0;
    }

    public boolean isLessThan( ProposalID rhs ) {
        return compare(rhs) < 0;
    }

    public boolean equals(Object obj) {
        if (this == obj)
                return true;
        if (obj == null)
                return false;
        if (getClass() != obj.getClass())
                return false;
        ProposalID other = (ProposalID) obj;
        if (id != other.id)
                return false;
        if (playerID == -1) {
                if (other.playerID != -1)
                        return false;
        } else if (playerID != other.playerID)
                return false;
        return true;
    }
    
//    public int compare(ProposalID rhs) {
//        if (this.id == rhs.id) {
//            if (rhs.getPlayerID() == playerID){
//                return 0;
//            }
//            else if (playerID < rhs.getPlayerID()) {
//                return -1;
//            }
//            else {
//                return 1;
//            }
//        }
//        else if (this.id < rhs.id) {
//            return -1;
//        }
//        else {
//            return 1;
//        }
//    }
}
