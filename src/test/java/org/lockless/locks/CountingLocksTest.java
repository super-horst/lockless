package org.lockless.locks;

import org.junit.Test;

public class CountingLocksTest {

    private static final int BARRIER_COUNT = 10;
    private static final int CONCURRENCY   = 50;

    private final LocksBaseTest<Lock, CountingLocks> locksBase = new LocksBaseTest<>(BARRIER_COUNT, CONCURRENCY);

    private final ExclusiveLocksBaseTest<Lock, CountingLocks> exclusiveLocksBase = new ExclusiveLocksBaseTest<>(
            BARRIER_COUNT, CONCURRENCY);

    @Test
    public void lockTest() throws Exception {
        CountingLocks locks = new CountingLocks(BARRIER_COUNT);
        this.locksBase.lockTest(locks);
    }

    @Test
    public void exclusiveLockTest() throws Exception {
        CountingLocks locks = new CountingLocks(BARRIER_COUNT);
        this.exclusiveLocksBase.exclusiveLockTest(locks);
    }

    @Test
    public void mixedLockingTest() throws Exception {
        CountingLocks locks = new CountingLocks(BARRIER_COUNT);
        this.exclusiveLocksBase.mixedLockingTest(locks);
    }

}
