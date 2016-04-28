/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Client.ClientProtocol;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author fauzanrifqy
 */
public class WereWolfClient {
    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Werewolf Game");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JTextArea playerList = new JTextArea(8, 40);
    ClientProtocol clientProtocol = new ClientProtocol();

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public WereWolfClient() {

        // Layout GUI
        textField.setEditable(false);
        messageArea.setEditable(false);
        playerList.setEditable(false);
        playerList.setPreferredSize(new Dimension(20, playerList.getPreferredSize().height));
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(playerList, "East");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
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
        Thread t1 = new Thread(connection);
        t1.start();
        
        UDPListener udpListener = new UDPListener(connection.getLocalPort());
        Thread t2 = new Thread(udpListener);
        t2.start();
        
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("Cara Komunikasi : ");
            System.out.println("UDP <spasi> Address <spasi> Port <spasi> Pesan");
            String message = sc.nextLine();
            String messages[] = message.split(" ");
            if(messages[0].equals("UDP")){
//                System.out.println("masuk sini");
                UDPSender udpSender = new UDPSender(messages[1],Integer.parseInt(messages[2]) , messages[3]);
//                System.out.println(messages[1]);
                Thread t3 = new Thread(udpSender);
                t3.start();
            }
            else{
                
            }
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
