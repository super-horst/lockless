package org.lockless.locks;

import org.junit.Test;

public class ReentrantBinaryLocksTest {

    private static final int BARRIER_COUNT = 1;
    private static final int CONCURRENCY   = 50;

    private final LocksBaseTest<ReentrantLock, ReentrantBinaryLocks>     locksBase     = new LocksBaseTest<>(
            BARRIER_COUNT, CONCURRENCY);
    private final ReentrantBaseTest<ReentrantLock, ReentrantBinaryLocks> reentrantBase = new ReentrantBaseTest<>();

    @Test
    public void lockTest() throws Exception {
        ReentrantBinaryLocks locks = new ReentrantBinaryLocks();
        this.locksBase.lockTest(locks);
    }

    @Test
    public void illegalThreadExceptionTest() throws Exception {
        ReentrantBinaryLocks locks = new ReentrantBinaryLocks();
        this.reentrantBase.illegalThreadExceptionTest(locks);
    }

    @Test
    public void threadReenterTest() throws Exception {
        ReentrantBinaryLocks locks = new ReentrantBinaryLocks();
        this.reentrantBase.threadReenterTest(locks);
    }
}
