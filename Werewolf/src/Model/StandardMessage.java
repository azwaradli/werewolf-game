/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Adz
 */
public class StandardMessage {
    /* Client */
    public static String MESSAGE_METHOD = "method";
    public static String MESSAGE_USERNAME = "username";
    public static String MESSAGE_PROPOSAL_ID = "proposal_id";
    public static String MESSAGE_KPU_ID = "kpu_id";
    public static String MESSAGE_ADDRESS = "address";
    public static String MESSAGE_PORT = "port";
    public static String MESSAGE_UDP_ADDRESS = "udp_address";
    public static String MESSAGE_UDP_PORT = "udp_port";
    public static String MESSAGE_PLAYER_ID = "player_id";
    public static String MESSAGE_PLAYER_ALIVE = "is_alive";
    public static String MESSAGE_VOTE_STATUS = "vote_status";
    public static String MESSAGE_PLAYER_KILLED = "player_killed";
    public static String MESSAGE_VOTE_RESULT = "vote_result";
    public static String MESSAGE_PREVIOUS_ACCEPTED = "previous_accepted";
            
    public static String PARAM_JOIN = "join";
    public static String PARAM_LEAVE = "leave";
    public static String PARAM_READY = "ready";
    public static String PARAM_CLIENT_ADDRESS = "client_address";
    public static String PARAM_PREPARE_PROPOSAL = "prepare_proposal";
    public static String PARAM_ACCEPT_PROPOSAL = "accept_proposal";
    public static String PARAM_WAITING_VOTE = "waiting_vote_now";
    public static String PARAM_ACCEPTED_PROPOSAL = "accepted_proposal";
    public static String PARAM_VOTE_WEREWOLF = "vote_werewolf";
    public static String PARAM_VOTE_RESULT_WEREWOLF = "vote_result_werewolf";
    public static String PARAM_VOTE_RESULT = "vote_result";
    public static String PARAM_VOTE_CIVILIAN = "vote_civilian";
    public static String PARAM_VOTE_RESULT_CIVILIAN = "vote_result_civilian";
    public static String PARAM_FAIL = "fail";
    public static String PARAM_REJECTED = "rejected";
    public static String PARAM_ERROR = "error";
    public static String KPU_SELECTED = "kpu_selected";
    public static String KPU_ID = "kpu_id";
    /* End of client */
    
    /* Server */
    public static String MESSAGE_STATUS = "status";
    public static String MESSAGE_DESCRIPTION = "description";
    public static String MESSAGE_CLIENTS = "clients";
    public static String MESSAGE_TIME = "time";
    public static String MESSAGE_ROLE = "role";
    public static String MESSAGE_FRIEND = "friend";
    public static String MESSAGE_DAYS = "days";
    public static String MESSAGE_PHASE = "phase";
    public static String MESSAGE_WINNER = "winner";
    
    public static String PARAM_OK = "ok";
    public static String PARAM_ACCEPTED = "accepted";
    public static String PARAM_PREV_ACCEPTED = "previous_accepted";
    public static String PARAM_START = "start";
    public static String PARAM_DAY = "day";
    public static String PARAM_NIGHT = "night";
    public static String PARAM_WEREWOLF = "werewolf";
    public static String PARAM_CIVILIAN = "civilian";
    public static String PARAM_CHANGE_PHASE = "change_phase";
    public static String PARAM_VOTE_NOW = "vote_now";
    public static String PARAM_KPU_SELECTED = "kpu_selected";
    public static String PARAM_GAME_OVER = "game_over";
    /* End of server */
}
