package com.devgabriel.bstrong.features.books.domain.model;

public class Book {
    private final int id;
    private final int testamentId;
    private final String name;
    private final String shortName;

    public Book(int id, int testamentId, String name, String shortName) {
        this.id = id;
        this.testamentId = testamentId;
        this.name = name;
        this.shortName = shortName;
    }

    public int getId() { return id; }
    public int getTestamentId() { return testamentId; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
}
