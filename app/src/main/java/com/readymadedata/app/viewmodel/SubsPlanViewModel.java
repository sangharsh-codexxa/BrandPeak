package com.readymadedata.app.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.readymadedata.app.api.ApiStatus;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.items.CouponItem;
import com.readymadedata.app.items.SubsPlanItem;
import com.readymadedata.app.repository.SubsPlanRepository;
import com.readymadedata.app.utils.AbsentLiveData;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;

import java.util.List;

public class SubsPlanViewModel extends AndroidViewModel {

    private SubsPlanRepository subsPlanRepository;
    private MutableLiveData<String> postObj = new MutableLiveData<>();
    private MutableLiveData<String> paymentObj = new MutableLiveData<>();

    private MutableLiveData<TmpDataHolder> couponObj = new MutableLiveData<>();
    public LiveData<Resource<CouponItem>>couponData = new MutableLiveData<>();
    PrefManager prefManager;

    public SubsPlanViewModel(@NonNull Application application) {
        super(application);
        subsPlanRepository = new SubsPlanRepository(application);
        prefManager = new PrefManager(application);

        couponData = Transformations.switchMap(couponObj, obj -> {
            if(obj==null){
                return AbsentLiveData.create();
            }
            return subsPlanRepository.checkCoupon("sdghhgh416546dd5654wst56w4646w46", obj.userId, obj.couponCode);
        });
    }

    public LiveData<Resource<List<SubsPlanItem>>> getSubsPlanItems() {
        postObj.setValue("PS");
        return Transformations.switchMap(postObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return subsPlanRepository.getSubsPlanItems("sdghhgh416546dd5654wst56w4646w46");
        });
    }

    public LiveData<Resource<ApiStatus>> loadPayment(String userId, String planId, String paymentId, String planPrice, String couponCode) {
        paymentObj.setValue("PS");
        return Transformations.switchMap(paymentObj, obj -> {
            if(obj==null){
                return AbsentLiveData.create();
            }
            Log.e("PaymentData---->"," "+userId+" "+ planId+" " +paymentId +" " +planPrice+" "+ couponCode);

            return subsPlanRepository.loadPayment("sdghhgh416546dd5654wst56w4646w46", userId, planId, paymentId, planPrice, couponCode);
        });
    }

    public LiveData<Resource<CouponItem>> checkCoupon() {
        return couponData;
    }

    public void setCouponObj(String userId, String couponCode){
        TmpDataHolder tmpDataHolder = new TmpDataHolder();
        tmpDataHolder.userId = userId;
        tmpDataHolder.couponCode = couponCode;
        couponObj.setValue(tmpDataHolder);
    }

    public class TmpDataHolder{
        public String userId = "";
        public String couponCode = "";
    }
}
