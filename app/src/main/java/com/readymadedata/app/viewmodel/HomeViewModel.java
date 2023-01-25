package com.readymadedata.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.HomeItem;
import com.readymadedata.app.repository.HomeRepository;

import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.util.List;

public class HomeViewModel extends AndroidViewModel  {

    HomeRepository homeRepository;
    public LiveData<Resource<HomeItem>> result;

//    public LiveData<List<Banner>> getBannerData() {
//        return bannerData;
//    }

//    public void setBannerData(LiveData<List<Banner>> bannerData) {
//        this.bannerData = bannerData;
//    }
//
//    public LiveData<List<Banner>> bannerData;
    public MutableLiveData<String> homeObj = new MutableLiveData<>();

    PrefManager prefManager;

    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        prefManager = new PrefManager(application);
        homeRepository = new HomeRepository(application);

        result = Transformations.switchMap(homeObj, obj->{
            if(obj == null){
                return AbsentLiveData.create();
            }
            return homeRepository.getHomeData("sdghhgh416546dd5654wst56w4646w46");
        });

    }



    public void setHomeObj(String obj){
        homeObj.setValue(obj);
    }

    public LiveData<Resource<HomeItem>> getHomeData(){
        return result;
    }

    public boolean isLoading = false;


    //region For loading status
    public void setLoadingState(Boolean state) {
        isLoading = state;
        loadingState.setValue(state);
    }

    public MutableLiveData<Boolean> getLoadingState() {
        return loadingState;
    }
}
