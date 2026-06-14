package com.devgabriel.bstrong.features.strong.domain.repository;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;

public interface StrongRepository {
    StrongEntry getStrong(String code);
}

