/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Client.ClientProtocol;
import Paxos.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author fauzanrifqy
 */
public class WereWolfClient {
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Werewolf Game");
    JTextArea messageArea = new JTextArea(8, 40);
    JTextArea playerList = new JTextArea(8, 40);
    
    JButton sendMessage = new JButton("Enter");
    JTextField messageBox = new JTextField(40);
    JTextField IPChooser = new JTextField(20);
    JTextField portChooser = new JTextField(10);
    
    int player_id;
    Messenger messenger;
    
    ClientProtocol clientProtocol = new ClientProtocol();

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the messageBox so that pressing Return in the
     * listener sends the messageBox contents to the server.  Note
     * however that the messageBox is messageBox NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public WereWolfClient() {
        
        //MAIN FRAME
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.GRAY);
        southPanel.setLayout(new GridBagLayout());
        
        messageBox.requestFocusInWindow();
        messageArea.setEditable(false);
        messageArea.setPreferredSize(new Dimension(20,messageArea.getPreferredSize().height));
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        messageArea.setLineWrap(true);
        
        mainPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        
        GridBagConstraints label = new GridBagConstraints();
        label.anchor = GridBagConstraints.LINE_START;
        label.weightx = 0D;
        label.weighty = 1.0D;
        label.gridx = 0;
        label.gridy = 1;
        
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.gridwidth = 2;
        left.weightx = 0D;
        left.weighty = 1.0D;
        left.gridx = 1;
        left.gridy = 1;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_START;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 0D;
        right.weighty = 1.0D;
        right.gridx = 3;
        right.gridy = 1;
        
        GridBagConstraints upper1 = new GridBagConstraints();
        upper1.anchor = GridBagConstraints.LINE_START;
        upper1.weightx = 0D;
        upper1.weighty = 1.0D;
        upper1.gridx = 0;
        upper1.gridy = 0;
        
        GridBagConstraints upper2 = new GridBagConstraints();
        upper2.anchor = GridBagConstraints.LINE_START;
        upper2.weightx = 0D;
        upper2.weighty = 1.0D;
        upper2.gridx = 1;
        upper2.gridy = 0;
        
        GridBagConstraints upper3 = new GridBagConstraints();
        upper3.anchor = GridBagConstraints.LINE_END;
        upper3.weightx = 0D;
        upper3.weighty = 1.0D;
        upper3.gridx = 2;
        upper3.gridy = 0;
        
        GridBagConstraints upper4 = new GridBagConstraints();
        upper4.anchor = GridBagConstraints.LINE_START;
        upper4.weightx = 0D;
        upper4.weighty = 1.0D;
        upper4.gridx = 3;
        upper4.gridy = 0;
        
        southPanel.add(new JLabel("Message : "),label);
        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);
        southPanel.add(new JLabel("IP Address : "),upper1);
        southPanel.add(IPChooser,upper2);
        southPanel.add(new JLabel(" Port : "),upper3);
        southPanel.add(portChooser,upper4);
        mainPanel.add(BorderLayout.SOUTH, southPanel);
        frame.add(mainPanel);
        
        frame.pack();
        
        // Add Listeners
        messageBox.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the messageBox by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                UDPSender udpSender = new UDPSender(IPChooser.getText(),Integer.parseInt(portChooser.getText()) , messageBox.getText());
                Thread t3 = new Thread(udpSender);
                t3.start();
                
                messageArea.append("<You>           :  " + messageBox.getText() + "\n");
                messageBox.setText("");
            }
        });
        
        sendMessage.addActionListener(new ActionListener(){
            /**
             * Responds to pressing the enter button by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                UDPSender udpSender = new UDPSender(IPChooser.getText(),Integer.parseInt(portChooser.getText()) , messageBox.getText());
                Thread t3 = new Thread(udpSender);
                t3.start();
                messageArea.append("<You>       :  " + messageBox.getText() + "\n");
                messageBox.setText("");
            }
        });
       
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Werewolf Game",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    private void waitForData(TCPConnection connection){
        while(!connection.isReady()){
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        connection.setDataReady(false);
    }
    
    private void waitForDay(TCPConnection connection, UDPListener udpListener, boolean listen){
        if(listen){
            udpListener.setWerewolfCount(connection.getWerewolfCount());
            udpListener.setCivilianCount(connection.getCivilianCount());
        }
        while(!connection.isDayChanged()){
            //busy waiting
            if(listen){
                if(udpListener.getWerewolfCount()<=0){
                    System.out.println("kill werewolf");
                    connection.infoWerewolfKilled(udpListener.getVoteResults(), udpListener);
                }
                if(udpListener.getCivilianCount()<=0){
                    System.out.println("kill civilian");
                    connection.infoCivilianKilled(udpListener.getVoteResults(), udpListener);
                }
            }
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        connection.setDayChanged(false);
    }
    
    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {
        String serverAddress = getServerAddress();
        int port = 8080;
        
        TCPConnection connection= new TCPConnection(serverAddress,port);
        messenger = new Messenger(connection);
        Thread t1 = new Thread(connection);
        t1.start();
        
        //UDPListener udpListener = new UDPListener(connection.getAddress(),connection.getLocalPort(),messageArea);
        UDPListener udpListener = new UDPListener(connection.getAddress(),connection.getLocalPort(),messenger);
        Thread t2 = new Thread(udpListener);
        t2.start();
        
        connection.joinGame(udpListener.getLocalPort());
        
        Scanner sc = new Scanner(System.in);
        
        boolean waiting = false;
        while(!waiting){
            System.out.println("Cara Komunikasi : ");
            System.out.println("Fungsi");
            String message = sc.nextLine();
            if(message.equals("leave-game")){
                connection.leaveGame();
            }
            else if(message.equals("ready-up")){
                connection.readyUp();
                waiting = true;
            }
//            else if(message.equals("list-client")){
//                connection.listClient();
//            } 
        }
        while(!connection.isStarted()){
            //busy waiting
            try {
                Thread.sleep(500);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Client : Start PAXOS");
        //PILIH LEADER
        connection.listClient();
        waitForData(connection);
        PaxosController paxosController = new PaxosController(connection.getPlayerId(),connection);
        udpListener.setProposer(paxosController.getProposer());
        paxosController.run();
        waitForDay(connection,udpListener,false);
        System.out.println("Client : End PAXOS");

        System.out.println("masuk sini");
        int count =0;
        Integer order;
        String time;
        time = "day";
        while(!connection.isEnded()){
            if(!time.equals(connection.getPhase())){
                //GANTI HARI
                //PILIH LEADER
                System.out.println("Client : Start PAXOS");
                connection.listClient();
                waitForData(connection);
                paxosController = new PaxosController(connection.getPlayerId(),connection);
                udpListener.setProposer(paxosController.getProposer());
                paxosController.run();
                waitForDay(connection,udpListener,false);
                time ="day";
                System.out.println("Client : End PAXOS");
            }
            
            if(connection.getPhase().equals("day")&&(connection.isAlive())){
                //DAYTIME
                System.out.println("DAY TIME");
                System.out.println("List of alive players");
                System.out.println(connection.listClients());
                System.out.println("Vote by typing '<user_id>'");
                connection.listClient();
                waitForData(connection);
                connection.setDataReady(false);
                order = sc.nextInt();
                while(connection.isPlayerExist(order)){
                    System.out.println("User doesn't exist. Please vote again");
                    order = sc.nextInt();
                }
                messenger.sendVoteWerewolf(order);
                
                
            }else if(connection.getPhase().equals("night")&&(connection.isAlive())){
                //NIGHTTIME
                time ="night";
                System.out.println("NIGHT TIME");
                if(connection.getRole().equals("werewolf")){
                    System.out.println("List of alive players");
                    System.out.println(connection.listClients());
                    System.out.println("Vote by typing '<user_id>'");
                    connection.listClient();
                    waitForData(connection);
                    connection.setDataReady(false);
                    order = sc.nextInt();
                    while(connection.isPlayerExist(order)){
                        System.out.println("User doesn't exist. Please vote again");
                        order = sc.nextInt();
                    }
                    messenger.sendVoteCivilian(order);
                }
            }
            waitForDay(connection,udpListener,true);
            count++;
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        WereWolfClient client = new WereWolfClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
