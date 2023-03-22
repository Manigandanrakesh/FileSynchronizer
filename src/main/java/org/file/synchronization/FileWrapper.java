package org.file.synchronization;


import org.file.synchronization.model.FileEnricher;
import org.file.synchronization.model.SiblingFileEnricher;
import org.file.synchronization.service.impl.FileSynchronizationImpl;
import org.file.synchronization.service.task.impl.FileTaskExecutorImpl;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;

public class FileWrapper {
    public static void main(String[] args) {
        System.out.println("File Wrapper Execution ! Started");
        FileSynchronizationImpl fileSynchronization = new FileSynchronizationImpl(() -> System.out.println("Execution File Processing"),
                Schedulers.parallel(), FileEnricher.FileEnricherBuilder.
                fileEnricherBuilder("resources/sample.txt", Long.valueOf(100))
                .setMaxFetchSize(Long.valueOf(1024 * 100)).setStoragePath("resources/").buildInstance(),
                new FileTaskExecutorImpl(new SiblingFileEnricher("resources/sample.txt")));
        // Sync
        Set<String> fileNames = fileSynchronization.getRandomInsertFileNamesOnAsync().block();
        System.out.println(fileNames);
        System.out.println("File Wrapper Execution ! Sync Completed");
        // Async
        fileSynchronization.getRandomInsertFileNamesOnAsync().doOnError(throwable -> {
                    System.out.println(throwable.getMessage());
                }).doOnNext(System.out::println)
                .doFinally(signalType -> System.out.println("File Wrapper Execution ! Async Completed"))
                .subscribe();
    }
}