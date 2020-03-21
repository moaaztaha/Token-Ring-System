/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Peer;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author kelwa
 */
public class Peer implements Runnable{
    private static String port;
    private static String nextPort;
    private static boolean hasToken;
    private static String token;
    
    Socket scSocket;
    
    public Peer(Socket scSocket)
    {
        this.scSocket = scSocket;
    }
    
    
    public static void join()
    {
        Map<String, String> message = new HashMap<String, String>();
        message.put("task", "join");
        message.put("port", port);
        
        
        try
        {
            Socket mServer = new Socket("localhost", 2000);
            
            //helper 
            System.out.println("Peer:Connected to main Server");
            
            // in,out
            DataOutputStream out = new DataOutputStream(mServer.getOutputStream());
            DataInputStream in = new DataInputStream(mServer.getInputStream());
            
            // send map
            ObjectOutputStream mout = new ObjectOutputStream(out);
            mout.writeObject(message);
            
            //helper
            System.out.println("Peer:Message sent to main server");
            
            
            // recieve input
            ObjectInputStream min = new ObjectInputStream(in);
            message = (Map)min.readObject();
            
            if (message.get("task").equals("update"))
            {
                nextPort = message.get("port");
            }
            
            // new Port updated
            System.out.println("New port updated " + nextPort);
        
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
    public static void leave()
    {
        Map<String, String> message = new HashMap<String, String>();
        message.put("task", "leave");
        message.put("port", port);
        
        try
        {
            // connect
            Socket mServer = new Socket("localhost", 2000);
            ObjectOutputStream mout = new ObjectOutputStream(
                    new DataOutputStream(mServer.getOutputStream())
            );
            
            mout.writeObject(message);
            
            // close
            mout.close();
            mServer.close();
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
    
    @Override
    public void run()
    {
        try
        {
            // get the input stream
            ObjectInputStream min = new ObjectInputStream(
                        new DataInputStream(scSocket.getInputStream())
                    );
            
            Map <String, String> message = (Map)min.readObject();            
            
            // figure which task
            switch(message.get("task"))
            {
                case "update":
                    nextPort = message.get("port");
                    System.out.println("Nextport updated through run "+nextPort+ " current " + port);
                    break;
            }
            
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
    
    
    public static String getPort()
    {
        return port;
    }
    
    public static String getNextPort()
    {
        return nextPort;
    }
    
    public static void main(String args[])
    {
        try
        {
            // get the new port
            Socket server = new Socket("localhost", 2000);
            Map<String, String> message = new Hashtable<String, String>();
            message.put("task", "getPort");
            ObjectOutputStream mout = new ObjectOutputStream(new DataOutputStream(server.getOutputStream()));
            mout.writeObject(message);
            ObjectInputStream min = new ObjectInputStream(new DataInputStream(server.getInputStream()));
            message = (Map)min.readObject();
            port = message.get("port");
            System.out.println("Port:" + port);
            min.close();
            mout.close();
            server.close();
            
            
            int iPort = Integer.parseInt(port);
            ServerSocket peerServer = new ServerSocket(iPort);
            // show UI
            PeerUI pui = new PeerUI();
            pui.setVisible(true);
            
            while (true)
            {
                Socket scSocket = peerServer.accept();
                Thread peer = new Thread(new Peer(scSocket));
                peer.start();
            }
        
        
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
}
