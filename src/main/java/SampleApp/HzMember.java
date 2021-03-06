package SampleApp;

import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Collection;
import com.hazelcast.cluster.Member;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import com.hazelcast.map.IMap;
import com.hazelcast.core.DistributedObjectListener;

import java.util.Collection;

public class HzMember {

       /*
         * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         * http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */


    public static void main(String[] args) {
       // com.ss.ExampleDOL example = new com.ss.ExampleDOL();
        Config config = new Config();
        config.getManagementCenterConfig().setScriptingEnabled(true);
        RestApiConfig restApiConfig = new RestApiConfig()
                .setEnabled(true)
                .disableAllGroups()
                .enableGroups(RestEndpointGroup.DATA)
                .enableGroups(RestEndpointGroup.CLUSTER_READ)
                .enableGroups(RestEndpointGroup.CLUSTER_WRITE)
                .enableGroups(RestEndpointGroup.HEALTH_CHECK)
                ;
        config.getNetworkConfig().setRestApiConfig(restApiConfig);

        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(config);


        /*
        node1.addDistributedObjectListener((DistributedObjectListener) example);
         */
        Collection<DistributedObject> distributedObjects = node1
                .getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            System.out.println(distributedObject.getName());
        }


        final IMap<String, Object> map = node1.getMap("ssmap");


        map.put("1", "Sharath");
        Member member2 = node2.getCluster().getLocalMember();

        boolean member2Safe = node1.getPartitionService().isMemberSafe(member2);

        System.out.printf("# Is member2 safe for shutdown\t: %s\n", member2Safe);
    }

    public void HzMember() {
        System.out.println("In HzMember Constructor");
    }

}
