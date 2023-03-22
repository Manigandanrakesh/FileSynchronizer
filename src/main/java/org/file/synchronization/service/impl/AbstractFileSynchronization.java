package org.file.synchronization.service.impl;

import org.file.synchronization.model.FileEnricher;
import org.file.synchronization.service.IFileSynchronization;
import org.file.synchronization.service.task.AbstractTaskExecutor;
import org.file.synchronization.service.task.impl.FileTaskExecutorImpl;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

public abstract class AbstractFileSynchronization<T, R> implements IFileSynchronization<T,R>, Scheduler {
     protected final Runnable runnable;
     protected final Scheduler scheduler;

     protected final FileEnricher fileEnricher;

     protected final FileTaskExecutorImpl taskExecutor;

    public AbstractFileSynchronization(Runnable runnable, Scheduler scheduler, FileEnricher fileEnricher,
                                       FileTaskExecutorImpl taskExecutor) {
        this.runnable = runnable;
        this.scheduler=scheduler;
        this.fileEnricher=fileEnricher;
        this.taskExecutor=taskExecutor;
    }

    @Override
    public Disposable schedule(Runnable task, long delay, TimeUnit unit) {
        return this.scheduler.schedule(task, delay, unit);
    }

    @Override
    public Disposable schedulePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return this.scheduler.schedulePeriodically(task, initialDelay, period, unit);
    }

    @Override
    public long now(TimeUnit unit) {
        return this.scheduler.now(unit);
    }

    @Override
    public void dispose() {
        this.scheduler.dispose();
    }

    @Override
    public Mono<Void> disposeGracefully() {
        return this.scheduler.disposeGracefully();
    }

    @Override
    public void start() {
        this.scheduler.start();
    }

    @Override
    public void init() {
        this.scheduler.init();
    }

    @Override
    public Disposable schedule(Runnable task) {
        return this.scheduler.schedule(this.execute(task));
    }

    @Override
    public Worker createWorker() {
        return this.scheduler.createWorker();
    }

    @Override
    public Runnable execute(Runnable task) {
        return ()-> {
            this.runnable.run();
            task.run();
        };
    }

    @Override
    public Runnable execute() {
        return this.runnable;
    }

    @Override
    public void run() {
        this.execute().run();
    }
}
