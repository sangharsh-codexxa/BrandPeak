package com.readymadedata.app.ui.activities;

import static com.readymadedata.app.utils.Constant.IS_EDIT_MODE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.trusted.sharing.ShareTarget;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.readymadedata.app.Config;
import com.readymadedata.app.R;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.ApiResponse;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.databinding.ActivityAddBusinessBinding;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.PersonalItem;
import com.readymadedata.app.items.PoliticalItem;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.ui.fragments.BusinessFragment;
import com.readymadedata.app.utils.Connectivity;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.BusinessViewModel;
import com.readymadedata.app.viewmodel.PersonalViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusinessActivity extends AppCompatActivity {

    public String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    ActivityAddBusinessBinding binding;
    BusinessItem businessItem;
    List<BusinessItem> businessItemList;
    List<PersonalItem> personalItemList;
    List<PoliticalItem> politicalItemList;
    BusinessViewModel businessViewModel;
    Connectivity connectivity;
    DialogMsg dialogMsg;
    Uri imageUri, poliprofileImageUri, polilogoUri, photoAUri, photoBUri, photoCUri;
    boolean isBusiness = true;
    boolean isPersonal = false;
    boolean isPolitical = false;
    String logoImagePath;
    PersonalViewModel personalViewModel;
    String photoApath;
    String photoBPath;
    String photoCPath;
    String profileImagePath, polilogoPath;
    PrefManager prefManager;
    ProgressDialog prgDialog;


    UserItem userItem;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityAddBusinessBinding inflate = ActivityAddBusinessBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        businessViewModel = (BusinessViewModel) new ViewModelProvider(this).get(BusinessViewModel.class);
        personalViewModel = (PersonalViewModel) new ViewModelProvider(this).get(PersonalViewModel.class);
        userViewModel = (UserViewModel) new ViewModelProvider(this).get(UserViewModel.class);
        this.connectivity = new Connectivity(this);
        this.prefManager = new PrefManager(this);
        ProgressDialog progressDialog = new ProgressDialog(this);
        prgDialog = progressDialog;
        progressDialog.setMessage(getResources().getString(R.string.login_loading));
        prgDialog.setCancelable(false);
        dialogMsg = new DialogMsg(this, false);
//       BannerAdManager.showBannerAds(this, this.binding.llAdview);
        setUpUi();
        setUpViewModel();
        businessViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            public void onChanged(Boolean loadingState) {
                if (loadingState != null) {
                    loadingState.booleanValue();
                }
            }
        });
        businessItemList = new ArrayList();

        personalViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {
                if (loadingState != null) {
                    loadingState.booleanValue();
                }
            }
        });

        personalItemList = new ArrayList<>();

        politicalItemList = new ArrayList<>();


    }

    private void setUpUi() {
//        if (!Constant.IS_EDIT_MODE) {
//            this.binding.toolbar.toolName.setText(getString(R.string.add_business_titles));
//        } else {
//            this.binding.toolbar.toolName.setText(getString(R.string.edit_business_titles));
//            this.businessItem = (BusinessItem) getIntent().getSerializableExtra(Constant.INTENT_BUSINESS);
//        }
//        this.binding.toolbar.toolbarIvMenu.setBackground(getResources().getDrawable(R.C0603drawable.ic_back));
//        this.binding.toolbar.toolbarIvMenu.setOnClickListener(new AddBusinessActivity$$ExternalSyntheticLambda0(this));
        this.binding.lvlBusinessType.setVisibility(View.VISIBLE);
        if (this.prefManager.getString(Constant.PRIMARY_CATEGORY).equals(Config.ZERO)) {
            this.isBusiness = true;
            this.isPersonal = false;
            this.isPolitical = false;
            loadBusiness();
            this.binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            this.binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.lvlBusinessType.setVisibility(View.VISIBLE);
            this.binding.lvlPoliticalType.setVisibility(View.GONE);
            this.binding.lvlPersonalType.setVisibility(View.GONE);
        }
        if (this.prefManager.getString(Constant.PRIMARY_CATEGORY).equals("1")) {
            this.isBusiness = false;
            this.isPersonal = true;
            this.isPolitical = false;
            loadPersonal();
            this.binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            this.binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.lvlPersonalType.setVisibility(View.VISIBLE);
            this.binding.lvlPoliticalType.setVisibility(View.GONE);
            this.binding.lvlBusinessType.setVisibility(View.GONE);
        }
        if (this.prefManager.getString(Constant.PRIMARY_CATEGORY).equals("2")) {
            this.isBusiness = false;
            this.isPersonal = false;
            this.isPolitical = true;
            loadPolitical();
            this.binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.gray));
            this.binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
            this.binding.lvlPoliticalType.setVisibility(View.VISIBLE);
            this.binding.lvlPersonalType.setVisibility(View.GONE);
            this.binding.lvlBusinessType.setVisibility(View.GONE);
        }
        this.binding.btnTypeBusiness.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefManager.setString(Constant.PRIMARY_CATEGORY, Config.ZERO);
                isBusiness = true;
                isPersonal = false;
                isPolitical = false;
                binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.green));
                binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.lvlBusinessType.setVisibility(View.VISIBLE);
                binding.lvlPoliticalType.setVisibility(View.GONE);
                binding.lvlPersonalType.setVisibility(View.GONE);
                startActivity(new Intent(AddBusinessActivity.this, AddBusinessActivity.class));
                finish();
            }
        });
        binding.btnTypePersonal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefManager.setString(Constant.PRIMARY_CATEGORY, "1");
                isBusiness = false;
                isPersonal = true;
                isPolitical = false;
                binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.green));
                binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.lvlPersonalType.setVisibility(View.VISIBLE);
                binding.lvlPoliticalType.setVisibility(View.GONE);
                binding.lvlBusinessType.setVisibility(View.GONE);
                startActivity(new Intent(AddBusinessActivity.this, AddBusinessActivity.class));
                finish();
            }
        });
        this.binding.btnTypePolitilcle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefManager.setString(Constant.PRIMARY_CATEGORY, "2");
                isBusiness = true;
                isPersonal = false;
                isPolitical = true;
                binding.btnTypeBusiness.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.btnTypePersonal.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.gray));
                binding.btnTypePolitilcle.setBackgroundTintList(ContextCompat.getColorStateList(AddBusinessActivity.this, R.color.green));
                binding.lvlPoliticalType.setVisibility(View.VISIBLE);
                binding.lvlPersonalType.setVisibility(View.GONE);
                binding.lvlBusinessType.setVisibility(View.GONE);
                startActivity(new Intent(AddBusinessActivity.this, AddBusinessActivity.class));
                finish();
            }
        });
        binding.btnSave.setOnClickListener(v -> {

            String name = binding.etBusinessName.getText().toString().trim();
            String email = binding.etBusinessEmail.getText().toString().trim();
            String phone = binding.etBusinessNumber.getText().toString().trim();
            String website = binding.etBusinessWebsite.getText().toString().trim();
            String address = binding.etBusinessAddress.getText().toString().trim();

            if (!connectivity.isConnected()) {
                Util.showToast(this, getString(R.string.error_message__no_internet));
                return;
            }
            
            
          

            if (IS_EDIT_MODE) {
                prgDialog.show();
                businessViewModel.updateBusinessData(name, profileImagePath, imageUri, email, phone, website, address,
                        false, prefManager.getString(Constant.USER_ID), getContentResolver()).observe(this,
                        listResource -> {
                            if (listResource != null) {

                                Util.showLog("Got Data" + listResource.message + listResource.toString());

                                switch (listResource.status) {
                                    case LOADING:
                                        // Loading State
                                        // Data are from Local DB

                                        break;
                                    case SUCCESS:
                                        // Success State
                                        // Data are from Server
                                        businessViewModel.setLoadingState(false);
                                        prgDialog.cancel();

                                        dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                                        dialogMsg.show();
                                            dialogMsg.okBtn.setOnClickListener(v1 -> {
                                                dialogMsg.cancel();
                                                startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                                                finish();
//                                              onBackPressed();
                                            });
//                                            Config.BUSINESS_SIZE = businessViewModel.getBusinessCount();
//                                            try {
//                                                BusinessFragment businessFragment = new BusinessFragment();
//                                                businessFragment.setUpViewModel();
//                                            } catch (Exception e) {
//
//                                            }


//                                        });

                                        break;
                                    case ERROR:
                                        // Error State

                                        prgDialog.cancel();
                                        dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                                        dialogMsg.show();
                                        businessViewModel.setLoadingState(false);
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
            } else {
                if (imageUri == null) {
                    Util.showToast(AddBusinessActivity.this, getString(R.string.err_add_image));
                    return;
                }
                prgDialog.show();
                if (binding.btnPoliSave.getText().equals("Save")) {
                    businessViewModel.addBusiness(name, profileImagePath, imageUri, email, phone, website, address,
                            false, prefManager.getString(Constant.USER_ID), getContentResolver()).observe(this,
                            listResource -> {
                                if (listResource != null) {

                                    Util.showLog("Got Data" + listResource.message + listResource.toString());

                                    switch (listResource.status) {
                                        case LOADING:
                                            // Loading State
                                            // Data are from Local DB

                                            break;
                                        case SUCCESS:
                                            // Success State
                                            // Data are from Server
                                            businessViewModel.setLoadingState(false);
                                            prgDialog.cancel();

                                            dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                                            dialogMsg.show();
                                            dialogMsg.okBtn.setOnClickListener(v1 -> {
                                                dialogMsg.cancel();
                                                Config.BUSINESS_SIZE = businessViewModel.getBusinessCount();
                                                startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                                                finish();
//                                              onBackPressed();
                                            });

                                            break;
                                        case ERROR:
                                            // Error State

                                            prgDialog.cancel();
                                            dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                                            dialogMsg.show();
                                            businessViewModel.setLoadingState(false);
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

                } else {

                }
//                    else{
//                        Toast.makeText(AddBusinessActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
//                        prgDialog.show();
//                        businessViewModel.updateBusinessData(name, profileImagePath, imageUri, email, phone, website, address,
//                                false, prefManager.getString(Constant.USER_ID), getContentResolver()).observe(this,
//                                listResource -> {
//                                    if (listResource != null) {
//
//                                        Util.showLog("Got Data" + listResource.message + listResource.toString());
//
//                                        switch (listResource.status) {
//                                            case LOADING:
//                                                // Loading State
//                                                // Data are from Local DB
//
//                                                break;
//                                            case SUCCESS:
//                                                // Success State
//                                                // Data are from Server
//                                                businessViewModel.setLoadingState(false);
//                                                prgDialog.cancel();
//
//                                                dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
//                                                dialogMsg.show();
//                                                dialogMsg.okBtn.setOnClickListener(v1 -> {
//                                                    Config.BUSINESS_SIZE = businessViewModel.getBusinessCount();
//                                                    try {
//                                                        BusinessFragment businessFragment = new BusinessFragment();
//                                                        businessFragment.setUpViewModel();
//                                                    } catch (Exception e) {
//
//                                                    }
//                                                    dialogMsg.cancel();
//                                                    onBackPressed();
//                                                });
//
//                                                break;
//                                            case ERROR:
//                                                // Error State
//
//                                                prgDialog.cancel();
//                                                dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
//                                                dialogMsg.show();
//                                                businessViewModel.setLoadingState(false);
//                                                break;
//                                            default:
//                                                // Default
//
//                                                break;
//                                        }
//
//                                    } else {
//
//                                        // Init Object or Empty Data
//                                        Util.showLog("Empty Data");
//
//                                    }
//                                });
//                    }
//                }
//                onBackPressed();

            }
        });
        binding.btnAddImage.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(i);
        });


        binding.btnSavePersonal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (binding.btnSavePersonal.getText().toString().equals("Save")) {
                    String name = binding.etYourName.getText().toString().trim();
                    String phone = binding.etYourNumber.getText().toString().trim();
                    String address = binding.etYourAddress.getText().toString().trim();
                    String facebookUsername = binding.etFacebookUsername.getText().toString().trim();
                    String instagramUsername = binding.etInstagramUsername.getText().toString().trim();
                    RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);


                    personalViewModel.addPersonal("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_ID), profileImagePath, name, "", phone, address, facebookUsername, instagramUsername).observe(AddBusinessActivity.this,
                            listResource -> {
                                if (listResource != null) {

                                    Util.showLog("Got Data" + listResource.message + listResource.toString());

                                    switch (listResource.status) {
                                        case LOADING:
                                            // Loading State
                                            // Data are from Local DB

                                            break;
                                        case SUCCESS:
                                            // Success State
                                            // Data are from Server
                                            businessViewModel.setLoadingState(false);
                                            prgDialog.cancel();

                                            dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                                            dialogMsg.show();
                                            dialogMsg.okBtn.setOnClickListener(v1 -> {
                                                dialogMsg.cancel();
                                                Config.BUSINESS_SIZE = businessViewModel.getBusinessCount();
                                                startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                                                finish();
//                                                onBackPressed();
                                            });

                                            break;
                                        case ERROR:
                                            // Error State

                                            prgDialog.cancel();
                                            dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                                            dialogMsg.show();
                                            businessViewModel.setLoadingState(false);
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
                } else {
                    String str = profileImagePath;
                    MultipartBody.Part body = null;
                    RequestBody fullName = null;

                    if (str != null && !str.equals("")) {
                        File file = new File(str);
                        body = MultipartBody.Part.createFormData("logo", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        fullName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    String name = binding.etYourName.getText().toString().trim();
                    String phone = binding.etYourNumber.getText().toString().trim();
                    String address = binding.etYourAddress.getText().toString().trim();
                    String facebookUsername = binding.etFacebookUsername.getText().toString().trim();
                    String instagramUsername = binding.etInstagramUsername.getText().toString().trim();

                    MultipartBody.Part finalBody = body;

                    RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);
                    RequestBody userIdRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), prefManager.getString(Constant.USER_ID));
                    RequestBody phoneRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), phone);
                    RequestBody addressRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), address);
                    RequestBody facebookUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), facebookUsername);
                    RequestBody instaUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), instagramUsername);

                    ApiClient.getApiService().updatePersonal("sdghhgh416546dd5654wst56w4646w46", userIdRB, nameRB, finalBody, phoneRB, addressRB, facebookUsernameRB, instaUsernameRB).enqueue(new Callback<ApiResponse<List<BusinessItem>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<BusinessItem>>> call, Response<ApiResponse<List<BusinessItem>>> response) {

                            Log.e("ResponseOnUpdatePersonal--->", response.message().toString());


                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<BusinessItem>>> call, Throwable t) {
                            Log.e("ErrorOnUpdatePersonal--->", t.getMessage());

                        }
                    });

                    dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                    dialogMsg.okBtn.setOnClickListener(v1 -> {
                        dialogMsg.cancel();
                        startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                        finish();

                    });
                    dialogMsg.show();
                }


            }


        });

        binding.btnPoliSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (binding.btnPoliSave.getText().toString().equals("Save")) {
                    String name = binding.etPoliName.getText().toString().trim();
                    String designation = binding.etPoliDesignation.getText().toString().trim();
                    String mobile_num = binding.etPoliMoNumber.getText().toString().trim();
                    String facebook_username = binding.etPoliFacebookUsername.getText().toString().trim();
                    String instagram_username = binding.etPoliInstagramUsername.getText().toString().trim();


                    String polilogoPath1 = polilogoPath;
                    MultipartBody.Part poliLogoBody = null;
                    RequestBody poliLogo = null;
                    Util.showLog("File: " + polilogoPath1);
                    if (!polilogoPath1.equals("")) {
                        File file = new File(polilogoPath1);
                        poliLogoBody = MultipartBody.Part.createFormData("logo", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        poliLogo = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalBody = poliLogoBody;
                    RequestBody finalFullName = poliLogo;

                    String poliProfilePath1 = profileImagePath;
                    MultipartBody.Part poliProfileBody = null;
                    RequestBody poliProfileLogo = null;
                    Util.showLog("File: " + poliProfilePath1);
                    if (!poliProfilePath1.equals("")) {
                        File file = new File(poliProfilePath1);
                        poliProfileBody = MultipartBody.Part.createFormData("profile", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        poliProfileLogo = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalpoliProfileBody = poliProfileBody;
                    RequestBody finalpoliProfileLogoName = poliProfileLogo;


                    MultipartBody.Part photoApathBody = null;
                    RequestBody photoApathName = null;
                    Util.showLog("File: " + photoApath);
                    if (!photoApath.equals("")) {
                        File file = new File(photoApath);
                        photoApathBody = MultipartBody.Part.createFormData("photo_a", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoApathName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalphotoApathBody = photoApathBody;
                    RequestBody finalphotoApathName = photoApathName;

                    String photoBpath = photoBPath;
                    MultipartBody.Part photoBpathBody = null;
                    RequestBody photoBpathLogoName = null;
                    Util.showLog("File: " + photoBpath);
                    if (!photoBpath.equals("")) {
                        File file = new File(photoBpath);
                        photoBpathBody = MultipartBody.Part.createFormData("photo_b", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoBpathLogoName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }
                    MultipartBody.Part finalphotoBpath1Body = photoBpathBody;
                    RequestBody finalphotoBpath2LogoName = photoBpathLogoName;

                    String photoCpath = photoCPath;
                    MultipartBody.Part photoCpathBody = null;
                    RequestBody photoCpathLogoName = null;
                    Util.showLog("File: " + photoCpath);
                    if (!photoCpath.equals("")) {
                        File file = new File(photoCpath);
                        photoCpathBody = MultipartBody.Part.createFormData("photo_c", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoCpathLogoName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalphotoCpath1Body = photoCpathBody;
                    RequestBody finalphotoCpath2LogoName = photoCpathLogoName;


                    RequestBody useIdRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), prefManager.getString(Constant.USER_ID));
                    RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);
                    RequestBody designationRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), designation);
                    RequestBody phoneRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), mobile_num);
                    RequestBody facebookUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), facebook_username);
                    RequestBody instaUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), instagram_username);


                    String apiKey = "sdghhgh416546dd5654wst56w4646w46";

                    prgDialog.show();
                    Call<ApiResponse<List<PoliticalItem>>> call = ApiClient.getApiService().addPoliticals(apiKey, useIdRB,
                            finalFullName, finalBody, finalpoliProfileLogoName, finalpoliProfileBody, finalphotoApathName, finalphotoApathBody, finalphotoBpath2LogoName, finalphotoBpath1Body, finalphotoCpath2LogoName, finalphotoCpath1Body, nameRB, designationRB, phoneRB, facebookUsernameRB, instaUsernameRB
                    );
                    call.enqueue(new Callback<ApiResponse<List<PoliticalItem>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<PoliticalItem>>> call, Response<ApiResponse<List<PoliticalItem>>> response) {
                            if (response.isSuccessful()) {
                                prgDialog.cancel();
//
                                dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                                dialogMsg.show();
                                dialogMsg.okBtn.setOnClickListener(v1 -> {
                                    dialogMsg.cancel();
                                    startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                                    finish();
//                                    onBackPressed();
                                });

                            } else {
                                dialogMsg.showErrorDialog(response.message(), getString(R.string.ok));
                                dialogMsg.show();
                                prgDialog.cancel();
                            }
                            Log.e("ResponseSuccess==>", response.message());
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<PoliticalItem>>> call, Throwable t) {
                            Log.e("ResponseError==>", t.getMessage());

                            dialogMsg.showErrorDialog(t.getMessage(), getString(R.string.ok));
                            dialogMsg.show();
                            prgDialog.cancel();
                        }
                    });
                } else {
                    String name = binding.etPoliName.getText().toString().trim();
                    String designation = binding.etPoliDesignation.getText().toString().trim();
                    String mobile_num = binding.etPoliMoNumber.getText().toString().trim();
                    String facebook_username = binding.etPoliFacebookUsername.getText().toString().trim();
                    String instagram_username = binding.etPoliInstagramUsername.getText().toString().trim();


                    String polilogoPath1 = polilogoPath;
                    MultipartBody.Part poliLogoBody = null;
                    RequestBody poliLogo = null;
                    Util.showLog("File: " + polilogoPath1);
                    if ( polilogoPath1 !=null && !polilogoPath1.equals("") ) {
                        File file = new File(polilogoPath1);
                        poliLogoBody = MultipartBody.Part.createFormData("logo", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        poliLogo = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalBody = poliLogoBody;
                    RequestBody finalFullName = poliLogo;

                    String poliProfilePath1 = profileImagePath;
                    MultipartBody.Part poliProfileBody = null;
                    RequestBody poliProfileLogo = null;
                    Util.showLog("File: " + poliProfilePath1);
                    if ( poliProfilePath1 != null && !poliProfilePath1.equals("")) {
                        File file = new File(poliProfilePath1);
                        poliProfileBody = MultipartBody.Part.createFormData("profile", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        poliProfileLogo = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalpoliProfileBody = poliProfileBody;
                    RequestBody finalpoliProfileLogoName = poliProfileLogo;


                    MultipartBody.Part photoApathBody = null;
                    RequestBody photoApathName = null;
                    Util.showLog("File: " + photoApath);
                    if (photoApath != null && !photoApath.equals("")) {
                        File file = new File(photoApath);
                        photoApathBody = MultipartBody.Part.createFormData("photo_a", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoApathName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalphotoApathBody = photoApathBody;
                    RequestBody finalphotoApathName = photoApathName;

                    String photoBpath = photoBPath;
                    MultipartBody.Part photoBpathBody = null;
                    RequestBody photoBpathLogoName = null;
                    Util.showLog("File: " + photoBpath);
                    if (photoBpath !=null && !photoBpath.equals("")  ) {
                        File file = new File(photoBpath);
                        photoBpathBody = MultipartBody.Part.createFormData("photo_b", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoBpathLogoName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }
                    MultipartBody.Part finalphotoBpath1Body = photoBpathBody;
                    RequestBody finalphotoBpath2LogoName = photoBpathLogoName;

                    String photoCpath = photoCPath;
                    MultipartBody.Part photoCpathBody = null;
                    RequestBody photoCpathLogoName = null;
                    Util.showLog("File: " + photoCpath);
                    if (photoCpath != null && !photoCpath.equals("")) {
                        File file = new File(photoCpath);
                        photoCpathBody = MultipartBody.Part.createFormData("photo_c", file.getName(), RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file));
                        photoCpathLogoName = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), file.getName());
                    }

                    MultipartBody.Part finalphotoCpath1Body = photoCpathBody;
                    RequestBody finalphotoCpath2LogoName = photoCpathLogoName;


                    RequestBody useIdRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), prefManager.getString(Constant.USER_ID));
                    RequestBody nameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), name);
                    RequestBody designationRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), designation);
                    RequestBody phoneRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), mobile_num);
                    RequestBody facebookUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), facebook_username);
                    RequestBody instaUsernameRB = RequestBody.create(MediaType.parse(ShareTarget.ENCODING_TYPE_MULTIPART), instagram_username);


                    String apiKey = "sdghhgh416546dd5654wst56w4646w46";

                    prgDialog.show();
                    Call<ApiResponse<List<PoliticalItem>>> call = ApiClient.getApiService().updatePolitical(apiKey, useIdRB,
                            finalFullName, finalBody, finalpoliProfileLogoName, finalpoliProfileBody, finalphotoApathName, finalphotoApathBody, finalphotoBpath2LogoName, finalphotoBpath1Body, finalphotoCpath2LogoName, finalphotoCpath1Body, nameRB, designationRB, phoneRB, facebookUsernameRB, instaUsernameRB
                    );
                    call.enqueue(new Callback<ApiResponse<List<PoliticalItem>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<PoliticalItem>>> call, Response<ApiResponse<List<PoliticalItem>>> response) {
                            if (response.isSuccessful()) {
                                prgDialog.cancel();
//
                                dialogMsg.showSuccessDialog(getString(R.string.success), getString(R.string.ok));
                                dialogMsg.show();
                                dialogMsg.okBtn.setOnClickListener(v1 -> {

                                    dialogMsg.cancel();
                                    startActivity(new Intent(AddBusinessActivity.this,MainActivity.class));
                                    finish();
//                                    onBackPressed();
                                });

                            } else {
                                dialogMsg.showErrorDialog(response.message(), getString(R.string.ok));
                                dialogMsg.show();
                                prgDialog.cancel();
                            }
                            Log.e("ResponseSuccess==>", response.message());
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<PoliticalItem>>> call, Throwable t) {
                            Log.e("ResponseError==>", t.getMessage());

                            dialogMsg.showErrorDialog(t.getMessage(), getString(R.string.ok));
                            dialogMsg.show();
                            prgDialog.cancel();
                        }
                    });
                }
            }
        });
        binding.btnAddPerImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(i);
            }
        });


        binding.btnAddPoliLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                politicsLogoLauncher.launch(i);
            }
        });

        binding.btnAddPoliProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                someActivityResultLauncher.launch(i);
            }
        });

        binding.btnAddPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photo1Launcher.launch(i);
            }
        });
        binding.btnAddPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photo2Launcher.launch(i);
            }
        });
        binding.btnAddPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photo3Launcher.launch(i);
            }
        });

    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), (String) null));
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void setData() {
        binding.etBusinessName.setText(businessItem.name);
        binding.etBusinessNumber.setText(businessItem.phone);
        binding.etBusinessAddress.setText(businessItem.address);
        binding.etBusinessEmail.setText(businessItem.email);
        binding.etBusinessWebsite.setText(businessItem.website);
        GlideBinding.bindImage(binding.ivBusiness, businessItem.logo);
    }

    private void setUpViewModel() {
        userViewModel.getDbUserData(prefManager.getString(Constant.USER_ID)).observe(this, result -> {
            if (result != null) {
                userItem = result.user;
            }
        });
    }

    private Boolean validate() {
        if (binding.etBusinessName.getText().toString().trim().isEmpty()) {
            binding.etBusinessName.setError(getResources().getString(R.string.hint_business_name));
            binding.etBusinessName.requestFocus();
            return false;
        } else if (binding.etBusinessEmail.getText().toString().trim().isEmpty()) {
            binding.etBusinessEmail.setError(getResources().getString(R.string.hint_business_email));
            binding.etBusinessEmail.requestFocus();
            return false;
        } else if (!isEmailValid(binding.etBusinessEmail.getText().toString())) {
            binding.etBusinessEmail.setError(getString(R.string.invalid_email));
            binding.etBusinessEmail.requestFocus();
            return false;
        } else if (binding.etBusinessWebsite.getText().toString().isEmpty()) {
            binding.etBusinessWebsite.setError(getResources().getString(R.string.hint_business_website));
            binding.etBusinessWebsite.requestFocus();
            return false;
        } else if (binding.etBusinessNumber.getText().toString().trim().isEmpty()) {
            binding.etBusinessNumber.setError(getResources().getString(R.string.hint_business_number));
            binding.etBusinessNumber.requestFocus();
            return false;
        } else if (binding.etBusinessAddress.getText().toString().trim().isEmpty()) {
            binding.etBusinessAddress.setError(getResources().getString(R.string.hint_business_address));
            binding.etBusinessAddress.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadBusiness() {
        prgDialog.show();
        ApiClient.getApiService().getBusinessCall("sdghhgh416546dd5654wst56w4646w46", this.prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<BusinessItem>>() {
            public void onResponse(Call<List<BusinessItem>> call, Response<List<BusinessItem>> response) {
                if (response.isSuccessful()) {
                    prgDialog.cancel();
                    prefManager.setString("p_name", ((BusinessItem) response.body().get(0)).name);
                    prefManager.setString("p_url", ((BusinessItem) response.body().get(0)).logo);

                    binding.etBusinessName.setText(((BusinessItem) response.body().get(0)).name);
                    binding.etBusinessNumber.setText(((BusinessItem) response.body().get(0)).phone);
                    binding.etBusinessAddress.setText(((BusinessItem) response.body().get(0)).address);
                    binding.etBusinessEmail.setText(((BusinessItem) response.body().get(0)).email);
                    binding.etBusinessWebsite.setText(((BusinessItem) response.body().get(0)).website);
                    GlideBinding.bindImage(binding.ivBusiness, ((BusinessItem) response.body().get(0)).logo);
                    binding.btnSave.setText("Update");
                    IS_EDIT_MODE = true;
                }
            }

            public void onFailure(Call<List<BusinessItem>> call, Throwable t) {
                prgDialog.cancel();
                Log.e("@@@@Business", t.getMessage());
            }
        });
    }

    private void loadPersonal() {
        prgDialog.show();
        ApiClient.getApiService().getPersonalCall("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<PersonalItem>>() {
            public void onResponse(Call<List<PersonalItem>> call, Response<List<PersonalItem>> response) {
                if (response.isSuccessful()) {
                    prgDialog.cancel();
                    prefManager.setString("p_name", ((PersonalItem) response.body().get(0)).name);
                    prefManager.setString("p_url", ((PersonalItem) response.body().get(0)).logo);

                    binding.etYourName.setText(((PersonalItem) response.body().get(0)).name);
                    binding.etYourNumber.setText(((PersonalItem) response.body().get(0)).phone);
                    binding.etYourAddress.setText(((PersonalItem) response.body().get(0)).address);
                    binding.etFacebookUsername.setText(((PersonalItem) response.body().get(0)).face_username);
                    binding.etInstagramUsername.setText(((PersonalItem) response.body().get(0)).insta_username);
                    try {
                        GlideBinding.bindImage(binding.ivPersonal, ((PersonalItem) response.body().get(0)).logo);
                    } catch (IllegalArgumentException e) {

                    }
                    binding.btnSavePersonal.setText("Update");
                }
            }

            public void onFailure(Call<List<PersonalItem>> call, Throwable t) {
                Log.e("@@@@Personal", t.getMessage());
                prgDialog.cancel();
            }
        });
    }

    private void loadPolitical() {
        prgDialog.show();
        ApiClient.getApiService().getPoliticalCall("sdghhgh416546dd5654wst56w4646w46", this.prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<PoliticalItem>>() {
            public void onResponse(Call<List<PoliticalItem>> call, Response<List<PoliticalItem>> response) {
                if (response.isSuccessful()) {
                    prgDialog.cancel();
                    prefManager.setString("p_name", ((PoliticalItem) response.body().get(0)).name);
                    prefManager.setString("p_url", ((PoliticalItem) response.body().get(0)).logo);

                    binding.etPoliMoNumber.setText(((PoliticalItem) response.body().get(0)).phone);
                    binding.etPoliName.setText(((PoliticalItem) response.body().get(0)).name);
                    binding.etPoliInstagramUsername.setText(((PoliticalItem) response.body().get(0)).instagram_username);
                    binding.etPoliDesignation.setText(((PoliticalItem) response.body().get(0)).designation);
                    binding.etPoliFacebookUsername.setText(((PoliticalItem) response.body().get(0)).facebook_username);
                    try {
                        GlideBinding.bindImage(binding.ivPoliLogo, ((PoliticalItem) response.body().get(0)).logo);

                        GlideBinding.bindImage(binding.ivPoliProfile, ((PoliticalItem) response.body().get(0)).profile);
                        GlideBinding.bindImage(binding.ivPhoto1, ((PoliticalItem) response.body().get(0)).photo1);
                        GlideBinding.bindImage(binding.ivPhoto2, ((PoliticalItem) response.body().get(0)).photo2);
                        GlideBinding.bindImage(binding.ivPhoto3, ((PoliticalItem) response.body().get(0)).photo3);
                    } catch (IllegalArgumentException e) {
                    }
                    binding.btnPoliSave.setText("Update");
                    Log.e("@@@@DataPoli", response.message());
                    return;
                }
                Log.e("@@@@Political", response.message());
            }

            public void onFailure(Call<List<PoliticalItem>> call, Throwable t) {
                Log.e("@@@@PoliticalFail", t.getMessage());
                prgDialog.cancel();

            }
        });
    }

    ActivityResultLauncher<Intent> politicsLogoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndexLogo = cursor.getColumnIndex(filePathColumn[0]);
                    polilogoPath = cursor.getString(columnIndexLogo);
                    cursor.close();
                    polilogoUri = selectedImage;
                    GlideBinding.bindImage(binding.ivPoliLogo, polilogoPath);
                }
            }
        }
    });

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    profileImagePath = cursor.getString(columnIndex);
                    poliprofileImageUri = selectedImage;
                    imageUri = selectedImage;
                    cursor.close();
                    GlideBinding.bindImage(binding.ivPoliProfile, profileImagePath);
                    GlideBinding.bindImage(binding.ivBusiness, profileImagePath);
                    GlideBinding.bindImage(binding.ivPersonal, profileImagePath);

                }
            }
        }
    });

    ActivityResultLauncher<Intent> personalImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    profileImagePath = cursor.getString(columnIndex);

                    cursor.close();
                    GlideBinding.bindImage(binding.ivPersonal, profileImagePath);

                }
            }
        }
    });


    ActivityResultLauncher<Intent> photo1Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoApath = cursor.getString(columnIndex);
                    photoAUri = selectedImage;
                    cursor.close();
                    GlideBinding.bindImage(binding.ivPhoto1, photoApath);

                }
            }
        }
    });


    ActivityResultLauncher<Intent> photo2Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoBPath = cursor.getString(columnIndex);
                    photoBUri = selectedImage;
                    cursor.close();
                    GlideBinding.bindImage(binding.ivPhoto2, photoBPath);

                }
            }
        }
    });


    ActivityResultLauncher<Intent> photo3Launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        public void onActivityResult(ActivityResult result) {
            Cursor cursor;
            if (result.getResultCode() == -1 && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                String[] filePathColumn = {"_data"};
                if (selectedImage != null && (cursor = getContentResolver().query(selectedImage, (String[]) null, (String) null, (String[]) null, (String) null)) != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoCPath = cursor.getString(columnIndex);
                    photoCUri = selectedImage;
                    cursor.close();
                    GlideBinding.bindImage(binding.ivPhoto3, photoCPath);

                }
            }
        }
    });

}