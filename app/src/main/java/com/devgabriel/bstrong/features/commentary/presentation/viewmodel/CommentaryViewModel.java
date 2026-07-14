package com.devgabriel.bstrong.features.commentary.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.devgabriel.bstrong.features.commentary.domain.repository.CommentaryRepository;
import com.devgabriel.bstrong.features.commentary.domain.model.Commentary;

import java.util.ArrayList;
import java.util.List;

public class CommentaryViewModel extends ViewModel {
    private final CommentaryRepository repository;
    private final MutableLiveData<List<Commentary>> commentaries = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CommentaryViewModel(CommentaryRepository repository){
        this.repository = repository;
    }

    public void loadCommentaries(int bookId, int chapterId){
        executorService.execute(() -> {
            List<Commentary> result = repository.getCommentariesForChapter(bookId, chapterId);
            commentaries.postValue(result);
        });
    }

    public void loadCommentaryForVerse(int bookId, int chapterId, int verse) {
        executorService.execute(() -> {
            Commentary commentary = repository.getCommentaryForVerse(bookId, chapterId, verse);
            List<Commentary> list = new ArrayList<>();
            if (commentary != null) list.add(commentary);
            commentaries.postValue(list);
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public LiveData<List<Commentary>> getCommentaries(){
        return commentaries;
    }
}