package com.ss;

import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import com.hazelcast.map.IMap;
import com.hazelcast.core.DistributedObjectListener;

import java.util.Collection;
/* Sample Distributed Object Listener */
public class ExampleDOL implements DistributedObjectListener{

    public static void main(String[] args) {
        ExampleDOL example = new ExampleDOL();
        Config config = new Config();
        config.getManagementCenterConfig().setScriptingEnabled(true);

        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(config);

        node1.addDistributedObjectListener((DistributedObjectListener) example);
        Collection<DistributedObject> distributedObjects = node1
                .getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            System.out.println(distributedObject.getName());
        }

        final IMap<String, Object> map = node1.getMap("ssmap");


        map.put("1", "Sharath");
        Member member2 = node2.getCluster().getLocalMember();

        boolean member2Safe = node1.getPartitionService().isMemberSafe(member2);

        distributedObjects = node1.getDistributedObjects();

        distributedObjects.forEach(
                distributedObject -> System.out.println("distributed Objects i : " + distributedObject));



        System.out.printf("# Is member2 safe for shutdown\t: %s\n", member2Safe);
    }

    @Override
    public void distributedObjectCreated(DistributedObjectEvent event) {
        DistributedObject instance = event.getDistributedObject();
        System.out.println("Distributed Object Created " + instance.getName());
    }
    @Override
    public void distributedObjectDestroyed(DistributedObjectEvent event) {
        DistributedObject instance = event.getDistributedObject();
        System.out.println("Distributed Object Destroyed " + instance.getName());
    }

}