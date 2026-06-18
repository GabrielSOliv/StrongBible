package com.devgabriel.bstrong.features.commentary.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devgabriel.bstrong.features.commentary.domain.repository.CommentaryRepository;
import java.util.List;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;

public class CommentaryViewModel extends ViewModel {
    private final CommentaryRepository repository;
    private final MutableLiveData<List<Commentary>> commentaries = new MutableLiveData<>();

    public CommentaryViewModel(CommentaryRepository repository){
        this.repository = repository;
    } 
    public void loadCommentaries(int bookId, int chapterId){
        List<Commentary> result = repository.getCommentariesForChapter(bookId, chapterId);
        commentaries.setValue(result);
    }
    public LiveData<List<Commentary>> getCommentaries(){
        return commentaries;
    }
}