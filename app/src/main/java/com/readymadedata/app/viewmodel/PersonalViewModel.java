package com.readymadedata.app.viewmodel;

import android.app.Application;
import android.content.ContentResolver;

import androidx.browser.trusted.sharing.ShareTarget;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.Config;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.api.common.NetworkBoundResource;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.PersonalItem;
import com.readymadedata.app.repository.PersonalRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonalViewModel extends AndroidViewModel {
    public MutableLiveData<String> businessObj = new MutableLiveData<>();
    private MutableLiveData<String> defaultObj = new MutableLiveData<>();
    private MutableLiveData<String> deleteimgObj = new MutableLiveData<>();
    private MutableLiveData<String> imgObj = new MutableLiveData<>();
    public boolean isLoading = false;
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    public MutableLiveData<TmpData> onObj = new MutableLiveData<>();
    PrefManager prefManager;
    public PersonalRepository repository;
    public LiveData<Resource<List<PersonalItem>>> results;
    public LiveData<Resource<PersonalItem>> resultsBusiness;
    private MutableLiveData<String> updateimgObj = new MutableLiveData<>();

    public PersonalViewModel(Application application) {
        super(application);
        this.repository = new PersonalRepository(application);
        this.prefManager = new PrefManager(application);
    }

    public void setPersonalObj(String userId) {
        this.businessObj.setValue(userId);
    }

    public LiveData<Resource<List<PersonalItem>>> getPersonal() {
        return this.results;
    }

//    public LiveData<Resource<List<PersonalItem>>> addPersonal(String name, String profileImagePath, String email, String phone, String address, ContentResolver contentResolver) {
//        this.updateimgObj.setValue("PS");
//        return Transformations.switchMap(this.updateimgObj, new PersonalViewModel(this.getApplication(), profileImagePath, name, email, phone, address));
//    }


    public LiveData<Resource<List<PersonalItem>>> addPersonal(String apiKey, String userId, String filePath, String name, String email, String phone, String address,String faceb,String instauser) {
        String str = filePath;
        MultipartBody.Part body = null;
        RequestBody fullName = null;

        if (!str.equals("")) {
            File file = new File(str);
            body = MultipartBody.Part.createFormData("logo", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
            fullName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
        }
        RequestBody useIdRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), userId);
        RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);
        RequestBody emailRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), email);
        RequestBody addressRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), address);
        RequestBody phoneRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), phone);
        RequestBody instaUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), instauser);
        RequestBody faceUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), faceb);

        MultipartBody.Part finalBody = body;
        return new NetworkBoundResource<List<PersonalItem>, List<PersonalItem>>() {
            List<PersonalItem> resultsDb;

            /* access modifiers changed from: protected */
            public void saveCallResult(List<PersonalItem> list) {
            }

            /* access modifiers changed from: protected */
            public boolean shouldFetch(List<PersonalItem> list) {
                return Config.IS_CONNECTED;
            }

            /* access modifiers changed from: protected */
            public LiveData<List<PersonalItem>> loadFromDb() {
                if (this.resultsDb == null) {
                    return AbsentLiveData.create();
                }
                return new LiveData<List<PersonalItem>>() {
                    /* access modifiers changed from: protected */
                    public void onActive() {
                        super.onActive();
                        setValue(resultsDb);
                    }
                };
            }

            public LiveData<ApiResponse<List<PersonalItem>>> createCall() {
                return ApiClient.getApiService().addPersonal(apiKey, useIdRB, finalBody, nameRB, emailRB, phoneRB, addressRB,instaUsernameRB,faceUsernameRB);
            }
        }.asLiveData();
    }







    private class TmpData {
        String businessId = "";

        private TmpData() {
        }
    }

    public void setLoadingState(Boolean state) {
        this.isLoading = state.booleanValue();
        this.loadingState.setValue(state);
    }

    public MutableLiveData<Boolean> getLoadingState() {
        return this.loadingState;
    }
}
