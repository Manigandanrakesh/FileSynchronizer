package org.file.synchronization.service.impl;

import org.file.synchronization.model.FileEnricher;
import org.file.synchronization.model.SiblingFileEnricher;
import org.file.synchronization.service.task.impl.FileTaskExecutorImpl;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class FileSynchronizationImpl extends AbstractFileSynchronization<SiblingFileEnricher, byte[]> {
    public FileSynchronizationImpl(Runnable runnable, Scheduler scheduler, FileEnricher fileEnricher,
                                   FileTaskExecutorImpl taskExecutor) {
        super(runnable, scheduler, fileEnricher, taskExecutor);
    }

    @Override
    public Mono<Queue<byte[]>> getFileBytesOnAsync() {
        return this.getCompressedBytes(this.fileEnricher.getSuperFilePath());
    }

    @Override
    public Mono<Set<String>> getRandomInsertFileNamesOnAsync() {
        return this.getFileBytesOnAsync()
                .map(bytes ->
                    this.taskExecutor.getAsyncResponse(bytes, ()->
                            new SiblingFileEnricher(new StringBuilder(fileEnricher.getStoragePath())
                                    .append(File.separator).append(System.currentTimeMillis()).toString())))
                .flatMap(Mono::fromFuture);
    }

    private Mono<Queue<byte[]>> getCompressedBytes(String filePath){
        return Mono.just(filePath)
                .map(fPath-> {
                    File file = new File(filePath);
                    long maxFetchSize = this.fileEnricher.getMaxFetchSize();
                    if(maxFetchSize > file.length())
                        throw new UnsupportedOperationException("File Max Size Is Increased Than Fetch Size");
                    long start=0;
                    long precendingIndex= this.fileEnricher.getSplitSize().intValue();
                    while (start <= maxFetchSize) {
                        SiblingFileEnricher enricher =new SiblingFileEnricher(filePath, start,
                                Math.min(precendingIndex, maxFetchSize));
                        this.taskExecutor.add(enricher);
                        start=precendingIndex;
                        precendingIndex= Math.addExact(precendingIndex,
                                this.fileEnricher.getSplitSize().intValue());
                    }
                    return this.taskExecutor.getAsyncResponse();
                }).map(this::convertedFromFutureToBytes)
                .flatMap(Mono::fromFuture)
                .publishOn(this.scheduler);
    }

    private CompletableFuture<Queue<byte[]>> convertedFromFutureToBytes(List<CompletableFuture<byte[]>>
                                                                         completableFutures){
        CompletableFuture<Queue<byte[]>> byteFuture=new CompletableFuture<>();
        Queue<byte[]> bytes= new ArrayDeque<>();
        completableFutures.forEach(completableFuture ->
                completableFuture.whenComplete((byteResponse, throwable) -> {
                    if(Objects.nonNull(throwable)){
                        // Log Statement
                        System.out.println(throwable.getMessage());
                    }else {
                        bytes.add(byteResponse);
                    }
                }));
        try {
            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[]{}))
                    .whenComplete((voidType, throwable) -> {
                        if(Objects.nonNull(throwable)){
                            byteFuture.completeExceptionally(throwable);
                        } else {
                            byteFuture.complete(bytes);
                        }
                    });
        } catch (Exception ex) {
            byteFuture.completeExceptionally(ex);
        }
        return byteFuture;
    }
}
