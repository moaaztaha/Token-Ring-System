/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Coordinator;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author kelwa
 */
public class Coordinator implements Runnable{
    
    static ArrayList<PeerInfo> peers = new ArrayList<PeerInfo>();
    Socket peer;
    static int counter = 0;
    
    public Coordinator(Socket peer)
    {
        this.peer = peer;
    }
    
    @Override
    public void run()
    {
        try
        {
            
            DataInputStream in = new DataInputStream(peer.getInputStream());
            ObjectInputStream min = new ObjectInputStream(in);
            
            // get the message 
            Map <String, String> message = (Map)min.readObject();
            
            
            
            // determine the task
            switch (message.get("task"))
            {
                case "join":
                    // get the port 
                    String port = message.get("port");        
                    // get the port of the first peer
                    String nextPort;
                    if (peers.size() == 0)
                    {
                        nextPort = port;
                    }
                    else
                    {
                        nextPort = peers.get(0).getPort();    
                    }
                    
                    // 
                    System.out.println("Tracking the joining operation");
                    System.out.println("new Peer, port:" + port + "Next port is:" + nextPort);
                    
                    // add it to the arraylist
                    peers.add(new PeerInfo(counter++, port, nextPort));
                    System.out.println("Added to the array list");
                    
                    
                    // send the next peer port for the final one
                    message = new HashMap<String, String>();
                    message.put("task", "update");
                    message.put("port", nextPort);
                    
                    DataOutputStream out = new DataOutputStream(peer.getOutputStream());
                    ObjectOutputStream mOut = new ObjectOutputStream(out);
                    mOut.writeObject(message);
                    
                    
                    // close
                    mOut.close();
                    out.close();
                    // focussss!!!
                    peer.close();
                    

                    // send the next peer port for the previous
                    System.out.println("Sending the the current peer port to the previous one !!!!");
                    int currentIndex = peers.size()-1;
                    if (peers.size() > 1)
                    {
                        System.out.println("Coo:Updating pervious peer!!");
                        updateNext(currentIndex, "join");
                    }

                    break;
                case "leave":
                    // get the port 
                    port = message.get("port");  
                    
                    System.out.println("Peer:"+ port + " is leaving !!!!!!");
                    
                    // inform the pervious peer of its new next port
                        // get the index
                    currentIndex = -1;
                    for (int i=0; i < peers.size(); i++)
                    {
                        if (peers.get(i).getPort().equals(port))
                        {
                            currentIndex = i;
                            break;
                        }
                    }
                    
                    System.out.println("Its index is:" + currentIndex +" !!!!!!!!");
                    
                        // update the previous
                            // to-do what if u have only one peer!!
                    if (currentIndex==0 || currentIndex==-1)
                    {
                        peers.clear();
                    }
                    else
                    {   
                        // update pervious [here and there] + update arraylist
                        System.out.println("Updating the previous peer !!!");
                        updateNext(currentIndex, "leave");
                        // remove current
                        peers.remove(currentIndex);
                    }
                    
                    break;
                    
                case "getPort":
                    if (peers.size() == 0)
                    {
                        message = new HashMap<String, String>();
                        message.put("port", "3000");
                    }
                    else
                    {
                        int p = Integer.parseInt(peers.get(peers.size()-1).getPort()) + 1;
                        message.put("port", Integer.toString(p));
                    }
                    
                    ObjectOutputStream mout = new ObjectOutputStream(new DataOutputStream(peer.getOutputStream()));
                    mout.writeObject(message);
                    
                    mout.close();
                        
                    break;
            }
            
            min.close();
            in.close();
            
        
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
    public static String listPeers()
    {
        String all = "";
        for (PeerInfo p:peers)
        {
            System.out.println(p);
            all += p + "\n";
        }
        all += "*************";
        System.out.println("***************");
        
        peers.get(0).setNextPort("Asdasd");
        peers.get(1).setPort("asdasd");
         return all;   
    }
    
    
    private void updateNext(int currentIndex, String task)
    {
        if (task.equals("leave"))
        {
            try
            {
                Map<String, String> message = new Hashtable<String, String>();
                message.put("task", "update");
                message.put("port", peers.get(currentIndex).getNextPort());

                // u were tacking the leave code
                System.out.println("The prepared Message, port:" + peers.get(currentIndex).getNextPort());
                
                // connect
                String port = peers.get(currentIndex-1).getPort();
                Socket p = new Socket("localhost", Integer.parseInt(port));
                ObjectOutputStream mout = new ObjectOutputStream(
                            new DataOutputStream(p.getOutputStream())
                        );

                mout.writeObject(message);


                // close
                mout.close();
                p.close();

            }
            catch (Exception i)
            {
                System.out.println(i);
            }
        }
        else if (task.equals("join"))
        {
            
            try
            {
                Map<String, String> message = new Hashtable<String, String>();
                message.put("task", "update");
                message.put("port", peers.get(currentIndex).getPort());

                // connect
                String port = peers.get(currentIndex-1).getPort();
                Socket p = new Socket("localhost", Integer.parseInt(port));
                ObjectOutputStream mout = new ObjectOutputStream(
                            new DataOutputStream(p.getOutputStream())
                        );
                

                mout.writeObject(message);
                System.out.println("Updating the peer next port in the arraylist!!!");
                System.out.println("Peer port:" + peers.get(currentIndex-1).getPort());
                System.out.println("it's new next port:"+peers.get(currentIndex).getPort());
                peers.get(currentIndex-1).setNextPort(peers.get(currentIndex).getPort());
                System.out.println("it's new next port after updating:" + peers.get(currentIndex-1).getNextPort());
                // close
                mout.close();
                p.close();

            }
            catch (Exception i)
            {
                System.out.println(i);
            }
        }
        
        
    }
    
    
    public static void main(String args[])
    {
        try
        {
            // init the server
            ServerSocket server = new ServerSocket(2000);
            
            // show ui
            CoordinatorUI cui = new CoordinatorUI();
            cui.setVisible(true);
            
            
            while(true)
            {
                Socket s = server.accept();
                System.out.println("Peer Connected");
                Thread peer = new Thread(new Coordinator(s));
                peer.start();
            }
        
        
        
        }
        catch (Exception i)
        {
            System.out.println(i);
        }
    }
    
}


