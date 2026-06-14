package com.devgabriel.bstrong.features.verses.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.devgabriel.bstrong.features.verses.domain.repository.VerseRepository;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import java.util.List;

public class VerseViewModel extends ViewModel{
    private final VerseRepository repository;
    private final MutableLiveData<List<Verse>> verses = new MutableLiveData<>();

    public VerseViewModel(VerseRepository repository){
        this.repository = repository;
    }

    public void loadVerses(int bookId, int chapter){
        List<Verse> result = repository.getVerses(bookId, chapter);
        verses.setValue(result);
    }
    public LiveData<List<Verse>> getVerses(){
        return verses;
    }
}