package org.lockless.locks;

import org.junit.Test;

public class ReentrantCountingLocksTest {

    private static final int BARRIER_COUNT = 10;
    private static final int CONCURRENCY   = 50;

    private final LocksBaseTest<ReentrantLock, ReentrantCountingLocks> locksBase = new LocksBaseTest<>(BARRIER_COUNT,
            CONCURRENCY);

    private final ExclusiveLocksBaseTest<ReentrantLock, ReentrantCountingLocks> exclusiveLocksBase = new ExclusiveLocksBaseTest<>(
            BARRIER_COUNT, CONCURRENCY);

    private final ReentrantBaseTest<ReentrantLock, ReentrantCountingLocks> reentrantBase = new ReentrantBaseTest<>();

    @Test
    public void lockTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.locksBase.lockTest(locks);
    }

    @Test
    public void exclusiveLockTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.exclusiveLocksBase.exclusiveLockTest(locks);
    }

    @Test
    public void mixedLockingTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.exclusiveLocksBase.mixedLockingTest(locks);
    }

    @Test
    public void illegalThreadExceptionTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.reentrantBase.illegalThreadExceptionTest(locks);
    }

    @Test
    public void threadReenterTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.reentrantBase.threadReenterTest(locks);
    }

    @Test
    public void exclusiveThreadReenterTest() throws Exception {
        ReentrantCountingLocks locks = new ReentrantCountingLocks(BARRIER_COUNT);
        this.reentrantBase.exclusiveThreadReenterTest(locks);
    }
}
