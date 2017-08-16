package org.lockless.locks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;

public class ReentrantBaseTest<L extends Lock, T extends Locks<L>> {

    public void illegalThreadExceptionTest(T lockFac) throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Lock lock = lockFac.lock();

        Thread t = new Thread(() -> {
            try {
                lock.unlock();
            } catch (Exception e) {
                if (!ex.compareAndSet(null, e)) {
                    e.printStackTrace();
                }
            } finally {
                latch.countDown();
            }
        });
        t.start();

        latch.await();

        Exception e = ex.get();
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof IllegalThreadStateException);
    }

    public void threadReenterTest(T lockFac) throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try (Lock outer = lockFac.lock()) {
                try (Lock inner = lockFac.lock()) {
                    Assert.assertSame(outer, inner);
                }
            } catch (Exception e) {
                if (!ex.compareAndSet(null, e)) {
                    e.printStackTrace();
                }
            } finally {
                latch.countDown();
            }
        });
        t.start();

        latch.await();

        Exception e = ex.get();
        if (e != null) {
            throw e;
        }
    }

    public <E extends ExclusiveLocks<L>> void exclusiveThreadReenterTest(E lockFac) throws Exception {
        final AtomicReference<Exception> ex = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try (Lock outer = lockFac.exclusiveLock()) {
                try (Lock inner = lockFac.exclusiveLock()) {
                    Assert.assertSame(outer, inner);
                }
            } catch (Exception e) {
                if (!ex.compareAndSet(null, e)) {
                    e.printStackTrace();
                }
            } finally {
                latch.countDown();
            }
        });
        t.start();

        latch.await();

        Exception e = ex.get();
        if (e != null) {
            throw e;
        }
    }
}
