package org.file.synchronization.model;

public class SiblingFileEnricher extends BaseEnricher{
    private final String filePath;
    private Long startIndex;
    private Long endIndex;

    public SiblingFileEnricher(String filePath, Long startIndex, Long endIndex) {
        super(SiblingFileEnricher.class);
        this.filePath = filePath;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public SiblingFileEnricher(String filePath) {
        super(SiblingFileEnricher.class);
        this.filePath = filePath;
    }
    public String getFilePath() {
        return filePath;
    }

    public Long getStartIndex() {
        return startIndex;
    }

    public Long getEndIndex() {
        return endIndex;
    }
}
