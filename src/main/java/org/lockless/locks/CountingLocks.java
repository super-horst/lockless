package org.lockless.locks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

/**
 * Distributes a fixed number of {@link Lock Locks} to be run concurrently. May
 * be used to ensure a limited access of threads to a critical section.
 * <p>
 * Provides exclusive locks, which occupy all slots
 * 
 * @author super-horst
 */
public final class CountingLocks implements Locks<Lock>, ExclusiveLocks<Lock> {

    private final IntUnaryOperator adder = new SlotAwareAdder();

    private final AtomicInteger counter = new AtomicInteger();
    private final Lock          lock    = () -> this.counter.decrementAndGet();

    private final BinaryLocks exclLock = new BinaryLocks();
    private final Lock[]      locks;

    private final int slots;

    /**
     * Create new instance
     * 
     * @param slots
     *            number of locks to be handed out concurrently
     */
    public CountingLocks(int slots) {
        this.slots = slots;
        this.locks = new Lock[this.slots];
    }

    @Override
    public Lock lock() {
        while (this.counter.getAndUpdate(this.adder) == this.slots) {
            // do something else if you need to
            Thread.yield();
        }

        return this.lock;
    }

    @Override
    public Lock exclusiveLock() {
        try (Lock lock = this.exclLock.lock()) {
            for (int i = 0; i < this.slots; i++) {
                this.locks[i] = lock();
            }
        }

        return new ExclusiveLock<Lock>(this.locks);
    }

    private class SlotAwareAdder implements IntUnaryOperator {
        @Override
        public int applyAsInt(int operand) {
            return operand >= CountingLocks.this.slots ? CountingLocks.this.slots : operand + 1;
        }
    }
}
