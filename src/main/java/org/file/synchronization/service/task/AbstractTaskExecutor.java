package org.file.synchronization.service.task;

import org.file.synchronization.model.BaseEnricher;
import org.file.synchronization.model.FileEnricher;
import org.file.synchronization.service.task.ITaskExecutor;

import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AbstractTaskExecutor<T, R> implements ITaskExecutor<T, R> {
    protected final BaseEnricher baseEnricher;
    private final ConcurrentLinkedDeque<T> deque;

    protected AbstractTaskExecutor(BaseEnricher baseEnricher) {
        this.baseEnricher = baseEnricher;
        this.deque = new ConcurrentLinkedDeque<>();
    }

    @Override
    public void add(T t) {
        this.deque.add(t);
    }

    @Override
    public int size() {
        return this.deque.size();
    }

    @Override
    public T getFirst() {
        return this.deque.getFirst();
    }

    @Override
    public T getLast() {
        return this.deque.getLast();
    }

    @Override
    public T removeLast() {
        return this.deque.removeLast();
    }

    @Override
    public T removeFirst() {
        return this.deque.removeFirst();
    }

    @Override
    public boolean isEmpty() {
        return this.deque.isEmpty();
    }

    @Override
    public T pollFirst() {
        return this.deque.pollFirst();
    }

    @Override
    public T pollLast() {
        return this.deque.pollLast();
    }
}
