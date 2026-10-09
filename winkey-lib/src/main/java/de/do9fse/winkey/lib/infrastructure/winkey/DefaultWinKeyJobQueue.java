package de.do9fse.winkey.lib.infrastructure.winkey;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.port.out.WinKeyJobQueue;

public class DefaultWinKeyJobQueue implements WinKeyJobQueue {
    private final Lock lock = new ReentrantLock();
    private final Deque<WinKeyJob> jobs = new LinkedBlockingDeque<>();
   
    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @Override
    public WinKeyJob peek() {
        this.lock.lock();
        try {
            return this.jobs.peek();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public WinKeyJob poll() {
        this.lock.lock();
        try {
            return this.jobs.poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(final WinKeyJob job) {
        this.lock.lock();
        try {
            return this.jobs.offer(job);
        } finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public WinKeyJob pollLast() {
        return this.jobs.pollLast();
    }

    @Override
    public void failPendingJobs(final Exception exception) {
        this.lock.lock();
        try {
            WinKeyJob job;
            while ((job = jobs.poll()) != null) {
                job.response().completeExceptionally(exception);
            }
        } finally {
            lock.unlock();
        }
    }
}
