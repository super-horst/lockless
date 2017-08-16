package org.lockless.locks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;

public class SynchronisingBaseTest<L extends Lock, T extends Locks<L>> {

    private final int barrierCount;
    private final int concurrency;

    public SynchronisingBaseTest(int barrierCount, int concurrency) {
        this.barrierCount = barrierCount;
        this.concurrency = concurrency;
    }

    public void lockTest() throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();

        final AtomicInteger counter = new AtomicInteger(this.barrierCount);
        final CyclicBarrier ready = new CyclicBarrier(this.concurrency);
        final CountDownLatch done = new CountDownLatch(this.concurrency);

        ExecutorService exec = Executors.newFixedThreadPool(this.concurrency);

        for (int i = 0; i < this.concurrency; i++) {
            exec.execute(() -> {
                try {
                    ready.await();
                    synchronized (this) {
                        Assert.assertTrue("lock counter has been broken", counter.decrementAndGet() >= 0);
                        counter.incrementAndGet();
                        done.countDown();
                    }
                } catch (Exception e) {
                    if (!ex.compareAndSet(null, e)) {
                        e.printStackTrace();
                    }
                }
            });
        }
        done.await();

        Exception e = ex.get();
        if (e != null) {
            throw e;
        }
    }
}
