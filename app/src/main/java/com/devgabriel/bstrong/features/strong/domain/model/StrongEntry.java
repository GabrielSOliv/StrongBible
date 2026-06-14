package com.devgabriel.bstrong.features.strong.domain.model;

public class StrongEntry {
    private final String code;
    private final String language;
    private final String originalWord;
    private final String transliteration;
    private final String meaning;

    public StrongEntry(String code, String language, String originalWord, String transliteration, String meaning) {
        this.code = code;
        this.language = language;
        this.originalWord = originalWord;
        this.transliteration = transliteration;
        this.meaning = meaning;
    }

    public String getCode() { return code; }
    public String getLanguage() { return language; }
    public String getOriginalWord() { return originalWord; }
    public String getTransliteration() { return transliteration; }
    public String getMeaning() { return meaning; }
}
