package org.lockless.locks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;

public class ExclusiveLocksBaseTest<L extends Lock, T extends Locks<L> & ExclusiveLocks<L>> {

    private final int barrierCount;
    private final int concurrency;

    public ExclusiveLocksBaseTest(int barrierCount, int concurrency) {
        this.barrierCount = barrierCount;
        this.concurrency = concurrency;
    }

    public void exclusiveLockTest(final T lockFac) throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();

        final AtomicInteger counter = new AtomicInteger(1);
        final CyclicBarrier ready = new CyclicBarrier(this.concurrency);
        final CountDownLatch latch = new CountDownLatch(this.concurrency);

        ExecutorService exec = Executors.newFixedThreadPool(this.concurrency);

        for (int i = 0; i < this.concurrency; i++) {
            exec.execute(() -> {
                try {
                    ready.await();
                    try (L lock = lockFac.exclusiveLock()) {
                        Assert.assertTrue("exclusive lock counter has been broken", counter.decrementAndGet() >= 0);
                        counter.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                } catch (Exception e) {
                    if (!ex.compareAndSet(null, e)) {
                        e.printStackTrace();
                    }
                }
            });
        }
        latch.await();

        Exception e = ex.get();
        if (e != null) {
            throw e;
        }
    }

    public void mixedLockingTest(final T lockFac) throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();

        final AtomicInteger lockCounter = new AtomicInteger(this.barrierCount);
        final AtomicInteger exclusiveLockCounter = new AtomicInteger(1);

        final CyclicBarrier ready = new CyclicBarrier(this.concurrency);
        final CountDownLatch done = new CountDownLatch(this.concurrency);

        ExecutorService exec = Executors.newFixedThreadPool(this.concurrency);

        List<Runnable> runnables = new ArrayList<>(this.concurrency);

        for (int i = 0; i < this.concurrency / 2; i++) {
            runnables.add(() -> {
                try {
                    ready.await();
                    try (L lock = lockFac.lock()) {
                        Assert.assertTrue("lock counter has been broken", lockCounter.decrementAndGet() >= 0);
                        lockCounter.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                } catch (Exception e) {
                    if (!ex.compareAndSet(null, e)) {
                        e.printStackTrace();
                    }
                }
            });
        }

        for (int i = 0; i < this.concurrency / 2; i++) {
            runnables.add(() -> {
                try {
                    ready.await();
                    try (L lock = lockFac.exclusiveLock()) {
                        Assert.assertTrue("exclusive lock counter has been broken",
                                exclusiveLockCounter.decrementAndGet() >= 0);
                        exclusiveLockCounter.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                } catch (Exception e) {
                    if (!ex.compareAndSet(null, e)) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Collections.shuffle(runnables);
        Collections.shuffle(runnables);

        runnables.forEach((r) -> exec.execute(r));

        done.await();

        Exception e = ex.get();
        if (e != null) {
            throw e;
        }
    }
}
