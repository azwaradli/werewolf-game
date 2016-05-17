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
import java.net.SocketException;
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
    private static boolean waitingkpu = true, waitingsender = true;
    private final static int MIN_PLAYER = 2;
    private static int CURRENT_PLAYER = 0;
    
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
                
                while(true){
                    getjson = in.readLine();
                    System.out.println("Server :: Client "+ thisClient +" Message :: " + getjson);
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
                                if(!game.isBegan())
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
                                    game.getPlayer(thisClient).setNotReady();
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
                                    System.out.println("Server :: [Ready State] | Client "+ game.getPlayer(thisClient).getName() +" requesting to change ready state.");
                                    if(game.getReady(thisClient))
                                    {
                                        String message = mc.readySuccess();
                                        game.getPlayer(thisClient).setReady();
                                        out.println(message);
                                        if(MIN_PLAYER > game.playerSize()){
                                            CURRENT_PLAYER = MIN_PLAYER;
                                        }else{
                                            CURRENT_PLAYER = game.playerSize();
                                        }
                                        System.out.println("Server :: [Ready State] | Client "+ game.getPlayer(thisClient).getName() +" ready to play. Waiting " + (CURRENT_PLAYER-game.playerReadySize())+" more to play.");
                                       
                                        while(game.playerReadySize() < MIN_PLAYER || game.playerReadySize() != game.playerSize()){
                                            try {
                                                Thread.sleep(200);                 //1000 milliseconds is one second.
                                            } catch(InterruptedException ex) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                        
                                        //----------GAME START--------------
                                        if(game.playerReadySize() >= MIN_PLAYER && game.playerReadySize() == game.playerSize())
                                        {
                                            if(thisClient == game.getSenderId()){
                                                waitingsender = true;
                                                System.out.println("Server :: [Ready State] | Starting the Game");
                                                game.began();
                                                waitingsender = false;
                                            }else{
                                                while(waitingsender){
                                                    try {
                                                        Thread.sleep(200);                 //1000 milliseconds is one second.
                                                    } catch(InterruptedException ex) {
                                                        Thread.currentThread().interrupt();
                                                    }
                                                }
                                            }
                                            
                                            /* Send Message to Client */
                                            if(thisClient == game.getSenderId()){
                                                System.out.println("Server :: [Ready State] |Succesfully Started the Game.");
                                            }
                                            boolean sendf1 = true; 
                                            while(sendf1){
                                                message = game.messageStartGame(thisClient);
                                                out.println(message);
                                                System.out.println("Server :: [Ready State] | Sending Start Message to Player "+thisClient);

                                                getjson = in.readLine();
                                                System.out.println("Server :: [Ready State] | Receive Message from Player "+thisClient);
                                                obj = parser.parse(getjson);
                                                json = (JSONObject)obj;
                                                if(json.get("status").toString().equals("ok")){
                                                    sendf1 = false;
                                                }
                                            }
                                            
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("Server :: [Ready State] | Client "+ game.getPlayer(thisClient).getName() +" failure to ready. Userid doesn't recognize, please relogin.");
                                        String message = mc.failureUserNoExist();
                                        out.println(message);
                                    }
                                }
                                else if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_CLIENT_ADDRESS))
                                {
                                    //---------------------------------------
                                    //----------REQUEST PLAYER---------------
                                    //----------CLIENT ADDRESS STATE---------
                                    //---------------------------------------
                                    //System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to client address");
                                    ArrayList<Player> players = game.getPlayer();
                                    String message = mc.requestPlayers(players);
                                    out.println(message);
                                }
                                else if((json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_ACCEPTED_PROPOSAL)&&game.getPlayer(thisClient).isReady()))
                                {
                                    //-----------------------------------------
                                    //----------ACCEPTED PROPOSAL STATE---------
                                    //-----------------------------------------
                                    if(json.containsKey(StandardMessage.MESSAGE_KPU_ID))
                                    {
                                        System.out.println("Server :: [Accepted Proposal State] | Client "+ game.getPlayer(thisClient).getName() +" confirm KPU ID "+json.get(StandardMessage.MESSAGE_KPU_ID).toString());
                                        if(game.prepareLeader(Integer.parseInt(json.get(StandardMessage.MESSAGE_KPU_ID).toString()))==-1){
                                            System.out.println("Server :: [Accepted Proposal State] | Client "+ game.getPlayer(thisClient).getName() +" succesfully confirm. wait for other player confirm.");
                                            
                                            long startTime = System.currentTimeMillis(); //fetch starting time
                                            System.out.println("Server :: [Accepted Proposal State] | Client "+thisClient + " waiting leader.");
                                            //while(game.checkLeader() == -1&&(System.currentTimeMillis()-startTime)<10000);    //Wait other players until done or 10 seconds
                                            while(game.checkLeader() == -1){
                                                try {
                                                    Thread.sleep(200);                 //1000 milliseconds is one second.
                                                } catch(InterruptedException ex) {
                                                    Thread.currentThread().interrupt();
                                                }
                                            }
                                            System.out.println("Server :: [Accepted Proposal State] | Client "+thisClient + " leader chosen.");
                                        }else{
                                            System.out.println("Server :: [Accepted Proposal State] | Client "+ game.getPlayer(thisClient).getName() +" succesfully confirm. Done waiting.");
                                        }
                                        
                                    }
                                    else
                                    {
                                        System.out.println("Server :: [Accepted Proposal State] | Client "+ game.getPlayer(thisClient).getName() +" doesn't have kpu id");
                                    }
                                }
                                else if(json.get("method").equals("waiting_vote_now")&&game.getPlayer(thisClient).isReady()&&game.isBegan())
                                {
                                    System.out.println("Server :: [Accepted Proposal State] | CLIENT "+thisClient+" Request VOTE NOW");
                                    while(game.checkLeader() == -1){
                                        try {
                                            Thread.sleep(200);                 //1000 milliseconds is one second.
                                        } catch(InterruptedException ex) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                    System.out.println("Server :: [Accepted Proposal State] | Client "+thisClient+" stop waiting leader.");
                                }
                                
                                //--------------------------------------------
                                //----------GAME STARTED KPU RECEIVED---------
                                //--------------------------------------------
                                
                                if((game.checkLeader() != -1&&game.getPlayer(thisClient).isReady()&&game.isBegan()))
                                {
                                    //----------------------------------
                                    //----------KPU ID RECEIVED---------
                                    //----------------------------------
                                    System.out.println("Server :: [KPU RECEIVED] | Leader Chosen");
                                    
                                    System.out.println("Server :: [KPU RECEIVED] | All Client has confirmed their kpu id. "+game.getConflict()+" conflict.");

                                    
                                    String message = mc.prepareProposalSuccess(game.getLeaderId());
                                    out.println(message);
                                    System.out.println("Server :: [KPU RECEIVED] | Sent Kpu ID to Client "+thisClient);
                                    
                                    int tryKill = 0;
                                    while(true){
                                        System.out.println("Server :: [KPU RECEIVED] | Start on Game State.");
                                        //----------------------------------
                                        //----------ON GAME-----------------
                                        //----------------------------------
                                        System.out.println("Server :: [KPU RECEIVED] | Prepare Voting.");

                                        //waitingkpu = true;
                                        game.setWaitingKPU(true);
                                        String phase = game.getDay();

                                        /* Send Message to Client */
                                        boolean sendf2 = true;
                                        while(sendf2){
                                            message = game.messageVoteNow();
                                            out.println(message);
                                            System.out.println("Server :: [KPU RECEIVED] | Sending Vote Message to Player "+thisClient);

                                            getjson = in.readLine();
                                            System.out.println("Server :: [KPU RECEIVED] | Receive Message from Player "+thisClient);
                                            obj = parser.parse(getjson);
                                            json = (JSONObject)obj;
                                            System.out.println("Server :: Client "+ thisClient +" Message :: " + getjson);
                                            if(json.get("status").toString().equals("ok")){
                                                sendf2 = false;
                                            }
                                        }
                                        
                                        System.out.println("Server :: [Done Phase] | Wait client address request from Client "+thisClient);
                                        getjson = in.readLine();
                                        System.out.println("Server :: [Done Phase] | Receive Message from Player "+thisClient);
                                        obj = parser.parse(getjson);
                                        json = (JSONObject)obj;
                                        System.out.println("Server :: Client "+ thisClient +" Req Cient Address Message :: " + getjson);

                                        if(json.get("method").equals("client_address")){
                                            //---------------------------------------
                                            //----------REQUEST PLAYER---------------
                                            //----------CLIENT ADDRESS STATE---------
                                            //---------------------------------------
                                            //System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to client address");
                                            ArrayList<Player> players = game.getPlayer();
                                            message = mc.requestPlayers(players);
                                            out.println(message);
                                        }else{
                                            System.out.println("Server :: You must request client address first.");
                                        }
                                        
                                        if(game.getPlayer(thisClient).getId() == game.getLeaderId())
                                        {
                                            System.out.println("Server :: [PLAYER AS KPU] | Received Request from KPU");
                                            //----------------------------------
                                            //----------PLAYER AS KPU-----------
                                            //----------------------------------

                                            getjson = in.readLine();
                                            obj = parser.parse(getjson);
                                            json = (JSONObject)obj;
                                            System.out.println("Server :: [PLAYER AS KPU] | KPU "+ thisClient +" Message :: " + getjson);

                                            if(json.containsKey("method"))
                                            {
                                                if(json.get(StandardMessage.MESSAGE_METHOD).equals(StandardMessage.PARAM_CLIENT_ADDRESS))
                                                {
                                                    //---------------------------------------
                                                    //----------REQUEST PLAYER---------------
                                                    //----------CLIENT ADDRESS STATE---------
                                                    //---------------------------------------
                                                    //System.out.println("Server :: Client "+ game.getPlayer(thisClient).getName() +" requesting to client address");
                                                    ArrayList<Player> players = game.getPlayer();
                                                    message = mc.requestPlayers(players);
                                                    out.println(message);
                                                }
                                                else if(json.get("method").equals("vote_result_civilian")&&game.isDay())
                                                {
                                                    System.out.println("Server :: [PLAYER AS KPU] | KPU Request Vote Werewolf");
                                                    //------------------------------------------
                                                    //----------INFO WEREWOLF KILLED------------
                                                    //------------------------------------------
                                                    boolean someoneKilled = false;
                                                    if(json.containsKey("vote_status") && json.containsKey("vote_result"))
                                                    {
                                                        if(json.get("vote_status").toString().equals("1")&&json.containsKey("player_killed"))
                                                        {
                                                            System.out.println("Server :: [PLAYER AS KPU] | 'Day Voting' has approving one person to kill.");
                                                            someoneKilled = true;
                                                            game.outDataVote((JSONArray) json.get("vote_result"), json.get("player_killed").toString());
                                                            game.killPlayer(Integer.parseInt(json.get("player_killed").toString()));

                                                            System.out.println("Server :: [PLAYER AS KPU] | Player "+game.getPlayer(Integer.parseInt(json.get("player_killed").toString())).getName()+" is killed.");

                                                            message = mc.killedPlayerSuccess();
                                                            out.println(message);
                                                        }
                                                        else if(json.get("vote_status").toString().equals("-1"))
                                                        {
                                                            System.out.println("Server :: [PLAYER AS KPU] | 'Day Voting' No Person killed.");
                                                            tryKill++;
                                                            message = mc.killedNoPlayerSuccess();
                                                            out.println(message);
                                                        }else{
                                                            System.out.println("Server :: [PLAYER AS KPU] | Status not known.");
                                                            message = mc.killedPlayerFail();
                                                            out.println(message);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        System.out.println("Server :: [PLAYER AS KPU] | KPU Bad Request No Parameter vote_status or vote_result(when vote_status == 1).");
                                                        message = mc.killedPlayerError();
                                                        out.println(message);
                                                    }
                                                    
                                                    if(someoneKilled||tryKill==2){
                                                        System.out.println("Server :: [PLAYER AS KPU] | Game changing phase.");
                                                        game.changeDay();
                                                        System.out.println("Server :: [PLAYER AS KPU] | Changed phase to "+game.getDay());
                                                        tryKill = 0;
                                                        someoneKilled = false;
                                                        //waitingkpu = false;
                                                        game.setWaitingKPU(false);
                                                    }
                                                    
                                                }
                                                else if(json.get("method").equals("vote_result_werewolf")&&game.isNight())
                                                {
                                                    System.out.println("Server :: [PLAYER AS KPU] | KPU Request Vote Civilian");
                                                    //------------------------------------------
                                                    //----------INFO CIVILIANS KILLED-----------
                                                    //------------------------------------------
                                                    boolean someoneKilled = false;
                                                    if(json.containsKey("vote_status") && json.containsKey("vote_result"))
                                                    {
                                                        if(json.get("vote_status").toString().equals("1")&&json.containsKey("player_killed"))
                                                        {
                                                            game.outDataVote((JSONArray) json.get("vote_result"), json.get("player_killed").toString());
                                                            game.killPlayer(Integer.parseInt(json.get("player_killed").toString()));
                                                            message = mc.killedPlayerSuccess();
                                                            out.println(message);
                                                            
                                                            someoneKilled = true;
                                                        }
                                                        else if(json.get("vote_status").toString().equals("-1"))
                                                        {
                                                            System.out.println("Server :: [PLAYER AS KPU] | No Civilian killed.");
                                                            message = mc.killedNoPlayerSuccess();
                                                            out.println(message);
                                                            
                                                        }else{
                                                            System.out.println("Server :: [PLAYER AS KPU] | Status not known.");
                                                            message = mc.killedPlayerFail();
                                                            out.println(message);
                                                        }
                                                        
                                                        if(someoneKilled){
                                                            System.out.println("Server :: [PLAYER AS KPU] | Game changing phase.");
                                                            game.changeDay();
                                                            System.out.println("Server :: [PLAYER AS KPU] | Changed phase to "+game.getDay());
                                                            //waitingkpu = false;
                                                            game.setWaitingKPU(false);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        System.out.println("Server :: [PLAYER AS KPU] | KPU Bad Request No Parameter vote_status or vote_result(when vote_status == 1).");
                                                        message = mc.killedPlayerError();
                                                        out.println(message);
                                                    }
                                                }else{
                                                    System.out.println("Server :: [PLAYER AS KPU] | Bad Request Method is Unknown.");
                                                    message = mc.killedError();
                                                    out.println(message);
                                                }
                                            }
                                            else
                                            {
                                                //----------WRONG STATE---------
                                                System.out.println("Server :: [PLAYER AS KPU] | Client has no method");
                                                message = mc.failureWrongRequest();
                                                out.println(message);
                                            }
                                        }
                                        //----------------------------------
                                        //----------PLAYER AS ACCEPTOR------
                                        //----------AND KPU WHEN DONE-------
                                        //----------------------------------
                                        System.out.println("Server :: Client "+thisClient + " waiting KPU Phase "+phase);
                                        game.setNoKill(false);
                                        if(thisClient == game.getLeaderId()){
                                            if(phase.equals(game.getDay())){
                                                game.setNoKill(true);
                                            }
                                        }
                                        while(phase.equals(game.getDay())){
                                            try {
                                                if(game.isNokill()) break;
                                                Thread.sleep(200);                 //1000 milliseconds is one second.
                                            } catch(InterruptedException ex) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                        System.out.println("Server :: Client "+thisClient + " End waiting kpu. Phase "+game.getDay());
                                        
                                        if(!phase.equals(game.getDay())){
                                            if(thisClient == game.getLeaderId()){
                                                System.out.println("Server :: [AFTER KPU DONE] | Day "+game.getDayCounter()+" phase "+game.getDay()+".");
                                            }

                                            /* Send Message to Client */
                                            boolean sendf3 = true;
                                            while(sendf3){
                                                message = game.messageChangePhase();
                                                out.println(message);
                                                System.out.println("Server :: [AFTER KPU DONE] | Sending Phase Message to Player "+thisClient);

                                                getjson = in.readLine();
                                                System.out.println("Server :: [AFTER KPU DONE] | Receive Message from Player "+thisClient);
                                                obj = parser.parse(getjson);
                                                json = (JSONObject)obj;
                                                if(json.get("status").toString().equals("ok")){
                                                    sendf3 = false;
                                                }
                                            }

                                            //----------------------------------
                                            //----------GAME OVER---------------
                                            //----------------------------------
                                            if(game.isGameOver()){
                                                System.out.println("Server :: [ENDING GAME] | Game End. Thank you for playing.");
                                                message = game.messageGameOver(game.getWinner());
                                                out.println(message);
                                                break;
                                            }

                                            if(game.isDay()){
                                                /* KPU Session Expire */
                                                System.out.println("Server :: [ENDING DAY] | KPU ID is expired.");
                                                game.resetLeader();
                                                break;
                                            }
                                        }
                                    }

                                }
                                else if(game.getPlayer(thisClient).isReady()&&game.isBegan())
                                {
                                    //----------------------------------
                                    //----------KPU ID REJECTED---------
                                    //----------------------------------
//                                    System.out.println("Server ::Other clients failed to confirm their kpu id.");
//                                    String message = mc.prepareProposalFail();
//                                    out.println(message);

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
                        System.out.println("Error Exception : Client "+thisClient+" "+npe.getMessage());
                        if(thisClient!=-1)
                        {
                            System.out.println("Server :: Client "+game.getPlayer(thisClient).getName()+" Leaving the game.");
                            game.deletePlayer(thisClient);
                        }
                        return;
//                    } catch (SocketException se) {
//                        if(thisClient!=-1)
//                        {
//                            System.out.println("Server :: Client "+game.getPlayer(thisClient).getName()+" Leaving the game.");
//                            game.deletePlayer(thisClient);
//                        }
//                        return;
                    }
                    if(game.isGameOver()){
                        System.out.println("Server :: Reseting The Game.");
                        game.resetGame();
                        System.out.println("Server :: Game has been Reset.");
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }   
    }
}
