package org.file.synchronization.service.task;

import org.file.synchronization.model.FileEnricher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

 interface ITaskExecutor<T, R> {
    List<CompletableFuture<R>> getAsyncResponse();
    void add(T t);

    int size();

    T getFirst();


    T getLast();

    T removeLast();
    T removeFirst();

    T pollFirst();

    T pollLast();

    boolean isEmpty();
}
