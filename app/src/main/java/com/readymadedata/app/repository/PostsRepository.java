package com.readymadedata.app.repository;

import android.app.Application;
import android.util.Log;

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
import com.readymadedata.app.database.PostDao;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class PostsRepository {

    public AppDatabase db;
    public PostDao postDao;

    public MediatorLiveData<Resource<List<PostItem>>> result = new MediatorLiveData<>();
    public MediatorLiveData<Resource<List<PostItem>>> trending_result = new MediatorLiveData<>();

    public PostsRepository(Application application) {
        db = AppDatabase.getInstance(application);
        postDao = db.getPostDao();
    }

    public LiveData<Resource<List<PostItem>>> getById(String apiKey, String festId, String type, String language, boolean isVideo) {
        return new NetworkBoundResource<List<PostItem>, List<PostItem>>() {
                @Override
                protected void saveCallResult(@NonNull List<PostItem> item) {
                    try {
                        Log.e("Size--->", String.valueOf(item.size()));
                        db.runInTransaction(() -> {
                            if (language.equals("")) {
                                postDao.deleteByFestId(festId, type, isVideo);
                            } else {
                                postDao.deleteByFestId(festId, type, language, isVideo);
                            }
                            postDao.insertAll(item);

                        });
                    } catch (Exception ex) {
                        Util.showErrorLog("Error at ", ex);
                    }
                }

                @Override
                protected boolean shouldFetch(@Nullable List<PostItem> data) {
                    return Config.IS_CONNECTED;
                }

                @NonNull
                @Override
                protected LiveData<List<PostItem>> loadFromDb() {
                    if (language.equals("")) {
                        return postDao.getByFestId(festId, type, isVideo);
                    }
                    return postDao.getByLanguage(festId, type, language, isVideo);
                }

                @NonNull
                @Override
                protected LiveData<ApiResponse<List<PostItem>>> createCall() {
//                    if (isVideo) {
//                        return ApiClient.getApiService().getVideosById(apiKey, festId, type);
//                    } else {
//                        if (type.equals(Constant.CUSTOM)) {
//                            return ApiClient.getApiService().getCustomPost(apiKey, festId);
//                        }
//                        if (type.equals(Constant.BUSINESS)) {
//                            return ApiClient.getApiService().getBusinessPost(apiKey, festId);
//                        }
                        return ApiClient.getApiService().getPost(apiKey, type, festId);
//                    }
                }
            }.asLiveData();
    }

    public LiveData<Resource<List<PostItem>>> getTrendingPost(String language) {
        List<PostItem> postItemList = new ArrayList<>();

        try {
            db.runInTransaction(() -> {
//                postDao.deleteTrending(true);
                for (int i = 0; i < postItemList.size(); i++) {
                    postDao.insert(postItemList.get(i));
                }
            });
        } catch (Exception ex) {
            Util.showLog("Error at " + ex);
        }

        if (language.equals("")) {
            trending_result.addSource(postDao.getTrending(true), data -> {
                trending_result.setValue(Resource.success(data));
            });
        } else {
            trending_result.addSource(postDao.getTrendingByLang(true, language), data -> {
                trending_result.setValue(Resource.success(data));
            });
        }
        return trending_result;
    }

}
