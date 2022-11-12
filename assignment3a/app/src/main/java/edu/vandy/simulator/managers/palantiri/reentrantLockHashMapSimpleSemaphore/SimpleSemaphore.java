package edu.vandy.simulator.managers.palantiri.reentrantLockHashMapSimpleSemaphore;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class defines a counting semaphore with "fair" semantics that
 * are implemented using a Java ReentrantLock and ConditionObject.
 */
public class SimpleSemaphore {
    /**
     * Define a count of the number of available permits.
     */
    // TODO - you fill in here.  Ensure that this field will ensure
    // its values aren't cached by multiple threads..
    private volatile int mPermits;

    /**
     * Define a Lock to protect critical sections.
     */
    // TODO - you fill in here
    private Lock mLock;

    /**
     * Define a Condition that's used to wait while the number of
     * permits is 0.
     */
    // TODO - you fill in here
    private Condition mCondition;

    /**
     * Default constructor used for regression tests.
     */
    public SimpleSemaphore() {
    }

    /**
     * Constructor initialize the fields.
     */
    public SimpleSemaphore(int permits) {
        // TODO -- you fill in here making sure the ReentrantLock has
        // "fair" semantics.
        mPermits = permits;
        mLock = new ReentrantLock(true);
        mCondition = mLock.newCondition();
    }

    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    public void acquire() throws InterruptedException {
        // TODO -- you fill in here, make sure the lock is always
        // released, e.g., even if an exception occurs.
        mLock.lockInterruptibly();
        try {
            while (mPermits <= 0)
                mCondition.await();
            mPermits--;
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.  If an interrupt occurs while this method is
     * running make sure the interrupt flag is reset when the method
     * returns.
     */
    public void acquireUninterruptibly() {
        // TODO -- you fill in here, make sure to call your acquire()
        // implementation in a loop to avoid code duplication.
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

        if (isInterruptedAtLeastOnce)
            Thread.currentThread().interrupt();
    }

    /**
     * Return one permit to the semaphore in a manner that can be
     * interrupted.
     */
    public void release() throws InterruptedException {
        // TODO -- you fill in here, make sure the lock is always
        // released, e.g., even if an exception occurs.
        mLock.lockInterruptibly();
        try {
            mPermits++;
            if (mPermits > 0)
                mCondition.signal();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Returns the current number of permits.
     */
    protected int availablePermits() {
        // TODO -- you fill in here replacing this statement with your solution.
        return mPermits;
    }
}
