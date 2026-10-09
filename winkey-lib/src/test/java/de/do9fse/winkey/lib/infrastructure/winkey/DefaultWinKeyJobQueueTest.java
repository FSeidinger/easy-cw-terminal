package de.do9fse.winkey.lib.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.commands.host.BackspaceCommand;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;
import de.do9fse.winkey.lib.core.port.out.WinKeyJobQueue;

class DefaultWinKeyJobQueueTest {
    @Test
    void supportsTheWinKeyQueueContractIncludingRemovingTheTail() {
        final WinKeyJobQueue queue = new DefaultWinKeyJobQueue();
        final WinKeyJob firstJob = newJob();
        final WinKeyJob lastJob = newJob();

        assertNull(queue.peek());
        assertNull(queue.poll());
        assertNull(queue.pollLast());

        assertTrue(queue.offer(firstJob));
        assertTrue(queue.offer(lastJob));
        assertSame(firstJob, queue.peek());
        assertSame(lastJob, queue.pollLast());
        assertSame(firstJob, queue.poll());
        assertNull(queue.pollLast());
    }

    @Test
    void lockPreventsTheReceiverFromPollingUntilTheSenderUnlocks() throws Exception {
        final WinKeyJobQueue queue = new DefaultWinKeyJobQueue();
        final WinKeyJob job = newJob();
        final CountDownLatch pollStarted = new CountDownLatch(1);
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        queue.offer(job);
        final Future<WinKeyJob> polledJob;

        queue.lock();
        try {
            polledJob = executor.submit(() -> {
                pollStarted.countDown();
                return queue.poll();
            });

            assertTrue(pollStarted.await(1, TimeUnit.SECONDS));
            assertFalse(polledJob.isDone());
        } finally {
            queue.unlock();
        }

        try {
            assertSame(job, polledJob.get(1, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

    private static WinKeyJob newJob() {
        return new WinKeyJob(new BackspaceCommand(), new CompletableFuture<WinKeyResponse>());
    }
}
