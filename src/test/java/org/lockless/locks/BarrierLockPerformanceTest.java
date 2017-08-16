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
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;

public class BarrierLockPerformanceTest {

	private static final int BARRIER_COUNT = 10;
	private static final int CONCURRENCY = 50;
	private static final int LEVERAGE = 1000;

	private static final int VM_WARMUP = 100;

//	@Test
	public void mixedLockTest() throws Exception {
		DescriptiveStatistics warmupStat = new DescriptiveStatistics();
		IntStream.range(0, VM_WARMUP).forEach((i) -> {
			System.out.println("Warmup " + i);
			try {
				mixedLockInternalTest(warmupStat);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		System.gc();
		// Compute some statistics
		double mean = warmupStat.getMean();
		double std = warmupStat.getStandardDeviation();
		double median = warmupStat.getPercentile(50);

		System.out.println(String.format("[Warmup] Avg: %f , StdDev: %f, Median: %f", mean, std, median));

		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();

		IntStream.range(0, VM_WARMUP).forEach((i) -> {
			System.out.println("Run " + i);
			try {
				mixedLockInternalTest(stats);
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		// Compute some statistics
		mean = stats.getMean();
		std = stats.getStandardDeviation();
		median = stats.getPercentile(50);

		System.out.println(String.format("Avg: %f , StdDev: %f, Median: %f", mean, std, median));
	}

	public void mixedLockInternalTest(DescriptiveStatistics stats) throws Exception {
		final AtomicReference<Exception> ex = new AtomicReference<>();
		final int leveragedConcurrency = CONCURRENCY * LEVERAGE;

		final AtomicInteger lockCounter = new AtomicInteger(BARRIER_COUNT);
		final AtomicInteger exclusiveLockCounter = new AtomicInteger(1);

		final ExecutorService exec = Executors.newFixedThreadPool(CONCURRENCY);
		final CyclicBarrier ready = new CyclicBarrier(CONCURRENCY);

		final CountDownLatch done = new CountDownLatch(leveragedConcurrency);

		final CountingLocks lockFac = new CountingLocks(BARRIER_COUNT);

		List<Runnable> runnables = new ArrayList<>(leveragedConcurrency);

		for (int i = 0; i < leveragedConcurrency / 2; i++) {
			runnables.add(() -> {
				try {
					ready.await();
					try(Lock lock = lockFac.lock();) {
						Assert.assertTrue("lock counter has been broken", lockCounter.decrementAndGet() >= 0);
					} finally {
						lockCounter.incrementAndGet();
						done.countDown();
					}
				} catch (Exception e) {
					if (!ex.compareAndSet(null, e)) {
						e.printStackTrace();
					}
				}
			});
		}

		for (int i = 0; i < leveragedConcurrency / 2; i++) {
			runnables.add(() -> {
				try {
					ready.await();
					try(Lock lock = lockFac.exclusiveLock();) {
						Assert.assertTrue("exclusive lock counter has been broken",
								exclusiveLockCounter.decrementAndGet() >= 0);
					} finally {
						exclusiveLockCounter.incrementAndGet();
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

		long start = System.currentTimeMillis();
		runnables.forEach((r) -> exec.execute(r));
		done.await();
		stats.addValue(System.currentTimeMillis() - start);

		Exception e = ex.get();
		if (e != null) {
			throw e;
		}
	}
}
