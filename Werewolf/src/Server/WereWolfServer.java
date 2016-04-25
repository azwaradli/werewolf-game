/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author fauzanrifqy
 */
public class WereWolfServer {
    
    private static final int PORT = 8080;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private static Game game = new Game();
    private static MessageCreator mc = new MessageCreator();
    
    public static void main(String[] args) throws IOException{
        System.out.println("The Werewolf server is running");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
    
    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                String getjson;         //tempat string yang didapatkan dari client
                JSONObject json;        //string yang telah diubah ke json object
                JSONParser parser = new JSONParser();   //json parser
                System.out.println(socket.getRemoteSocketAddress().toString());
                System.out.println("Server :: A Client is connected");
                int thisClient = -1;
                while(true){
                    getjson = in.readLine();
                    System.out.println("Server :: Client Message :: " + getjson);
                    try {
                        
                        Object obj = parser.parse(getjson);
                        json = (JSONObject)obj;
                        
                      
                        if(json.get("method").equals("join"))
                        {      
                        //------------------------------
                        //----------JOIN STATE----------
                        //------------------------------
                            System.out.println("Server :: Client Request Join");
                            if(!game.isStarted())
                            {      
                                //Jika Game Belum Di Mulai
                                if(json.containsKey("username"))
                                {
                                    //Jika Terdapat Key Username
                                    if(game.addPlayer(json.get("username").toString(),socket.getRemoteSocketAddress().toString()))
                                    {
                                        //Jika username unique dan berhasil ditambahkan
                                        System.out.println("Server :: Client Join Success as " + json.get("username").toString());
                                        thisClient = game.getPlayerId(json.get("username").toString());
                                        String message = mc.joinSuccess(thisClient);
                                        out.println(message);
                                    }
                                    else
                                    {
                                        //Jika username tidak unique dan gagal ditambahkan
                                        System.out.println("Server :: Client Join Request Rejected, Name Already Exists");
                                        String message = mc.joinFailureUserExists();
                                        out.println(message);
                                    }
                                }
                                else
                                {
                                    //Jika Tidak Memiliki Key Username
                                    System.out.println("Server :: Client Join Request Rejected, Username doesn't recognize");
                                    String message = mc.failureWrongRequest();
                                    out.println(message);
                                }
                                
                            }
                            else
                            {
                                //Jika Game Telah Berjalan
                                System.out.println("Server :: Client Join Request Rejected, Game Already Began");
                                String message = mc.joinFailureGameStarted();
                                out.println(message);
                            } 
                        }
                        else if(thisClient > -1)            //Check Apakah User telah berhasil join dan telah memiliki id
                        {
                            if(json.get("method").equals("leave"))
                            {
                                //------------------------------
                                //----------LEAVE STATE---------
                                //------------------------------
                                if(game.deletePlayer(thisClient))
                                {
                                    mc.leaveSuccess();
                                }
                                else
                                {
                                    mc.leaveFailure();
                                }
                            }
                        }
                        else
                        {
                            //------------------------------
                            //----------WRONG STATE---------
                            //------------------------------
                            mc.failureWrongRequest();
                        }
                        
                    } catch (ParseException ex) {
                        Logger.getLogger(WereWolfServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
//                while (true) {
//                    out.println("SUBMITNAME");
//                    name = in.readLine();
//                    if (name == null) {
//                        return;
//                    }
//                    synchronized (names) {
//                        if (!names.contains(name)) {
//                            names.add(name);
//                            break;
//                        }
//                    }
//                }

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
//                out.println("NAMEACCEPTED");
//                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
//                boolean isPlayerComplete = false;
//                while(!isPlayerComplete){
//                    for (PrintWriter writer : writers) {
//                        writer.println("REFRESHLISTPLAYERS");
//                        for(String temp : names){
//                            writer.println("LISTPLAYERS " + temp.toString());
//                        }
//                        if(writers.size() >= 6){
//                            isPlayerComplete = true;
//                        }
//                    }
//                }
                
//                while (true) {
//                    
//                    String input = in.readLine();
//                    if (input == null) {
//                        return;
//                    }
//                    for (PrintWriter writer : writers) {
//                        writer.println("MESSAGE " + name + ": " + input);
//                    }
//                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
//                if (name != null) {
//                    names.remove(name);
//                }
//                if (out != null) {
//                    writers.remove(out);
//                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        
        
        
    }
    
}
