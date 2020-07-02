package com.ss ;

import com.hazelcast.config.Config;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPSubsystemManagementService;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.cp.session.CPSessionManagementService;
import com.hazelcast.map.IMap;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Configures a CP subsystem of 3 CP members. When you run 3 instances of this
 * class, it will form the CP subsystem. Then, it fetches
 * a CP {@link FencedLock} proxy. Each CP member acquires the lock 2 times and
 * releases it afterwards. Between acquires and releases, it prints the fencing
 * tokens assigned to itself.
 */

public class CPMember {

    private static final int CP_MEMBER_COUNT = 3;

    public static void main(String[] args) throws InterruptedException , java.util.concurrent.ExecutionException {
        Config config = new Config();
        config.getCPSubsystemConfig()
                .setCPMemberCount(CP_MEMBER_COUNT)
                .setPersistenceEnabled(false)
        ;
        config.setProperty("hazelcast.jmx", "true");
        config.getManagementCenterConfig().setScriptingEnabled(true);
        config.setLicenseKey("ENT#3Nodes#2Y8UKAE0DPJy9mNd5gBCnXqwjuOGM61SQWliHTZbkf16910000000011001011001200110910000193101700");

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);

        com.ss.ExampleDOL example = new com.ss.ExampleDOL();
        hz.addDistributedObjectListener((DistributedObjectListener) example);


        FencedLock lock = hz.getCPSubsystem().getLock("lock");

        long fence1 = lock.lockAndGetFence();
        final IMap<String, Object> map = hz.getMap("ssmap");


        map.put("1", "Sharath");

        System.out.println("I acquired the lock for the first time at " + new Date() + " with fence: " + fence1);

        Thread.sleep(SECONDS.toMillis(1) + new Random().nextInt(100));

        long fence2 = lock.lockAndGetFence();

        System.out.println("I acquired the lock reentrantly with fence: " + fence2);

        /* Get all groups */
        CPSubsystemManagementService managementService = hz.getCPSubsystem()
                .getCPSubsystemManagementService();
        CompletionStage<Collection<CPGroupId>> future = managementService.getCPGroupIds();
        Collection<CPGroupId> groups = future.toCompletableFuture().get();
        System.out.println("Note: Groups are " + groups);

        /* Get all sessions */
        Iterator iter = groups.iterator();
        groups.forEach( group -> {
            CPSessionManagementService sessionManagementService = hz.getCPSubsystem()
                    .getCPSessionManagementService();
            CompletionStage<Collection<CPSession>> futureSessions = sessionManagementService
                    .getAllSessions(String.valueOf(group));
            Collection<CPSession> sessions = null;
            try {
                sessions = futureSessions.toCompletableFuture().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println(" Sessions are :" + sessions);
                }

        );



        Thread.sleep(SECONDS.toMillis(2));

        // calling unlock() two times since we acquired the lock 2 times

        System.out.println("Unlocking...");

        lock.unlock();

        System.out.println("I still hold the lock with fence: " + lock.getFence());

        Thread.sleep(SECONDS.toMillis(2));

        System.out.println("Unlocking again...");

        lock.unlock();

        System.out.println("Do I still hold the lock? " + lock.isLockedByCurrentThread());

        Thread.sleep(SECONDS.toMillis(30));

        hz.getLifecycleService().terminate();
    }

}

