package com.readymadedata.app.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.readymadedata.app.Config;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.api.common.NetworkBoundResource;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.database.AppDatabase;
import com.readymadedata.app.database.HomeDao;
import com.readymadedata.app.items.HomeItem;
import com.readymadedata.app.utils.Util;

import java.util.List;

public class HomeRepository {

    AppDatabase db;
    HomeDao homeDao;

    public HomeRepository(Application application) {
        db = AppDatabase.getInstance(application);
        homeDao = db.getHomeDao();
    }


    public LiveData<Resource<HomeItem>> getHomeData(String apiKey) {
        return new NetworkBoundResource<HomeItem, HomeItem>() {
            @Override
            protected void saveCallResult(@NonNull HomeItem item) {

                try {
                    db.runInTransaction(() -> {
                        homeDao.deleteTable();
                        homeDao.insertAll(item);
                    });
                } catch (Exception ex) {
                    Util.showErrorLog("Error at ", ex);
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable HomeItem data) {
                return Config.IS_CONNECTED;
            }

            @NonNull
            @Override
            protected LiveData<HomeItem> loadFromDb() {
                return homeDao.getHomeItem();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<HomeItem>> createCall() {
                return ApiClient.getApiService().getHomeData(apiKey);
            }
        }.asLiveData();
    }


}
