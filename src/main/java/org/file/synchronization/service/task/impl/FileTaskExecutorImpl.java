package org.file.synchronization.service.task.impl;

import org.file.synchronization.model.BaseEnricher;
import org.file.synchronization.model.FileEnricher;
import org.file.synchronization.model.SiblingFileEnricher;
import org.file.synchronization.service.task.AbstractTaskExecutor;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FileTaskExecutorImpl extends AbstractTaskExecutor<SiblingFileEnricher,byte[]> {
    public FileTaskExecutorImpl(SiblingFileEnricher fileEnricher) {
        super(fileEnricher);
    }

    @Override
    public List<CompletableFuture<byte[]>> getAsyncResponse() {
        List<CompletableFuture<byte[]>> completableFutures = new ArrayList<>();
       try {
           if (this.isEmpty())
               return Collections.emptyList();
           while (!this.isEmpty()) {
               SiblingFileEnricher enricher = this.pollFirst();
               completableFutures.add(this.getAsync(enricher));
           }
       } catch (Exception ex){
           throw new UnsupportedOperationException(ex.getMessage(), ex);
       }
       return completableFutures;
    }

    public CompletableFuture<Set<String>> getAsyncResponse(Queue<byte[]> bytes,
                                                           Supplier<SiblingFileEnricher> enricher) {
        List<CompletableFuture<String>> futures=new ArrayList<>();
        Set<String> fileNames=new HashSet<>();
        while (!bytes.isEmpty()) {
            CompletableFuture<String> createFilePath = this.getFilePathFuture(bytes.poll(), enricher);
            createFilePath.whenComplete((resp, throwable) -> {
                if(Objects.nonNull(throwable)){
                    // Log
                    System.out.println(throwable.getMessage());
                    return;
                }
                fileNames.add(resp);
            });
            futures.add(createFilePath);
        }
        CompletableFuture<Set<String>> finalFilePathFutures=new CompletableFuture<>();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}))
                .whenComplete((resp, throwable) -> {
                    if(Objects.nonNull(throwable)){
                        finalFilePathFutures.completeExceptionally(throwable);
                        return;
                    }
                    finalFilePathFutures.complete(fileNames);
                });
        return finalFilePathFutures;
    }

    public CompletableFuture<String> getFilePathFuture(byte[] resp, Supplier<SiblingFileEnricher> enricher) {
        CompletableFuture<String> createFilePath=new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            String randomFilePath="";
            try {
                SiblingFileEnricher fileEnricher = enricher.get();
                randomFilePath = fileEnricher.getFilePath();
                File file = new File(randomFilePath);
                if (file.exists())
                    file.delete();
                file.createNewFile();
                FileOutputStream outputStream =new FileOutputStream(file);
                outputStream.write(resp);
                outputStream.flush();
                outputStream.close();
            } catch (Exception ex) {
                createFilePath.completeExceptionally(ex);
            }
            return randomFilePath;
        }).thenAccept(createFilePath::complete);
        return createFilePath;
    }

    private CompletableFuture<byte[]> getAsync(SiblingFileEnricher enricher){
        CompletableFuture<byte[]> future =new CompletableFuture<>();
        try {
          byte[] responseByte= this.createFileAndGenerateByte(enricher);
          future.complete(responseByte);
        }catch (Exception ex){
            future.completeExceptionally(ex);
        }
        return future;
    }

    private byte[] createFileAndGenerateByte(SiblingFileEnricher enricher) throws IOException {
        FileInputStream inputStream=null;
        try {
            String filePath= Objects.isNull(enricher.getFilePath()) ?
                    enricher.getSuperFilePath(): enricher.getFilePath();
            File file = new File(filePath);
            if(!file.exists()) return new byte[]{};
            byte[] bytes= new byte[enricher.getEndIndex().intValue()];
            inputStream = new FileInputStream(filePath);
            int count= inputStream.read(bytes, enricher.getStartIndex().intValue(), enricher.getEndIndex().intValue());
            inputStream.close();
            return (count != -1) ? bytes: new byte[]{};
        } catch (Exception ex){
            throw new UnsupportedOperationException(ex.getMessage(),ex);
        } finally {
            if(Objects.nonNull(inputStream))
                inputStream.close();
        }
    }
}
