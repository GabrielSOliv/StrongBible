package com.devgabriel.bstrong.features.verses.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.devgabriel.bstrong.features.verses.domain.repository.VerseRepository;
import com.devgabriel.bstrong.features.verses.domain.model.Verse;
import java.util.List;

public class VerseViewModel extends ViewModel{
    private final VerseRepository repository;
    private final MutableLiveData<List<Verse>> verses = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public VerseViewModel(VerseRepository repository){
        this.repository = repository;
    }

    public void loadVerses(int bookId, int chapter){
        executorService.execute(() -> {
            List<Verse> result = repository.getVerses(bookId, chapter);
            verses.postValue(result);
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    public LiveData<List<Verse>> getVerses(){
        return verses;
    }
}