package com.ss;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.concurrent.BlockingQueue;

public class Client {

    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("127.0.0.1");
        // Connect to Azure AKS temp
        // clientConfig.getNetworkConfig().addAddress("52.154.205.44:5701");
        clientConfig.getNetworkConfig().setSmartRouting(false);
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        System.out.println(clientConfig.toString());
        BlockingQueue<String> queue = client.getQueue("queue");
        queue.put("Hello!");
        System.out.println("Message sent by Hazelcast Client!");
        IMap<String, Object> myMap = client.getMap("myMap");
        myMap.put("1", "value1");
        System.out.println("myMap value is " + myMap.get("1")) ;
        HazelcastClient.shutdownAll();
    }
}
