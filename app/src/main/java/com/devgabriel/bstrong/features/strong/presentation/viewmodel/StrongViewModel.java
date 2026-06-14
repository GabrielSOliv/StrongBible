package com.devgabriel.bstrong.features.strong.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.devgabriel.bstrong.features.strong.domain.model.StrongEntry;
import com.devgabriel.bstrong.features.strong.domain.repository.StrongRepository;

public class StrongViewModel extends ViewModel{
    private final StrongRepository repository;
    private final MutableLiveData<StrongEntry> strong = new MutableLiveData<>();

    public StrongViewModel(StrongRepository repository){
        this.repository = repository;
    } 
    public void loadStrong(String code){
        StrongEntry result = repository.getStrong(code);
        strong.setValue(result);
    }
    public LiveData<StrongEntry> getStrong(){
        return strong;
    }
}