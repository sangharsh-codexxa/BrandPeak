package com.readymadedata.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.repository.PostsRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.util.List;

public class PostViewModel extends AndroidViewModel {

    private PostsRepository postsRepository;
    private MutableLiveData<TmpPost> postObj = new MutableLiveData<>();
    private LiveData<Resource<List<PostItem>>> getTrendingPost;

    private MutableLiveData<TmpPost> postByIdObj = new MutableLiveData<>();
    private LiveData<Resource<List<PostItem>>> getByIdPost;

    PrefManager prefManager;

    public PostViewModel(@NonNull Application application) {
        super(application);
        postsRepository = new PostsRepository(application);
        prefManager = new PrefManager(application);
        getTrendingPost = Transformations.switchMap(postObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return postsRepository.getTrendingPost(obj.language);
        });

        getByIdPost = Transformations.switchMap(postByIdObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return postsRepository.getById("sdghhgh416546dd5654wst56w4646w46", obj.festId, obj.type, obj.language, obj.isVideo);
        });

    }

    public void setTrendingPost(boolean isTrending, String language) {
        TmpPost tmpPost = new TmpPost();
        tmpPost.isTraining = isTrending;
        tmpPost.festId = "";
        tmpPost.type = "";
        tmpPost.language = "";
        postObj.setValue(tmpPost);
    }

    public void setPostByIdObj(String id, String type, String language, boolean isVideo) {
        TmpPost tmpPost = new TmpPost();
        tmpPost.isTraining = false;
        tmpPost.isVideo = isVideo;
        tmpPost.festId = id;
        tmpPost.type = type;
        tmpPost.language = language;
        postByIdObj.setValue(tmpPost);
    }

    public LiveData<Resource<List<PostItem>>> getById() {
        return getByIdPost;
    }

    public LiveData<Resource<List<PostItem>>> getTrendingPost() {
        return getTrendingPost;
    }

    private class TmpPost {
        public String festId = "";
        public String type = "";
        public String language = "";
        public boolean isTraining = false;
        public boolean isVideo = false;
    }
}
