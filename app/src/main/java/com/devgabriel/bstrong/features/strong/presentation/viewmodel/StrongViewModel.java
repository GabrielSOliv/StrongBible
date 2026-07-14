package com.devgabriel.bstrong.features.strong.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;
import com.devgabriel.bstrong.features.strong.domain.repository.StrongRepository;

public class StrongViewModel extends ViewModel{
    private final StrongRepository repository;
    private final MutableLiveData<StrongEntry> strong = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public StrongViewModel(StrongRepository repository){
        this.repository = repository;
    } 
    public void loadStrong(String code){
        executorService.execute(() -> {
            StrongEntry result = repository.getStrong(code);
            strong.postValue(result);
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    public LiveData<StrongEntry> getStrong(){
        return strong;
    }
}