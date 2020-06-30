
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.internal.datastructures.lock.Lock;
import com.hazelcast.map.IMap;

import java.io.Serializable;
import java.util.Collection;

public class PessimisticUpdateMember {

    public static void main(String[] args) throws Exception {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        IMap<String, Value> map = hz.getMap("map");

        String key = "1";
        Collection distributedObjects;
        map.put(key, new Value());
        System.out.println("Starting");
        for (int i = 0; i < 100; i++) {
            map.lock(key);
            try {
                Value value = map.get(key);
                Thread.sleep(10);
                value.amount++;
                map.put(key, value);

                distributedObjects=hz.getDistributedObjects();

                distributedObjects.forEach(
                        distributedObject->System.out.println("distributed Objects i : " + distributedObject));


            } finally {
                map.unlock(key);
            }
        }
        System.out.println("Finished! Result = " + map.get(key).amount);

        Hazelcast.shutdownAll();
    }

    static final class Value implements Serializable {

        private int amount;
    }
}
