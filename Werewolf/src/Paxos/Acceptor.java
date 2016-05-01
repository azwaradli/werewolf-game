/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Paxos;
import Client.*;

/**
 *
 * @author Hp
 */
public class Acceptor {
    private Messenger messenger;
    private TCPConnection connection;
    
    public Acceptor(TCPConnection _connection){
        connection = _connection;
    }
    
}
