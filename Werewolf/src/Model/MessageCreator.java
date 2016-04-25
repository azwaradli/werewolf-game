/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author fauzanrifqy
 */
public class MessageCreator {
    
    public String joinSuccess(Integer id){
        try {
            JSONObject obj = new JSONObject();
            
            obj.put("status", "ok");
            obj.put("player_id", id.toString());
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String joinFailureUserExists(){
        try {
            JSONObject obj = new JSONObject();
            
            obj.put("status", "fail");
            obj.put("description", "user exists");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String joinFailureGameStarted(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("status", "fail");
            obj.put("description", "please wait, game is currently running");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String failureWrongRequest() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("status", "fail");
            obj.put("description", "wrong request");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String leaveSuccess(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("status", "ok");
            obj.put("description", "thanks for playing");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String leaveFailure(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("status", "fail");
            obj.put("description", "your id doesn't exist");
            
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            
            return out.toString();
        } catch (IOException ex) {
            Logger.getLogger(MessageCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
