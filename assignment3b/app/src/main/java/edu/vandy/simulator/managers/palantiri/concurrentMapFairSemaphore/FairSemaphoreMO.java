package edu.vandy.simulator.managers.palantiri.concurrentMapFairSemaphore;

import java.util.LinkedList;

/**
 * Implements a fair semaphore using the Specific Notification pattern
 * (www.dre.vanderbilt.edu/~schmidt/PDF/specific-notification.pdf)
 * using the Java built-in monitor object.  Undergraduate students
 * should implement this class.
 */
public class FairSemaphoreMO
        implements FairSemaphore {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = FairSemaphoreMO.class.getSimpleName();
    /**
     * Set to true if you want debug log output.
     */
    private static Boolean VERBOSE = false;
    /**
     * Define a count of the number of available permits.
     */
    // TODO -- you fill in here.  Make sure that this field will ensure
    // its values aren't cached by multiple threads..
    private volatile int mAvailablePermits;

    /**
     * Define a LinkedList "WaitQueue" that keeps track of the waiters in a FIFO
     * List to ensure "fair" semantics.
     */
    // TODO -- you fill in here.
    private LinkedList<Waiter> mWaitQueue;

    /**
     * Initialize the fields in the class.
     */
    public FairSemaphoreMO(int availablePermits) {
        // TODO -- you fill in here.
        mAvailablePermits = availablePermits;
        mWaitQueue = new LinkedList<>();
    }

    /**
     * Logging helper
     */
    private static void log(String msg) {
        if (VERBOSE) {
            System.out.println(msg);
        }
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.
     */
    @Override
    public void acquireUninterruptibly() {
        // TODO -- you fill in here, using a loop to ignore
        // InterruptedExceptions.
        boolean isInterrupted;
        boolean isInterruptedAtLeastOnce = false;
        do {
            isInterrupted = false;
            try {
                acquire();
            } catch (InterruptedException e) {
                isInterrupted = true;
                isInterruptedAtLeastOnce = true;
            }
        } while (isInterrupted);

        if (isInterruptedAtLeastOnce) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Acquire one permit from the semaphore in a manner that can
     * be interrupted.
     */
    @Override
    public void acquire() throws InterruptedException {
        // Bail out quickly if we've been interrupted.
        if (Thread.interrupted()) {
            throw new InterruptedException();
            // Try to get a permit without blocking.
        } else if (!tryToGetPermit()) {
            // Block until a permit is available.
            waitForPermit();
        }
    }

    /**
     * Handle the case where we can get a permit without blocking.
     *
     * @return Returns true if the permit was obtained, else false.
     */
    protected boolean tryToGetPermit() {
        // First try the "fast path" where the method doesn't need to
        // block if the queue is empty and permits are available.
        //
        // TODO -- you fill in here replacing this statement with your
        //  solution which should be synchronized.
        synchronized (this) {
            return tryToGetPermitUnlocked();
        }
    }

    /**
     * Factors out code that checks to see if a permit can be obtained
     * without blocking.  This method assumes the monitor lock
     * ("intrinsic lock") is held.
     *
     * @return Returns true if the permit was obtained, else false.
     */
    protected boolean tryToGetPermitUnlocked() {
        // We don't need to wait if the queue is empty and
        // permits are available.
        //
        // TODO -- you fill in here replacing this statement with your
        // solution which should not be synchronized.
        if (mWaitQueue.isEmpty() && mAvailablePermits > 0) {
            mAvailablePermits--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Constructs a new Waiter (required for test mocking).
     *
     * @return A new Waiter instance
     */
    protected Waiter createWaiter() {
        return new Waiter();
    }

    /**
     * Handle the case where we need to block since there are already
     * waiters in the queue or no permits are available.
     */
    protected void waitForPermit() throws InterruptedException {
        // Call createWaiter helper method to allocate a new Waiter that
        // acts as the "specific-notification lock".
        final Waiter waiter = createWaiter();

        // TODO -- implement "fair" semaphore acquire semantics using
        // the Specific Notification pattern.
        synchronized (this) {
            if (tryToGetPermitUnlocked())
                return;
        }
        synchronized (waiter) {
            synchronized (this) {
                mWaitQueue.add(waiter);
            }
            try {
                while (!waiter.mReleased)
                    waiter.wait();
            } catch (InterruptedException e) {
                boolean released;
                synchronized (this) {
                    released = mWaitQueue.remove(waiter);
                }
                if (!released)
                    release();
                throw e;
            }
        }
    }

    /**
     * Return one permit to the semaphore.
     */
    @Override
    public void release() {
        // TODO -- implement "fair" semaphore release semantics
        // using the Specific Notification pattern.
        Waiter waiter;
        synchronized (this) {
            waiter = mWaitQueue.pollFirst();
        }
        if (waiter == null)
            mAvailablePermits++;
        else {
            synchronized (waiter) {
                waiter.mReleased = true;
                waiter.notify();
            }
        }
    }

    /**
     * @return The number of available permits.
     */
    @Override
    public int availablePermits() {
        // TODO -- you fill in here replacing this statement with your solution.
        return mAvailablePermits;
    }

    /**
     * Define a class that can be used in the "WaitQueue" to wait for
     * a specific thread to be notified.
     */
    protected static class Waiter {
        /**
         * Keeps track of whether the Waiter was released or not to
         * detected and handle "spurious wakeups".
         */
        boolean mReleased = false;

        /**
         * Constructor used for mocking.
         */
        Waiter() {
        }
    }
}
