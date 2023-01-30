package com.readymadedata.app.ui.activities;

import static com.readymadedata.app.MyApplication.ShowOpenAds;
import static com.readymadedata.app.utils.Constant.ADMOB;
import static com.readymadedata.app.utils.Constant.ADS_ENABLE;
import static com.readymadedata.app.utils.Constant.AD_NETWORK;
import static com.readymadedata.app.utils.Constant.BANNER_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.BANNER_AD_ID;
import static com.readymadedata.app.utils.Constant.FACEBOOK;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_CLICK;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.INTERSTITIAL_AD_ID;
import static com.readymadedata.app.utils.Constant.NATIVE_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.NATIVE_AD_ID;
import static com.readymadedata.app.utils.Constant.OPEN_AD_ENABLE;
import static com.readymadedata.app.utils.Constant.OPEN_AD_ID;
import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY;
import static com.readymadedata.app.utils.Constant.PRIVACY_POLICY_LINK;
import static com.readymadedata.app.utils.Constant.PUBLISHER_ID;
import static com.readymadedata.app.utils.Constant.UNITY;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.readymadedata.app.Ads.BannerAdManager;
import com.readymadedata.app.Ads.GDPRChecker;
import com.readymadedata.app.Config;
import com.readymadedata.app.R;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.databinding.ActivityMainBinding;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.ui.dialog.LanguageDialog;
import com.readymadedata.app.ui.fragments.HomeFragment;
import com.readymadedata.app.ui.fragments.ProfileFragment;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.PostViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    PrefManager prefManager;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    String type = "business";

    UserViewModel userViewModel;
    UserItem userItem = null;
    PostViewModel postViewModel;
    public boolean isVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager = new PrefManager(this);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (prefManager.getBoolean(Constant.IS_LOGIN)) {
            changeData();
        } else {
            setUserData(null, false);
        }


        setupFragment(new HomeFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);

        if (!prefManager.getBoolean(Constant.NOTIFICATION_FIRST)) {
            prefManager.setBoolean(Constant.NOTIFICATION_ENABLED, true);
            prefManager.setBoolean(Constant.NOTIFICATION_FIRST, true);
        }

//      binding.toolbar.toolbarIvLanguage.setVisibility(View.VISIBLE);
        binding.toolbar.toolbarIvSearch.setVisibility(View.VISIBLE);
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        binding.toolbar.toolbarIvLanguage.setOnClickListener(v -> {
            LanguageDialog dialog = new LanguageDialog(this, data -> {
                postViewModel.setPostByIdObj(getIntent().getStringExtra(Constant.INTENT_FEST_ID), type,
                        prefManager.getString(Constant.USER_LANGUAGE), isVideo);
            });
            dialog.showDialog();
        });

        setupFragment(new HomeFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);


        binding.toolbar.toolbarIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        setUi();
        initData();
        if ("sdghhgh416546dd5654wst56w4646w46".equals(Constant.api_key)) {
            Util.loadFirebase(this);
        }
    }

    private void changeData() {
        userViewModel.getUserDataById().observe(this, listResource -> {
            if (listResource != null) {
                Util.showLog("Got Data "
                        + listResource.message +
                        listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {
                            userItem = listResource.data;
                            setUserData(listResource.data, true);
                        }

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            userItem = listResource.data;
                            setUserData(listResource.data, true);
                        }

                        break;
                    case ERROR:
                        // Error State

                        Util.showLog("Error: " + listResource.message);

                        break;
                    default:
                        // Default
                        break;
                }

            } else {

                // Init Object or Empty Data
                Util.showLog("Empty Data");

            }
        });
        userViewModel.setUserById(prefManager.getString(Constant.USER_ID));
    }

    private void setUserData(UserItem data, boolean bool) {
        if (bool) {
            GlideBinding.bindImage(binding.header.cvProfileImage, data.userImage);
            binding.header.txtHeaderName.setText(data.userName);
            binding.header.txtHeaderEmail.setText(data.email);
            binding.header.liHeader.setOnClickListener(v -> {
                closeDrawer();
                startActivity(new Intent(this, ProfileActivity.class));
            });
            binding.navLogin.setVisibility(View.GONE);
            binding.navLogout.setVisibility(View.VISIBLE);
        } else {
            binding.navLogout.setVisibility(View.GONE);
            binding.navLogin.setVisibility(View.VISIBLE);
            binding.navProfile.setVisibility(View.GONE);
            binding.header.txtHeaderName.setText(getString(R.string.click_here));
            binding.header.txtHeaderEmail.setText(getString(R.string.login_first_login));
            binding.header.liHeader.setOnClickListener(v -> {
                closeDrawer();
                startActivity(new Intent(this, LoginActivity.class));
            });
        }
    }

    private void initData() {
        userViewModel.getAppInfo().observe(this, listResource -> {
            if (listResource != null) {

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB
                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {
                                prefManager.setString(PRIVACY_POLICY, listResource.data.privacyPolicy);
                                prefManager.setString(Constant.TERM_CONDITION, listResource.data.termsCondition);
                                prefManager.setString(Constant.REFUND_POLICY, listResource.data.refundPolicy);

                                prefManager.setString(Constant.RAZORPAY_KEY_ID, listResource.data.razorpayKeyId);

                                prefManager.setString(PRIVACY_POLICY_LINK, listResource.data.privacyPolicy);

                                prefManager.setBoolean(ADS_ENABLE, listResource.data.adsEnabled.equals(Config.ONE) ? true : false);

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

                            } catch (NullPointerException ne) {
                                Util.showErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Util.showErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.setLoadingState(false);

                        }

                        break;
                    case ERROR:
                        // Error State

                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Util.showLog("Empty Data");
                prefManager.setBoolean(Constant.IS_LOGIN, false);

            }

        });
        userViewModel.setAppInfo("good");
    }

    private void initializeAds() {
        switch (prefManager.getString(AD_NETWORK)) {
            case ADMOB:
                new GDPRChecker()
                        .withContext(MainActivity.this)
                        .withPrivacyUrl(prefManager.getString(PRIVACY_POLICY_LINK))
                        .withPublisherIds(prefManager.getString(PUBLISHER_ID))
                        .check();
                ShowOpenAds();
                BannerAdManager.showBannerAds(this, binding.llAdview);
                break;
            case UNITY:
                break;
            case FACEBOOK:
                break;
        }
    }

    private void setUi() {
        checkPer();
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT);
        binding.drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                                   @Override
                                                   public void onDrawerSlide(View drawer, float slideOffset) {

                                                       binding.contentView.setX(binding.navigationView.getWidth() * slideOffset);
                                                       DrawerLayout.LayoutParams lp =
                                                               (DrawerLayout.LayoutParams) binding.contentView.getLayoutParams();
                                                       lp.height = drawer.getHeight() -
                                                               (int) (drawer.getHeight() * slideOffset * 0.3f);
                                                       lp.topMargin = (drawer.getHeight() - lp.height) / 2;
                                                       binding.contentView.setLayoutParams(lp);
                                                   }

                                                   @Override
                                                   public void onDrawerOpened(View drawerView) {
                                                       Util.StatusBarColor(getWindow(), getResources().getColor(R.color.white));
//                                                       binding.toolbar.toolbarIvMenu.setBackground(getDrawable(R.drawable.ic_back));
                                                   }

                                                   @Override
                                                   public void onDrawerClosed(View drawerView) {
                                                       Util.StatusBarColor(getWindow(), getResources().getColor(R.color.primary_color));
//                                                       binding.toolbar.toolbarIvMenu.setBackground(getDrawable(R.drawable.ic_menu_icon));
                                                   }
                                               }
        );

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_buy_plans:
                        startActivity(new Intent(MainActivity.this, SubsPlanActivity.class));

                        return true;
                    case R.id.home_menu:
                        recreate();
                        setupFragment(new HomeFragment());

                        return true;
                    case R.id.home_profile:
                        setupFragment(new ProfileFragment());
                        return true;
                    case R.id.manage_profile:
                        startActivity(new Intent(MainActivity.this, AddBusinessActivity.class));
                        return true;
                    default:
                        return false;

                }
            }
        });

//        binding.toolbar.toolbarIvMenu.setOnClickListener(v -> {
//            if (binding.drawerLayout.isOpen()) {
//                binding.drawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                binding.drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        binding.navLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.navLanguage.setOnClickListener(v -> {
            closeDrawer();
            LanguageDialog dialog = new LanguageDialog(this, languages -> {
                prefManager.setString(Constant.USER_LANGUAGE, languages);
            });
            dialog.showDialog();
        });

        binding.navCategory.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, CategoryActivity.class));
        });
        binding.navHome.setOnClickListener(v -> {
            closeDrawer();
            binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);
        });

//        String secureId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Util.showLog("AndroidId: " + secureId);
        binding.navSubscribe.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, SubsPlanActivity.class));
        });
        binding.navSetting.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, SettingActivity.class));
        });

        binding.navContact.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, ContactUsActivity.class));
        });
        binding.navProfile.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, ProfileActivity.class));
        });
        binding.navAboutUs.setOnClickListener(v -> {
            closeDrawer();
            startActivity(new Intent(this, AboutUsActivity.class));
        });
        binding.navRate.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        });

        binding.navLogout.setOnClickListener(v -> {
            DialogMsg dialogMsg = new DialogMsg(this, false);
            dialogMsg.showConfirmDialog(getString(R.string.menu_logout), getString(R.string.message__want_to_logout),
                    getString(R.string.message__logout),
                    getString(R.string.message__cancel_close));
            dialogMsg.show();

            dialogMsg.okBtn.setOnClickListener(view -> {

                dialogMsg.cancel();

                if (userItem != null) {
                    userViewModel.deleteUserLogin(userItem).observe(this, status -> {
                        if (status != null) {

                            Util.showLog("User is Status : " + status);

                            prefManager.setBoolean(Constant.IS_LOGIN, false);
                            prefManager.remove(Constant.USER_ID);
                            prefManager.remove(Constant.USER_EMAIL);
                            prefManager.remove(Constant.USER_PASSWORD);

                            userItem = null;

                            setUserData(null, false);

                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                            googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                            Util.showToast(MainActivity.this, getString(R.string.success_logout));
                        }
                    });

                    Util.showLog("nav_logout_login");
                }
            });

            dialogMsg.cancelBtn.setOnClickListener(view -> dialogMsg.cancel());
            closeDrawer();
        });

        binding.navPrivacyPolicy.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
            intent.putExtra("type", Constant.PRIVACY_POLICY);
            startActivity(intent);
        });

    }

    private void checkPer() {
        Dexter.withContext(this)
                .withPermissions(
                        PERMISSIONS
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void setupFragment(Fragment fragment) {
        try {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_main, fragment)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            Util.showLog("Error! Can't replace fragment.");
        }
    }

    private void launchIntro() {
        startActivity(new Intent(this, IntroActivity.class));
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main);

        if (fragment != null) {
            if (fragment instanceof HomeFragment) {

                DialogMsg dialogMsg = new DialogMsg(this, false);
                dialogMsg.showConfirmDialog(getString(R.string.menu_exit), getString(R.string.do_you_want_to_exit), getString(R.string.yes), getString(R.string.no));
                dialogMsg.show();
                dialogMsg.okBtn.setOnClickListener(v -> {
                    dialogMsg.cancel();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    super.onBackPressed();
                    finish();
                    System.exit(0);
                });
            } else {
//                binding.toolbar.toolName.setText(getResources().getString(R.string.app_name));
                setupFragment(new HomeFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);
            }
        }

    }
}