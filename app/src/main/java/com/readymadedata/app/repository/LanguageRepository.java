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
import com.readymadedata.app.database.LanguageDao;
import com.readymadedata.app.items.LanguageItem;
import com.readymadedata.app.utils.Util;

import java.util.List;

public class LanguageRepository {

    public Application application;
    public LanguageDao languageDao;
    public AppDatabase db;

    private MediatorLiveData<Resource<List<LanguageItem>>> result = new MediatorLiveData<>();

    public LanguageRepository(Application application) {
        this.application = application;
        db = AppDatabase.getInstance(application);
        languageDao = db.getLanguageDao();
    }

    public LiveData<Resource<List<LanguageItem>>> getLanguages(String apiKey) {
       return new NetworkBoundResource<List<LanguageItem>, List<LanguageItem>>() {
           @Override
           protected void saveCallResult(@NonNull List<LanguageItem> item) {
               try {
                   db.runInTransaction(() -> {
                       languageDao.deleteTable();
                       languageDao.insetAll(item);
                   });
               } catch (Exception ex) {
                   Util.showErrorLog("Error at ", ex);
               }
           }

           @Override
           protected boolean shouldFetch(@Nullable List<LanguageItem> data) {
               return Config.IS_CONNECTED;
           }

           @NonNull
           @Override
           protected LiveData<List<LanguageItem>> loadFromDb() {
               return languageDao.getLanguages();
           }

           @NonNull
           @Override
           protected LiveData<ApiResponse<List<LanguageItem>>> createCall() {
               return ApiClient.getApiService().getLanguages(apiKey);
           }
       }.asLiveData();
    }

    public void updateLanguage(String languageId, boolean isUpdate){
        languageDao.UpdateLanguage(isUpdate, languageId);
    }
}
