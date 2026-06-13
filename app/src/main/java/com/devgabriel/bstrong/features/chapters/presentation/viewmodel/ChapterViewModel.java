package com.devgabriel.bstrong.features.chapters.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devgabriel.bstrong.features.chapters.domain.repository.ChapterRepository;
import java.util.List;

public class ChapterViewModel extends ViewModel{
    private final ChapterRepository repository;
    private final MutableLiveData<List<Integer>> chapters = new MutableLiveData<>();

    public ChapterViewModel(ChapterRepository repository){
        this.repository = repository;
    } 
    public void loadChapters(int bookId){
        List<Integer> result = repository.getChapters(bookId);
        chapters.setValue(result);
    }
    public LiveData<List<Integer>> getChapters(){
        return chapters;
    }
}