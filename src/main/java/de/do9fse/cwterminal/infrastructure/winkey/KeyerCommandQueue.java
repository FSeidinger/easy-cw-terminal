package de.do9fse.cwterminal.infrastructure.winkey;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import de.do9fse.cwterminal.core.model.commands.KeyerCommand;

public class KeyerCommandQueue extends AbstractQueue<KeyerCommand> {
    private final ReentrantLock lock;
    private final Deque<KeyerCommand> queue;

    public KeyerCommandQueue() {
        this.lock = new ReentrantLock();
        this.queue = new ArrayDeque<>();
    }

    public void beginSendTransaction() {
        this.lock.lock();
    }

    public void commitSendTransaction() {
        this.lock.unlock();
    }

    public void rollbackSendTransaction() {
        this.lock.lock();
        this.queue.pollLast();
        this.lock.unlock();
    }

    @Override
    public boolean offer(final KeyerCommand command) {
        this.lock.lock();

        try {
            return this.queue.offer(command);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public KeyerCommand poll() {
        this.lock.lock();

        try {
            return queue.poll();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public KeyerCommand peek() {
        this.lock.lock();

        try {
            return queue.peek();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public Iterator<KeyerCommand> iterator() {
        this.lock.lock();

        try {
            return queue.iterator();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public int size() {
        this.lock.lock();

        try {
            return this.queue.size();
        } finally {
            this.lock.unlock();
        }
    }
}
