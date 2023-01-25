package com.readymadedata.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.LanguageItem;
import com.readymadedata.app.repository.LanguageRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.util.List;

public class LanguageViewModel extends AndroidViewModel {

    public LanguageRepository repository;
    public MutableLiveData<String> languageObj = new MutableLiveData<>();
    PrefManager prefManager;

    public LanguageViewModel(@NonNull Application application) {
        super(application);

        repository = new LanguageRepository(application);
        prefManager = new PrefManager(application);
    }

    public LiveData<Resource<List<LanguageItem>>> getLanguages() {
        languageObj.setValue("PS");
        return Transformations.switchMap(languageObj, obj->{
            if(obj==null){
                return AbsentLiveData.create();
            }
            return repository.getLanguages("sdghhgh416546dd5654wst56w4646w46");
        });
    }

    
}
