/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Client.ClientStandard;
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
        data.put(ClientStandard.MESSAGE_METHOD, ClientStandard.PARAM_JOIN);
        data.put(ClientStandard.MESSAGE_USERNAME, username);
        
        return data;
    }
    
    public JSONObject leaveGameMessage(){
        data.put(ClientStandard.MESSAGE_METHOD, ClientStandard.PARAM_LEAVE);
        
        return data;
    }
    
    public JSONObject readyUpMessage(){
        data.put(ClientStandard.MESSAGE_METHOD, ClientStandard.PARAM_READY);
        
        return data;
    }
    
    public JSONObject listClientMessage(){
        data.put(ClientStandard.MESSAGE_METHOD, ClientStandard.PARAM_CLIENT_ADDRESS);
        
        return data;
    }
       
}
