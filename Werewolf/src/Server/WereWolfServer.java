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
import java.util.ArrayList;
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
    private final static int MIN_PLAYER = 2;
    
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
                String thisIP = socket.getRemoteSocketAddress().toString();
                int thisPort = socket.getPort();
                boolean isready = false;
                
                while(true){
                    getjson = in.readLine();
                    System.out.println("Server :: Client Message :: " + getjson);
                    try {
                        
                        Object obj = parser.parse(getjson);
                        json = (JSONObject)obj;
                        
                        if(json.containsKey(StandardMessage.MESSAGE_METHOD)){
                            if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_JOIN))
                            {      
                            //------------------------------
                            //----------JOIN STATE----------
                            //------------------------------
                                System.out.println("Server :: Client Request Join");
                                if(!game.isStarted())
                                {      
                                    //Jika Game Belum Di Mulai
                                    if(json.containsKey(StandardMessage.MESSAGE_USERNAME))
                                    {
                                        //Check ip and address
                                        if(json.containsKey("udp_address"))
                                        {
                                            thisIP = json.get("udp_address").toString();
                                        }
                                        if(json.containsKey("udp_port"))
                                        {
                                            thisPort = Integer.parseInt(json.get("udp_port").toString());
                                        }
                                        
                                        //Jika Terdapat Key Username
                                        if(game.addPlayer(json.get(StandardMessage.MESSAGE_USERNAME).toString(), thisIP, thisPort))
                                        {
                                            //Jika username unique dan berhasil ditambahkan
                                            System.out.println("Server :: Client Join Success as " + json.get(StandardMessage.MESSAGE_USERNAME).toString());
                                            thisClient = game.getPlayerId(json.get(StandardMessage.MESSAGE_USERNAME).toString());
                                            String message = mc.joinSuccess(thisClient);
                                            out.println(message);
                                        }
                                        else
                                        {
                                            //Jika username tidak unique dan gagal ditambahkan
                                            System.out.println("Server :: Client Join Request Rejected, Name Already Exists or Your input name is empty.");
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
                            else if(thisClient > -1 && json.containsKey(StandardMessage.MESSAGE_METHOD))            //Check Apakah User telah berhasil join dan telah memiliki id
                            {
                                if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_LEAVE))
                                {
                                    //------------------------------
                                    //----------LEAVE STATE---------
                                    //------------------------------
                                    System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to leave.");
                                    isready = false;
                                    if(game.deletePlayer(thisClient))
                                    {
                                        String message = mc.leaveSuccess();
                                        out.println(message);
                                        System.out.println("Server :: Client is leaving the game.");
                                    }
                                    else
                                    {
                                        String message = mc.failureUserNoExist();
                                        out.println(message);
                                        System.out.println("Server :: Client leaving error, id doesn't matched.");
                                    }
                                }
                                else if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_READY))
                                {
                                    //------------------------------
                                    //----------READY STATE---------
                                    //------------------------------
                                    System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to change ready state.");
                                    if(game.getReady(thisClient))
                                    {
                                        String message = mc.readySuccess();
                                        isready = true;
                                        out.println(message);
                                        System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" ready to play. Waiting " + (MIN_PLAYER-game.playerReadySize())+" more to play.");
                                        while(game.playerReadySize() < MIN_PLAYER || game.playerReadySize() != game.playerSize());
                                        
                                        //----------GAME START--------------
                                        if(game.playerReadySize() >= MIN_PLAYER && game.playerReadySize() == game.playerSize())
                                        {
                                            game.start();
                                            System.out.println("Server ::Game Started");
                                            message = game.messageStartGame(thisClient);
                                            out.println(message);
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" failure to ready. Userid doesn't recognize, please relogin.");
                                        String message = mc.failureUserNoExist();
                                        out.println(message);
                                    }
                                }
                                else if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_CLIENT_ADDRESS))
                                {
                                    //---------------------------------------
                                    //----------CLIENT ADDRESS STATE---------
                                    //---------------------------------------
                                    System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to client address");
                                    ArrayList<Player> players = game.getPlayer();
                                    String message = mc.requestPlayers(players);
                                    out.println(message);
                                }
                                else if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_PREPARE_PROPOSAL)&&isready)
                                {
                                    //-----------------------------------------
                                    //----------PREPARE PROPOSAL STATE---------
                                    //-----------------------------------------
                                    if(json.containsKey(StandardMessage.MESSAGE_KPU_ID))
                                    {
                                        System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" confirm KPU ID "+json.get(StandardMessage.MESSAGE_KPU_ID).toString());
                                        if(game.prepareLeader((int) json.get(StandardMessage.MESSAGE_KPU_ID))==-1){
                                            System.out.println("Server ::Client "+ game.getPlayer(thisClient).getName() +" succesfully confirm. wait for other player confirm.");
                                            
                                            long startTime = System.currentTimeMillis(); //fetch starting time
                                            while(game.checkLeader() == -1&&(System.currentTimeMillis()-startTime)<10000);    //Wait other players until done or 10 seconds
                                            
                                            if(game.checkLeader() != -1)
                                            {
                                                //----------------------------------
                                                //----------KPU ID RECEIVED---------
                                                //----------------------------------
                                                System.out.println("Server ::All Client has confirmed their kpu id. "+game.getConflict()+" conflict.");
                                                
                                                String message = mc.prepareProposalFail();
                                                out.println(message);
                                             
                                                getjson = in.readLine();
                                                obj = parser.parse(getjson);
                                                json = (JSONObject)obj;
                                                System.out.println("Server :: Client Message :: " + getjson);
                                                
//                                                if(game.getPlayer(thisClient).getId() == game.getLeaderId())
//                                                {
//                                                    System.out.println("Server ::KPU Send a Request");
//                                                    //----------------------------------
//                                                    //----------PLAYER AS KPU-----------
//                                                    //----------------------------------
//                                                    if(json.containsKey("method"))
//                                                    {
//                                                        if(json.get("method").equals("vote_result_werewolf"))
//                                                        {
//                                                            System.out.println("Server ::KPU Request Vote Werewolf");
//                                                            //------------------------------------------
//                                                            //----------INFO WEREWOLF KILLED------------
//                                                            //------------------------------------------
//                                                            if(json.containsKey("vote_status") && 
//                                                                    json.containsKey("vote_result"))
//                                                            {
//                                                                if(json.get("vote_status").equals(1)&&json.containsKey("player_killed"))
//                                                                {
//                                                                    
//                                                                }
//                                                                else
//                                                                {
//                                                                    
//                                                                }
//                                                            }
//                                                            else
//                                                            {
//                                                                System.out.println("Server ::KPU Bad Request No Parameter vote_status or vote_result.");
//                                                            }
//                                                        }
//                                                        else
//                                                        {
//                                                            /* Not Vote Werewolf */
//                                                        }
//                                                    }
//                                                    else
//                                                    {
//                                                        //----------WRONG STATE---------
//                                                        System.out.println("Server :: Client has no method");
//                                                        message = mc.failureWrongRequest();
//                                                        out.println(message);
//                                                    }
//                                                }
//                                                else
//                                                {
//                                                    //----------------------------------
//                                                    //----------PLAYER AS ACCEPTOR------
//                                                    //----------------------------------
//                                                }
                                                
                                            }
                                            else
                                            {
                                                //----------------------------------
                                                //----------KPU ID REJECTED---------
                                                //----------------------------------
                                                System.out.println("Server ::Other clients failed to confirm their kpu id.");
                                                String message = mc.prepareProposalSuccess();
                                                out.println(message);
                                                
                                            }
                                        }
                                        
                                    }
                                    else
                                    {
                                        System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" doesn't have kpu id");
                                    }
                                            
                                    
                                }
                            }
                            else
                            {
                                //----------WRONG STATE---------
                                System.out.println("Server :: Client Bad Request.");
                                String message = mc.failureWrongRequest();
                                out.println(message);
                            }
                        }
                        else
                        {
                            //----------WRONG STATE---------
                            System.out.println("Server :: Client has no method");
                            String message = mc.failureWrongRequest();
                            out.println(message);
                        }
                        
                    } catch (ParseException ex) {
                        Logger.getLogger(WereWolfServer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch(NullPointerException npe){
                        if(thisClient!=-1)
                        {
                            System.out.println("Server :: Client "+game.getPlayer(thisClient).getName()+" Leaving the game.");
                            game.deletePlayer(thisClient);
                        }
                        return;
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
