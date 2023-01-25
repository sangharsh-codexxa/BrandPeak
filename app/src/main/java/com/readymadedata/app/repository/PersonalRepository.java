package com.readymadedata.app.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.messaging.Constants;
import com.readymadedata.app.Config;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.api.common.NetworkBoundResource;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.database.AppDatabase;
import com.readymadedata.app.database.BusinessDao;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.PersonalItem;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.Util;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalRepository {
    public Application application;
//    public BusinessDao businessDao;

    /* renamed from: db */
    public AppDatabase db;
    private MediatorLiveData<Resource<PersonalItem>> personalBusiness = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<PersonalItem>>> result = new MediatorLiveData<>();

    public PersonalRepository(Application application2) {
        this.application = application2;
        AppDatabase instance = AppDatabase.getInstance(application2);
        this.db = instance;
//        this.businessDao = instance.getBusinessDao();
    }

    public LiveData<Resource<List<PersonalItem>>> getPersonal() {
        return new NetworkBoundResource<List<PersonalItem>, List<PersonalItem>>() {
            /* access modifiers changed from: protected */
            public void saveCallResult(List<PersonalItem> list) {
            }

            /* access modifiers changed from: protected */
            public boolean shouldFetch(List<PersonalItem> list) {
                return Config.IS_CONNECTED;
            }

            /* access modifiers changed from: protected */
            public LiveData<List<PersonalItem>> loadFromDb() {
                return null;
            }

            /* access modifiers changed from: protected */
            public LiveData<ApiResponse<List<PersonalItem>>> createCall() {
                Util.showLog("BUSINESS LOAD");
                return ApiClient.getApiService().getPersonal(Constant.api_key, Constant.USER_ID);
            }
        }.asLiveData();
    }




}
