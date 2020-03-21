
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kelwa
 */

import java.util.*;
import Coordinator.PeerInfo;

public class test {
    public static void main(String args[])
    {
        ArrayList<PeerInfo> peers = new ArrayList<>();
        peers.add(new PeerInfo(1, "ASdasd", "Asdasd"));
        peers.get(0).setNextPort("zzzzzzzs");
        System.out.println(peers.get(0).getNextPort());
        
        
    }
}
