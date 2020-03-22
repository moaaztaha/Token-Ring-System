/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Coordinator;


/**
 *
 * @author kelwa
 */
public class PeerInfo {
    
    private int id;
    private String port;
    private String nextPort;
    private boolean token;

    public PeerInfo(int id, String port, String nextPort) {
        this.port = port;
        this.nextPort = nextPort;
        this.id = id;
        token = false;
    }
    
    
    public void setPort(String port)
    {
        this.port = port;
    }
    
    public String getPort()
    {
        return port;
    }
    
    public void setNextPort(String nextPort)
    {
        this.nextPort = nextPort;
    }
    
    public String getNextPort()
    {
        return nextPort;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setToken(boolean token)
    {
        this.token = token;
    }
    
    public boolean getToken()
    {
        return token;
    }
    
    
    @Override
    public String toString()
    {
        return "Peer{ID:" +id+ ",port:" + port +", next Port:"+nextPort+", Has Token:"+token+'}';
    }
    
    
    
    
    
}
