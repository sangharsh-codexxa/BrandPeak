package com.readymadedata.app.ui.activities;

import static com.readymadedata.app.utils.Constant.ADS_ENABLE;
import static com.readymadedata.app.utils.Constant.AD_NETWORK;
import static com.readymadedata.app.utils.Constant.BANNER_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.BANNER_AD_ID;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_CLICK;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ID;
import static com.readymadedata.app.utils.Constant.IS_LOGIN;
import static com.readymadedata.app.utils.Constant.NATIVE_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.NATIVE_AD_ID;
import static com.readymadedata.app.utils.Constant.OPEN_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.OPEN_AD_ID;
import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY;
import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY_LINK;
import static com.readymadedata.app.utils.Constant.PUBLISHER_ID;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.readymadedata.app.R;
import com.readymadedata.app.Config;
import com.readymadedata.app.R;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.api.common.common.Status;
import com.readymadedata.app.items.AppInfo;
import com.readymadedata.app.items.AppVersion;
import com.readymadedata.app.ui.activities.DetailActivity;
import com.readymadedata.app.ui.activities.IntroActivity;
import com.readymadedata.app.ui.activities.MainActivity;
import com.readymadedata.app.ui.activities.SendOtpActivity;
import com.readymadedata.app.ui.activities.SubsPlanActivity;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.UserViewModel;

public class SplashyActivity extends AppCompatActivity {
    DialogMsg dialogMsg;
    String id = "";
    String imgUrl = "";
    String name = "";
    PrefManager prefManager;
    String type = "";
    UserViewModel userViewModel;
    boolean video = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_splashy);
        prefManager = new PrefManager(this);
        dialogMsg = new DialogMsg(this, false);
        userViewModel = (UserViewModel) new ViewModelProvider(this).get(UserViewModel.class);
        initData();
        if (prefManager.getString(Constant.CHECKED_ITEM).equals("yes")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        try {
            if (Config.isFromNotifications) {
                id = prefManager.getString(Constant.PRF_ID);
                name = prefManager.getString(Constant.PRF_NAME);
                imgUrl = "";
                type = prefManager.getString(Constant.PRF_TYPE);
                video = prefManager.getBoolean(Constant.INTENT_VIDEO);
                Util.showLog("NOT: " + id + ", " + name + ", " + imgUrl + ", " + type + ", " + video);
            }
        } catch (Exception e) {
            Util.showErrorLog(e.getMessage(), (Object) e);
        }

        Thread timer= new Thread()
        {
            public void run()
            {
                try
                {
                    //Display for 3 seconds
                    sleep(5000);
                }
                catch (InterruptedException e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                finally
                {
                    //Goes to Activity  StartingPoint.java(STARTINGPOINT)
                    if (prefManager.getBoolean(Constant.IS_LOGIN)) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(getApplicationContext(), SendOtpActivity.class));
                        finish();
                    }
                }
            }
        };
        timer.start();


//        getData();
    }

    public void getData() {
        if (Config.IS_CONNECTED) {
            Util.showLog("Internet connected");
            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(60).build());
            mFirebaseRemoteConfig.setDefaultsAsync((int) R.xml.remote_config_defaults);
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                public void onComplete(Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        Util.showLog("Config params updated: " + task.getResult().booleanValue());
                    }
                    Util.showLog("API_KEY: " + mFirebaseRemoteConfig.getString("apiKey"));
                    SplashyActivity.this.prefManager.setString(Constant.api_key, mFirebaseRemoteConfig.getString("apiKey"));
                    Config.API_KEY = "sdghhgh416546dd5654wst56w4646w46";
                    prefManager.setString("FIRST", "TRUE");
                    userViewModel.setAppInfo("new");
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Util.showErrorLog("Firebase", (Object) e);
                    gotoMainActivity();
                }
            });
            startActivity(new Intent(SplashyActivity.this,MainActivity.class));
            return;
        }
        Util.showLog("Internet is not connected");
        gotoMainActivity();
    }


    private void initData() {

        userViewModel.getAppInfo().observe(this, listResource -> {
            if (listResource != null) {

                Util.showLog("Got Data: " + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:

                    break;
                    case SUCCESS:

                        if (listResource.data != null) {
                            try {
                                prefManager.setString(PRIVACY_POLICY, listResource.data.privacyPolicy);

                                prefManager.setString(Constant.TERM_CONDITION, listResource.data.termsCondition);

                                prefManager.setString(Constant.REFUND_POLICY, listResource.data.refundPolicy);

                                prefManager.setString(Constant.RAZORPAY_KEY_ID, listResource.data.razorpayKeyId);

                                prefManager.setString(PRIVACY_POLICY_LINK, listResource.data.privacyPolicy);

                                prefManager.setBoolean(ADS_ENABLE, listResource.data.adsEnabled.equals(Config.ONE));

                                prefManager.setString(AD_NETWORK, listResource.data.ad_network);

                                prefManager.setString(PUBLISHER_ID, listResource.data.publisher_id);

                                prefManager.setString(BANNER_AD_ID, listResource.data.banner_ad_id);

                                prefManager.setBoolean(BANNER_AD_ENABLE, listResource.data.banner_ad.equals(Config.ONE) ? true : false);

                                prefManager.setString(INTERSTITIAL_AD_ID, listResource.data.interstitial_ad_id);

                                prefManager.setBoolean(INTERSTITIAL_AD_ENABLE, listResource.data.interstitial_ad.equals(Config.ONE) ? true : false);

                                prefManager.setInt(INTERSTITIAL_AD_CLICK, Integer.parseInt(listResource.data.interstitial_ad_click));

                                prefManager.setString(NATIVE_AD_ID, listResource.data.native_ad_id);

                                prefManager.setBoolean(NATIVE_AD_ENABLE, listResource.data.native_ad.equals(Config.ONE) ? true : false);

                                prefManager.setString(OPEN_AD_ID, listResource.data.open_ad_id);

                                prefManager.setBoolean(OPEN_AD_ENABLE, listResource.data.open_ad.equals(Config.ONE) ? true : false);

                                if (prefManager.getBoolean(ADS_ENABLE)) {
                                    initializeAds();
                                }


                              checkVersionNo(listResource.data.appVersion);

                            } catch (NullPointerException ne) {
                                Util.showErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Util.showErrorLog("Error in getting notification flag data.", e);
                            }
                            if(prefManager.getBoolean(IS_LOGIN)){
                                Intent intent = new Intent(SplashyActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            userViewModel.setLoadingState(false);

                        }
                        break;


                    case ERROR:
                        // Error State
                        prefManager.setBoolean(IS_LOGIN, false);
                        dialogMsg.showErrorDialog(getString(R.string.click_try_again), getString(R.string.try_again));
                        dialogMsg.show();

                        dialogMsg.okBtn.setOnClickListener(v -> {
                            dialogMsg.cancel();
                            getData();
                        });

                        userViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        userViewModel.setLoadingState(false);

                        break;
                }

            } else {
                Util.showLog("Empty Data");
                prefManager.setBoolean(IS_LOGIN, false);

            }

        });

    }





    private void initializeAds() {

        throw new UnsupportedOperationException("Method not decompiled: com.readymadedata.app.p000ui.activities.SplashyActivity.initializeAds():void");
    }

    private void checkVersionNo(AppVersion appVersion) {
//        if (!appVersion.updatePopupShow.equals("1") || appVersion.newAppVersionCode.equals(Config.APP_VERSION)) {
//            gotoMainActivity();
//            return;
//        }
//        this.dialogMsg.showAppInfoDialog(getString(R.string.force_update__button_update), getString(R.string.app__cancel), getString(R.string.force_update_true), appVersion.versionMessage);
//        this.dialogMsg.show();
//        if (appVersion.cancelOption.equals(Config.ZERO)) {
//            this.dialogMsg.cancelBtn.setVisibility(8);
//        }
//        this.dialogMsg.cancelBtn.setOnClickListener(new SplashyActivity(this));
//        this.dialogMsg.okBtn.setOnClickListener(new SplashyActivity$$ExternalSyntheticLambda2(this, appVersion));
    }

//    public void checkVersionNo(View v) {
//        this.dialogMsg.cancel();
//        gotoMainActivity();
//    }

    /* renamed from: lambda$checkVersionNo$3$com-readymadedata-app-ui-activities-SplashyActivity */
    public void checkVersionNo(AppVersion appVersion, View v) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(appVersion.appLink)));
    }

    /* access modifiers changed from: private */
    public void gotoMainActivity() {
        Intent intent;
        Util.showLog("NOTI_SS: " + Config.isFromNotifications);
//        if (Config.isFromNotifications) {
//            if (this.type.equals(Constant.FESTIVAL) || this.type.equals(Constant.CATEGORY) || this.type.equals("custom")) {
//                intent = new Intent(this, DetailActivity.class);
//                intent.putExtra(Constant.INTENT_TYPE, this.type);
//                intent.putExtra(Constant.INTENT_FEST_ID, this.id);
//                intent.putExtra(Constant.INTENT_FEST_NAME, this.name);
//                intent.putExtra(Constant.INTENT_POST_IMAGE, "");
//                intent.putExtra(Constant.INTENT_VIDEO, this.video);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            } else if (this.type.equals(Constant.EXTERNAL)) {
//                intent = new Intent("android.intent.action.VIEW", Uri.parse(this.prefManager.getString(Constant.PRF_LINK)));
//            } else intent = new Intent(this, SubsPlanActivity.class);
//            Config.isFromNotifications = false;
//        }
//        else if (!this.prefManager.getBoolean(Constant.IS_FIRST_TIME_LAUNCH)) {
//            intent = new Intent(this, IntroActivity.class);
//        }
//        else {

            intent = new Intent(this, MainActivity.class);
//        }
        startActivity(intent);
        finish();
    }
}


//package com.readymadedata.app.ui.activities;
//
//import static com.readymadedata.app.utils.Constant.ADS_ENABLE;
//import static com.readymadedata.app.utils.Constant.AD_NETWORK;
//import static com.readymadedata.app.utils.Constant.BANNER_AD_ENABLE;
//import static com.readymadedata.app.utils.Constant.BANNER_AD_ID;
//import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_CLICK;
//import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ENABLE;
//import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ID;
//import static com.readymadedata.app.utils.Constant.IS_LOGIN;
//import static com.readymadedata.app.utils.Constant.NATIVE_AD_ENABLE;
//import static com.readymadedata.app.utils.Constant.NATIVE_AD_ID;
//import static com.readymadedata.app.utils.Constant.OPEN_AD_ENABLE;
//import static com.readymadedata.app.utils.Constant.OPEN_AD_ID;
//import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY;
//import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY_LINK;
//import static com.readymadedata.app.utils.Constant.PUBLISHER_ID;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//import androidx.lifecycle.ViewModelProvider;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
//import com.readymadedata.app.Config;
//import com.readymadedata.app.R;
//import com.readymadedata.app.api.common.common.Resource;
//import com.readymadedata.app.api.common.common.Status;
//import com.readymadedata.app.items.AppInfo;
//import com.readymadedata.app.items.AppVersion;
//import com.readymadedata.app.ui.activities.DetailActivity;
//import com.readymadedata.app.ui.activities.IntroActivity;
//import com.readymadedata.app.ui.activities.MainActivity;
//import com.readymadedata.app.ui.activities.SendOtpActivity;
//import com.readymadedata.app.ui.activities.SubsPlanActivity;
//import com.readymadedata.app.ui.dialog.DialogMsg;
//import com.readymadedata.app.utils.Constant;
//import com.readymadedata.app.utils.PrefManager;
//import com.readymadedata.app.utils.Util;
//import com.readymadedata.app.viewmodel.UserViewModel;
//
///* renamed from: com.readymadedata.app.ui.activities.SplashyActivity */
//public class SplashyActivity extends AppCompatActivity {
//    DialogMsg dialogMsg;
//
//    /* renamed from: id */
//    String id = "";
//    String imgUrl = "";
//    String name = "";
//    PrefManager prefManager;
//    String type = "";
//    UserViewModel userViewModel;
//    boolean video = false;
//
//    /* access modifiers changed from: protected */
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView((int) R.layout.activity_splashy);
//        this.prefManager = new PrefManager(this);
//        this.dialogMsg = new DialogMsg(this, false);
//        this.userViewModel = (UserViewModel) new ViewModelProvider(this).get(UserViewModel.class);
//        initData();
//        if (this.prefManager.getString(Constant.CHECKED_ITEM).equals("yes")) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//        try {
//            if (Config.isFromNotifications) {
//                this.id = this.prefManager.getString(Constant.PRF_ID);
//                this.name = this.prefManager.getString(Constant.PRF_NAME);
//                this.imgUrl = "";
//                this.type = this.prefManager.getString(Constant.PRF_TYPE);
//                this.video = this.prefManager.getBoolean(Constant.INTENT_VIDEO);
//                Util.showLog("NOT: " + this.id + ", " + this.name + ", " + this.imgUrl + ", " + this.type + ", " + this.video);
//            }
//        } catch (Exception e) {
//            Util.showErrorLog(e.getMessage(), (Object) e);
//        }
//        getData();
//    }
//
//    public void getData() {
//        if (Config.IS_CONNECTED) {
//            Util.showLog("Internet connected");
//            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//            mFirebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(60).build());
//            mFirebaseRemoteConfig.setDefaultsAsync((int)R.xml.remote_config_defaults);
//            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
//                public void onComplete(Task<Boolean> task) {
//                    if (task.isSuccessful()) {
//                        Util.showLog("Config params updated: " + task.getResult().booleanValue());
//                    }
//                    Util.showLog("API_KEY: " + mFirebaseRemoteConfig.getString("apiKey"));
//                    SplashyActivity.this.prefManager.setString(Constant.api_key, mFirebaseRemoteConfig.getString("apiKey"));
//                    Config.API_KEY = SplashyActivity."sdghhgh416546dd5654wst56w4646w46";
//                    SplashyActivity.this.prefManager.setString("FIRST", "TRUE");
//                    SplashyActivity.this.userViewModel.setAppInfo("new");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                public void onFailure(Exception e) {
//                    Util.showErrorLog("Firebase", (Object) e);
//                    SplashyActivity.this.gotoMainActivity();
//                }
//            });
//            return;
//        }
//        Util.showLog("Internet is not connected");
//        gotoMainActivity();
//    }
//
//    private void initData() {
//
//        userViewModel.getAppInfo().observe(this, listResource -> {
//
//            if (listResource != null) {
//
//                Util.showLog("Got Data: " + listResource.message + listResource.data.toString());
//
//                switch (listResource.status) {
//                    case LOADING:
//                        // Loading State
//                        // Data are from Local DB
//                        break;
//                    case SUCCESS:
//                        // Success State
//                        // Data are from Server
//
//                        if (listResource.data != null) {
//                            try {
//                                prefManager.setString(PRIVACY_POLICY, listResource.data.privacyPolicy);
//                                prefManager.setString(Constant.TERM_CONDITION, listResource.data.termsCondition);
//                                prefManager.setString(Constant.REFUND_POLICY, listResource.data.refundPolicy);
//
//                                prefManager.setString(Constant.RAZORPAY_KEY_ID, listResource.data.razorpayKeyId);
//
//                                prefManager.setString(PRIVACY_POLICY_LINK, listResource.data.privacyPolicy);
//
//
//                                prefManager.setBoolean(ADS_ENABLE, listResource.data.adsEnabled.equals(Config.ONE));
//
//                                prefManager.setString(AD_NETWORK, listResource.data.ad_network);
//
//                                prefManager.setString(PUBLISHER_ID, listResource.data.publisher_id);
//
//                                prefManager.setString(BANNER_AD_ID, listResource.data.banner_ad_id);
//                                prefManager.setBoolean(BANNER_AD_ENABLE, listResource.data.banner_ad.equals(Config.ONE) ? true : false);
//
//                                prefManager.setString(INTERSTITIAL_AD_ID, listResource.data.interstitial_ad_id);
//                                prefManager.setBoolean(INTERSTITIAL_AD_ENABLE, listResource.data.interstitial_ad.equals(Config.ONE) ? true : false);
//                                prefManager.setInt(INTERSTITIAL_AD_CLICK, Integer.parseInt(listResource.data.interstitial_ad_click));
//
//                                prefManager.setString(NATIVE_AD_ID, listResource.data.native_ad_id);
//                                prefManager.setBoolean(NATIVE_AD_ENABLE, listResource.data.native_ad.equals(Config.ONE) ? true : false);
//
//                                prefManager.setString(OPEN_AD_ID, listResource.data.open_ad_id);
//                                prefManager.setBoolean(OPEN_AD_ENABLE, listResource.data.open_ad.equals(Config.ONE) ? true : false);
//
//                                if (prefManager.getBoolean(ADS_ENABLE)) {
////                                    initializeAds();
//                                }
//
//
////                              checkVersionNo(listResource.data.appVersion);
//
//                            } catch (NullPointerException ne) {
//                                Util.showErrorLog("Null Pointer Exception.", ne);
//                            } catch (Exception e) {
//                                Util.showErrorLog("Error in getting notification flag data.", e);
//                            }
//                            if(prefManager.getBoolean(IS_LOGIN)){
//                                Intent intent = new Intent(SplashyActivity.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//
//                            userViewModel.setLoadingState(false);
//
//                        }
//
//                        break;
//                    case ERROR:
//                        // Error State
//                        prefManager.setBoolean(IS_LOGIN, false);
//                        dialogMsg.showErrorDialog(getString(R.string.click_try_again), getString(R.string.try_again));
//                        dialogMsg.show();
//
//                        dialogMsg.okBtn.setOnClickListener(v -> {
//                            dialogMsg.cancel();
//                            getData();
//                        });
//
//                        userViewModel.setLoadingState(false);
//
//                        break;
//                    default:
//                        // Default
//
//                        userViewModel.setLoadingState(false);
//
//                        break;
//                }
//
//            } else {
//                Util.showLog("Empty Data");
//                prefManager.setBoolean(IS_LOGIN, false);
//
//            }
//
//        });
//
//    }
//
//    /* renamed from: lambda$initData$1$com-readymadedata-app-ui-activities-SplashyActivity */
////    public /* synthetic */ void mo854xce9c3717(Resource listResource) {
////        boolean z = false;
////        if (listResource != null) {
////            Util.showLog("Got Data: " + listResource.message + listResource.toString());
////            switch (C01333.$SwitchMap$com$readymadedata$app$api$common$common$Status[listResource.status.ordinal()]) {
////                case 1:
////                    return;
////                case 2:
////                    if (listResource.data != null) {
////                        try {
////                            this.prefManager.setString(Constant.PRIVACY_POLICY, ((AppInfo) listResource.data).privacyPolicy);
////                            this.prefManager.setString(Constant.TERM_CONDITION, ((AppInfo) listResource.data).termsCondition);
////                            this.prefManager.setString(Constant.REFUND_POLICY, ((AppInfo) listResource.data).refundPolicy);
////                            this.prefManager.setString(Constant.RAZORPAY_KEY_ID, ((AppInfo) listResource.data).razorpayKeyId);
////                            this.prefManager.setString(Constant.PRIVACY_POLICY_LINK, ((AppInfo) listResource.data).privacyPolicy);
////                            this.prefManager.setBoolean(Constant.ADS_ENABLE, Boolean.valueOf(((AppInfo) listResource.data).adsEnabled.equals("1")));
////                            this.prefManager.setString(Constant.AD_NETWORK, ((AppInfo) listResource.data).ad_network);
////                            this.prefManager.setString(Constant.PUBLISHER_ID, ((AppInfo) listResource.data).publisher_id);
////                            this.prefManager.setString(Constant.BANNER_AD_ID, ((AppInfo) listResource.data).banner_ad_id);
////                            this.prefManager.setBoolean(Constant.BANNER_AD_ENABLE, Boolean.valueOf(((AppInfo) listResource.data).banner_ad.equals("1")));
////                            this.prefManager.setString(Constant.INTERSTITIAL_AD_ID, ((AppInfo) listResource.data).interstitial_ad_id);
////                            this.prefManager.setBoolean(Constant.INTERSTITIAL_AD_ENABLE, Boolean.valueOf(((AppInfo) listResource.data).interstitial_ad.equals("1")));
////                            this.prefManager.setInt(Constant.INTERSTITIAL_AD_CLICK, Integer.parseInt(((AppInfo) listResource.data).interstitial_ad_click));
////                            this.prefManager.setString(Constant.NATIVE_AD_ID, ((AppInfo) listResource.data).native_ad_id);
////                            this.prefManager.setBoolean(Constant.NATIVE_AD_ENABLE, Boolean.valueOf(((AppInfo) listResource.data).native_ad.equals("1")));
////                            this.prefManager.setString(Constant.OPEN_AD_ID, ((AppInfo) listResource.data).open_ad_id);
////                            PrefManager prefManager2 = this.prefManager;
////                            if (((AppInfo) listResource.data).open_ad.equals("1")) {
////                                z = true;
////                            }
////                            prefManager2.setBoolean(Constant.OPEN_AD_ENABLE, Boolean.valueOf(z));
////                            if (this.prefManager.getBoolean(Constant.ADS_ENABLE)) {
////                                initializeAds();
////                            }
////                        } catch (NullPointerException ne) {
////                            Util.showErrorLog("Null Pointer Exception.", (Object) ne);
////                        } catch (Exception e) {
////                            Util.showErrorLog("Error in getting notification flag data.", (Object) e);
////                        }
////                        if (this.prefManager.getBoolean(Constant.IS_LOGIN)) {
////                            startActivity(new Intent(this, MainActivity.class));
////                            finish();
////                        } else {
////                            startActivity(new Intent(this, SendOtpActivity.class));
////                            finish();
////                        }
////                        this.userViewModel.setLoadingState(false);
////                        return;
////                    }
////                    return;
////                case 3:
////                    this.prefManager.setBoolean(Constant.IS_LOGIN, false);
////                    this.dialogMsg.showErrorDialog(getString(R.string.click_try_again), getString(R.string.try_again));
////                    this.dialogMsg.show();
////                    this.dialogMsg.okBtn.setOnClickListener(new SplashyActivity.this);
////                    this.userViewModel.setLoadingState(false);
////                    return;
////                default:
////                    this.userViewModel.setLoadingState(false);
////                    return;
////            }
////        } else {
////            Util.showLog("Empty Data");
////            this.prefManager.setBoolean(Constant.IS_LOGIN, false);
////        }
////    }
////    private void checkVersionNo(AppVersion appVersion) {
////        if (!appVersion.updatePopupShow.equals("1") || appVersion.newAppVersionCode.equals(Config.APP_VERSION)) {
////            gotoMainActivity();
////            return;
////        }
////        this.dialogMsg.showAppInfoDialog(getString(R.string.force_update__button_update), getString(R.string.app__cancel), getString(R.string.force_update_true), appVersion.versionMessage);
////        this.dialogMsg.show();
////        if (appVersion.cancelOption.equals(Config.ZERO)) {
////            this.dialogMsg.cancelBtn.setVisibility(View.GONE);
////        }
////        this.dialogMsg.cancelBtn.setOnClickListener(this);
////        this.dialogMsg.okBtn.setOnClickListener(this, appVersion);
////    }
////
////    /* renamed from: lambda$checkVersionNo$2$com-readymadedata-app-ui-activities-SplashyActivity */
////    public /* synthetic */ void mo851x60bc7b4f(View v) {
////        this.dialogMsg.cancel();
////        gotoMainActivity();
////    }
//
//    /* renamed from: lambda$checkVersionNo$3$com-readymadedata-app-ui-activities-SplashyActivity */
//    public /* synthetic */ void mo852xedf72cd0(AppVersion appVersion, View v) {
//        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(appVersion.appLink)));
//    }
//
//    /* access modifiers changed from: private */
//    public void gotoMainActivity() {
//        Intent intent;
//        Util.showLog("NOTI_SS: " + Config.isFromNotifications);
//        if (Config.isFromNotifications) {
//            if (this.type.equals(Constant.FESTIVAL) || this.type.equals(Constant.CATEGORY) || this.type.equals("custom")) {
//                intent = new Intent(this, DetailActivity.class);
//                intent.putExtra(Constant.INTENT_TYPE, this.type);
//                intent.putExtra(Constant.INTENT_FEST_ID, this.id);
//                intent.putExtra(Constant.INTENT_FEST_NAME, this.name);
//                intent.putExtra(Constant.INTENT_POST_IMAGE, "");
//                intent.putExtra(Constant.INTENT_VIDEO, this.video);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            } else if (this.type.equals(Constant.EXTERNAL)) {
//                intent = new Intent("android.intent.action.VIEW", Uri.parse(this.prefManager.getString(Constant.PRF_LINK)));
//            } else {
//                intent = new Intent(this, SubsPlanActivity.class);
//            }
//            Config.isFromNotifications = false;
//        } else if (!this.prefManager.getBoolean(Constant.IS_FIRST_TIME_LAUNCH)) {
//            intent = new Intent(SplashyActivity.this, IntroActivity.class);
//        } else {
//            intent = new Intent(this, MainActivity.class);
//        }
//        startActivity(intent);
//        finish();
//    }
//}
