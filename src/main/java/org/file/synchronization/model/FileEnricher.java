package org.file.synchronization.model;

import java.util.Objects;

public class FileEnricher extends BaseEnricher{
    private Long splitSize;

    private Long maxFetchSize;

    private String storagePath;

    public FileEnricher() {
        super(FileEnricher.class);
    }

    public Long getSplitSize() {
        return splitSize;
    }

    public Long getMaxFetchSize() {
        return maxFetchSize;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public static class FileEnricherBuilder{
       private final FileEnricher fileEnricher = new FileEnricher();
       public FileEnricherBuilder(String superFilePath, Long splitSize) {
           this.fileEnricher.superFilePath = superFilePath;
           this.fileEnricher.splitSize = splitSize;
       }

        public static FileEnricherBuilder fileEnricherBuilder(String superFilePath, Long splitSize){
           return new FileEnricherBuilder(superFilePath, splitSize);
        }
       public FileEnricherBuilder setMaxFetchSize(Long maxFetchSize){
           this.fileEnricher.maxFetchSize=maxFetchSize;
           return this;
       }

        public FileEnricherBuilder setStoragePath(String storagePath){
            this.fileEnricher.storagePath= Objects.isNull(storagePath) ||
            storagePath.isBlank() ? "/tmp/" :storagePath;
            return this;
        }

       public FileEnricher buildInstance(){
           return this.fileEnricher;
       }
   }
}
