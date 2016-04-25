/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.StandardMessage;
import org.json.simple.JSONObject;
/**
 *
 * @author Adz
 */
public class ClientProtocol {
    JSONObject data;
    
    public ClientProtocol(){
        data = new JSONObject();
    }
    
    public JSONObject joinGameMessage(String username){
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_JOIN);
        data.put(StandardMessage.MESSAGE_USERNAME, username);
        
        return data;
    }
    
    public JSONObject leaveGameMessage(){
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_LEAVE);
        
        return data;
    }
    
    public JSONObject readyUpMessage(){
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_READY);
        
        return data;
    }
    
    public JSONObject listClientMessage(){
        data.put(StandardMessage.MESSAGE_METHOD, StandardMessage.PARAM_CLIENT_ADDRESS);
        
        return data;
    }
       
}
