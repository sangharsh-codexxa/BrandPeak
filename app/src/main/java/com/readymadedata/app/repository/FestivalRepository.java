package com.readymadedata.app.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.readymadedata.app.Config;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.api.common.NetworkBoundResource;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.database.AppDatabase;
import com.readymadedata.app.database.FestivalDao;
import com.readymadedata.app.items.FestivalItem;
import com.readymadedata.app.utils.Util;

import java.util.List;

public class FestivalRepository {

    public AppDatabase db;
    public FestivalDao festivalDao;

    private MediatorLiveData<Resource<List<FestivalItem>>> result = new MediatorLiveData<>();

    public FestivalRepository(Application application) {

        db = AppDatabase.getInstance(application);
        festivalDao = db.getFestivalDao();

    }

    public LiveData<Resource<List<FestivalItem>>> getResult(String apiKey, String page) {
        return new NetworkBoundResource<List<FestivalItem>, List<FestivalItem>>() {
            @Override
            protected void saveCallResult(@NonNull List<FestivalItem> item) {
                try {
                    db.runInTransaction(() -> {
                        festivalDao.deleteTable();
                        festivalDao.insert(item);
                    });
                } catch (Exception ex) {
                    Util.showErrorLog("Error at ", ex);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<FestivalItem> data) {
                return Config.IS_CONNECTED;
            }

            @NonNull
            @Override
            protected LiveData<List<FestivalItem>> loadFromDb() {
                return festivalDao.getFestivals();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<FestivalItem>>> createCall() {
                return ApiClient.getApiService().getFestival(apiKey, page);
            }
        }.asLiveData();
    }

}
