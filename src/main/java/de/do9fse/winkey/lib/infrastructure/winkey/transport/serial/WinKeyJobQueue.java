package de.do9fse.winkey.lib.infrastructure.winkey.transport.serial;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;

final class WinKeyJobQueue {
    private final Lock lock = new ReentrantLock();
    private final Queue<WinKeyJob> jobs = new LinkedBlockingQueue<>();

    @FunctionalInterface
    interface JobSender {
        void send(WinKeyJob job) throws WinKeyApplicationException;
    }

    void submit(final WinKeyJob job, final JobSender sender) throws WinKeyApplicationException {
        lock.lock();
        try {
            jobs.offer(job);
            try {
                sender.send(job);
            } catch (final WinKeyApplicationException | RuntimeException exception) {
                jobs.remove(job);
                throw exception;
            }
        } finally {
            lock.unlock();
        }
    }

    WinKeyJob peekActiveJob() {
        lock.lock();
        try {
            return jobs.peek();
        } finally {
            lock.unlock();
        }
    }

    void consumeActiveJob(
        final WinKeyResponse response,
        final Consumer<WinKeyCommand> onConsumed
    ) {
        lock.lock();
        try {
            final WinKeyJob job = jobs.remove();
            onConsumed.accept(job.command());
            job.response().complete(response);
        } finally {
            lock.unlock();
        }
    }

    void failPendingJobs(final Exception exception) {
        lock.lock();
        try {
            while (failActiveJob(exception)) {
                // Keep submissions from interleaving with the drain.
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean failActiveJob(final Exception exception) {
        lock.lock();
        try {
            final WinKeyJob job = jobs.poll();
            if (job == null) {
                return false;
            }
            job.response().completeExceptionally(exception);
            return true;
        } finally {
            lock.unlock();
        }
    }
}
