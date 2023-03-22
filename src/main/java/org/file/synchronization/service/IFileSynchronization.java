package org.file.synchronization.service;

import reactor.core.publisher.Mono;

import java.util.Queue;
import java.util.Set;

public interface IFileSynchronization<T, R> extends Runnable{
    Mono<Queue<R>> getFileBytesOnAsync();
    Mono<Set<String>> getRandomInsertFileNamesOnAsync();
    Runnable execute(Runnable task);
    Runnable execute();
}
