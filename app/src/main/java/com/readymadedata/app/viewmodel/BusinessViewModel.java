package com.readymadedata.app.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.Config;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.api.ApiService;
import com.readymadedata.app.api.common.NetworkBoundResource;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.PoliticalItem;
import com.readymadedata.app.repository.BusinessRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BusinessViewModel extends AndroidViewModel {

    public BusinessRepository repository;
    public LiveData<Resource<List<BusinessItem>>> results;
    public MutableLiveData<String> businessObj = new MutableLiveData<>();

    public LiveData<Resource<BusinessItem>> resultsBusiness;
    public MutableLiveData<TmpData> onObj = new MutableLiveData<>();

    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    public boolean isLoading = false;
    PrefManager prefManager;

    // for image upload
    private MutableLiveData<String> imgObj = new MutableLiveData<>();
    private MutableLiveData<String> updateimgObj = new MutableLiveData<>();
    private MutableLiveData<String> deleteimgObj = new MutableLiveData<>();
    private MutableLiveData<String> defaultObj = new MutableLiveData<>();

    public BusinessViewModel(@NonNull Application application) {
        super(application);
        repository = new BusinessRepository(application);

        prefManager = new PrefManager(application);

        results = Transformations.switchMap(businessObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getBusiness("sdghhgh416546dd5654wst56w4646w46", obj);
        });

        resultsBusiness = Transformations.switchMap(onObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.getBusinessData(obj.businessId);
        });
    }

    public void setBusinessObj(String userId) {
        businessObj.setValue(userId);
    }

    public LiveData<Resource<List<BusinessItem>>> getBusiness() {
        return results;
    }

    public LiveData<Resource<List<BusinessItem>>> addBusiness(String name, String profileImagePath, Uri imageUri, String email, String phone,
                                                              String website, String address, boolean b, String userId, ContentResolver contentResolver) {
        updateimgObj.setValue("PS");

        return Transformations.switchMap(updateimgObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Log.e("ApiKey--->","sdghhgh416546dd5654wst56w4646w46");
            return repository.addBusiness(profileImagePath, imageUri,
                    userId, name, email, phone, website, address, b, contentResolver);
        });
    }

    public LiveData<Resource<List<PoliticalItem>>> addPolitical(String apiKey, String filePath, Uri imageUri, String userId, String name, String designation, String phone, String website, String address, boolean b, ContentResolver contentResolver) {
        String str = filePath;
        MultipartBody.Part body = null;
        RequestBody fullName = null;
        Util.showLog("File: " + str);
        if (!str.equals("")) {
            File file = new File(str);
            body = MultipartBody.Part.createFormData("bussinessImage", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
            fullName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
        }
        RequestBody useIdRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), userId);
        RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);
        RequestBody designationRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), designation);
        RequestBody phoneRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), phone);
        RequestBody facebookUsername = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), website);
         MultipartBody.Part finalBody = body;
         RequestBody finalFullName = fullName;
        String str2 = apiKey;
        RequestBody requestBody1 = useIdRB;
        RequestBody requestBody2 = nameRB;
        RequestBody requestBody3 = designationRB;
        RequestBody requestBody4 = phoneRB;
        RequestBody requestBody5 = facebookUsername;
         RequestBody create = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), address);
        return new NetworkBoundResource<List<PoliticalItem>, List<PoliticalItem>>() {
            List<PoliticalItem> resultsDb;

            /* access modifiers changed from: protected */
            public void saveCallResult(List<PoliticalItem> list) {
//                try {
//                    runInTransaction((Runnable) INSTANCE);
//                } catch (Exception ex) {
//                    Util.showErrorLog("Error at ", (Object) ex);
//                }
            }

            public boolean shouldFetch(List<PoliticalItem> list) {
                return Config.IS_CONNECTED;
            }

            /* access modifiers changed from: protected */
            public LiveData<List<PoliticalItem>> loadFromDb() {
//                if (this.resultsDb == null) {
//                    return AbsentLiveData.create();
//                }
                return new LiveData<List<PoliticalItem>>() {
                    /* access modifiers changed from: protected */
                    public void onActive() {
                        super.onActive();
//                        setValue(resultsDb);
                    }
                };
            }

            public LiveData<ApiResponse<List<PoliticalItem>>> createCall() {
                ApiService apiService = ApiClient.getApiService();
                String str = str2;
                RequestBody requestBody = requestBody1;
                RequestBody requestBody2 = finalFullName;
                RequestBody requestBody3 = requestBody2;
                MultipartBody.Part part = finalBody;
                return apiService.addPolitical(str, requestBody, requestBody2, part, requestBody3, part, requestBody2, part, requestBody2, part, requestBody2, part, requestBody2, requestBody3, requestBody4, requestBody5, create);
            }
        }.asLiveData();
    }



    public LiveData<Resource<List<BusinessItem>>> updateBusinessData(String name, String profileImagePath, Uri imageUri,
                                                                     String email, String phone, String website,
                                                                     String address, boolean b, String bussinessId,ContentResolver contentResolver) {
        imgObj.setValue("PS");

        return Transformations.switchMap(imgObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.updateBusiness("sdghhgh416546dd5654wst56w4646w46", profileImagePath, imageUri,
                    bussinessId, name, email, phone, website, address, b, contentResolver);
        });
    }


    public LiveData<Resource<Boolean>> deleteBusiness(String businessId) {
        deleteimgObj.setValue("PS");
        return Transformations.switchMap(deleteimgObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.deleteBusiness("sdghhgh416546dd5654wst56w4646w46",businessId);
        });
    }

    public LiveData<Resource<Boolean>> setDefault(String userId, String businessId) {
        defaultObj.setValue("PS");
        return Transformations.switchMap(defaultObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.setDefault("sdghhgh416546dd5654wst56w4646w46",userId, businessId);
        });
    }

    public int getBusinessCount() {
        return repository.getBusinessCount();
    }

    private class TmpData {
        String businessId = "";
    }

    public BusinessItem getDefaultBusiness() {
        return repository.getDefaultBusiness();
    }

    public void setLoadingState(Boolean state) {
        isLoading = state;
        loadingState.setValue(state);
    }

    public MutableLiveData<Boolean> getLoadingState() {
        return loadingState;
    }
}
