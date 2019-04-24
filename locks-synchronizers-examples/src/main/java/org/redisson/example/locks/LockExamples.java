/**
 * Copyright (c) 2016-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.example.locks;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class LockExamples {

    public static void main(String[] args) throws InterruptedException {
        // connects to 127.0.0.1:6379 by default
        Config config = new Config();
//        config.setTransportMode(TransportMode.EPOLL);
        config.useSingleServer()
                // use "rediss://" for SSL connection
                .setAddress("redis://127.0.0.1:6379")
                .setPassword(null);
        RedissonClient redisson = Redisson.create(config);

        RLock lock = redisson.getLock("lock");
        lock.lock(30, TimeUnit.SECONDS);
        System.out.printf("main get lock");

        Thread t = new Thread() {
            public void run() {
                RLock lock1 = redisson.getLock("lock");
                System.out.println("thread get lock");
                lock1.lock();
                lock1.unlock();
            };
        };

        t.start();
        t.join();

        lock.unlock();
        
        redisson.shutdown();
    }
    
}
