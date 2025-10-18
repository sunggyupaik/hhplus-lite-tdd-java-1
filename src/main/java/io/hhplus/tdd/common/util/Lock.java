package io.hhplus.tdd.common.util;

import io.hhplus.tdd.point.UserPoint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Lock {
    private final Map<Long, ReentrantLock> lockTable = new HashMap<>();

    public void lock(long id) {
        ReentrantLock reentrantLock = lockTable.getOrDefault(id, new ReentrantLock());
        lockTable.put(id, reentrantLock);

        reentrantLock.lock();
    }

    public void unlock(long id) {
        ReentrantLock reentrantLock = lockTable.getOrDefault(id, new ReentrantLock());
        lockTable.put(id, reentrantLock);

        reentrantLock.unlock();
    }
}
