package org.file.synchronization.model;

public class BaseEnricher {

    protected String superFilePath;
    private final Class<? extends BaseEnricher> toStringVal;

    public BaseEnricher(Class<? extends BaseEnricher> baseEnricherClass) {
        this.toStringVal = baseEnricherClass;
    }

    public String getSuperFilePath() {
        return superFilePath;
    }

    @Override
    public String toString() {
        return toStringVal.toString();
    }
}
