package org.lockless.locks;

import org.junit.Test;

public class BinaryLocksTest {

    private static final int BARRIER_COUNT = 1;
    private static final int CONCURRENCY   = 50;

    private final LocksBaseTest<Lock, BinaryLocks> locksBase = new LocksBaseTest<>(BARRIER_COUNT, CONCURRENCY);

    @Test
    public void lockTest() throws Exception {
        BinaryLocks locks = new BinaryLocks();
        this.locksBase.lockTest(locks);
    }

}
