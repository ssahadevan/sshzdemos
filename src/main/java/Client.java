package com.ss;


import com.hazelcast.client.HazelcastClient;
        import com.hazelcast.client.config.ClientConfig;
        import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public class Client {

    public static void main(String[] args) throws Exception {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().addAddress("127.0.0.1");


        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        System.out.println(clientConfig.toString());

        BlockingQueue<String> queue = client.getQueue("queue");
        queue.put("Hello!");
        System.out.println("Message sent by Hazelcast Client!");


        IMap<String, Object> myMap = client.getMap("myMap");
        myMap.put("1", "Sharath");

        System.out.println("myMap value is " + myMap.get("1")) ;

        myMap.lock("1");

        try
        {
            // critical section code.


         Collection distributedObjects = client.getDistributedObjects();

         distributedObjects.forEach(
                 distributedObject->System.out.println(distributedObject));

        }
        finally
        {
            myMap.unlock("1");
        }

        HazelcastClient.shutdownAll();
    }
}
