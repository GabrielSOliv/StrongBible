package com.devgabriel.bstrong.features.verses.domain.model;

import java.util.List;

public class WordToken {
    private final String word;
    private final List<String> strongCodes;

    public WordToken(String word, List<String> strongCodes) {
        this.word = word;
        this.strongCodes = strongCodes;
    }

    public String getWord() { return word; }
    public List<String> getStrongCodes() { return strongCodes; }
    public boolean hasStrong() { return strongCodes != null && !strongCodes.isEmpty(); }
}
