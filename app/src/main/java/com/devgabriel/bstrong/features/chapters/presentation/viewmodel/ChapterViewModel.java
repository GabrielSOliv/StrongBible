package com.devgabriel.bstrong.features.chapters.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.devgabriel.bstrong.features.chapters.domain.repository.ChapterRepository;
import java.util.List;

public class ChapterViewModel extends ViewModel{
    private final ChapterRepository repository;
    private final MutableLiveData<List<Integer>> chapters = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ChapterViewModel(ChapterRepository repository){
        this.repository = repository;
    } 
    public void loadChapters(int bookId){
        executorService.execute(() -> {
            List<Integer> result = repository.getChapters(bookId);
            chapters.postValue(result);
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    public LiveData<List<Integer>> getChapters(){
        return chapters;
    }
}