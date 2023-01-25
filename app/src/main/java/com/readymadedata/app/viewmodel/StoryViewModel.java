package com.readymadedata.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.StoryItem;
import com.readymadedata.app.repository.StoryRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.util.List;

public class StoryViewModel extends AndroidViewModel {

    public LiveData<Resource<List<StoryItem>>> result;
    public MutableLiveData<String> storyObj = new MutableLiveData<>();

    public StoryRepository storyRepository;
    PrefManager prefManager;

    public StoryViewModel(@NonNull Application application) {
        super(application);
        storyRepository = new StoryRepository(application);
        prefManager = new PrefManager(application);

        result = Transformations.switchMap(storyObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return storyRepository.getStory("sdghhgh416546dd5654wst56w4646w46");
        });
    }

    public LiveData<Resource<List<StoryItem>>> getStoryData() {
        return result;
    }

    public void setStoryObj(String data) {
        storyObj.setValue(data);
    }

}
