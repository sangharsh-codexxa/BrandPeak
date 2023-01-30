package com.readymadedata.app.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.FFprobe;
import com.arthenica.mobileffmpeg.MediaInformation;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.readymadedata.app.Ads.InterstitialAdManager;
import com.readymadedata.app.Config;
import com.readymadedata.app.MyApplication;
import com.readymadedata.app.R;
import com.readymadedata.app.adapters.DetailAdapter;
import com.readymadedata.app.adapters.FontAdapter;
import com.readymadedata.app.adapters.FrameAdapter;
import com.readymadedata.app.adapters.PersonalFrameAdapter;
import com.readymadedata.app.adapters.PoliticalFrameAdapter;
import com.readymadedata.app.adapters.StickerAdapter;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.databinding.ActivityEditorBinding;
import com.readymadedata.app.databinding.BusinessFrame2Binding;
import com.readymadedata.app.databinding.BusinessFrame3Binding;
import com.readymadedata.app.databinding.Frame1Binding;
import com.readymadedata.app.databinding.Frame6Binding;
import com.readymadedata.app.databinding.PersonalFrame1Binding;
import com.readymadedata.app.databinding.PersonalFrame2Binding;
import com.readymadedata.app.databinding.PoliticalFrame2Binding;
import com.readymadedata.app.items.AddTextItem;
import com.readymadedata.app.items.BusinessItem;
import com.readymadedata.app.items.FrameItem;
import com.readymadedata.app.items.PersonalItem;
import com.readymadedata.app.items.PoliticalItem;
import com.readymadedata.app.items.PostItem;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.ui.stickers.ElementInfo;
import com.readymadedata.app.ui.stickers.RelStickerView;
import com.readymadedata.app.ui.stickers.ViewIdGenerator;
import com.readymadedata.app.utils.BitmapUtil;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.ImageUtils;
import com.readymadedata.app.utils.OnDragTouchListener;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.BusinessViewModel;
import com.readymadedata.app.viewmodel.PersonalViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditorActivity extends AppCompatActivity implements InterstitialAdManager.Listener {
    private static final int SELECT_PICTURE_CAMERA = 805;
    private static final int SELECT_PICTURE_GALLERY = 807;
    String FOLDER_NAME = "";
    int currentPosition = 0;
    private final String[] PERMISSIONS = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    String VideoPath;
    ExoPlayer absPlayerInternal;
    DetailAdapter adapter;
    ArrayList<AddTextItem> addTextList = new ArrayList<>();
    String address;
    ActivityEditorBinding binding;
    BusinessItem businessItem;
    BusinessViewModel businessViewModel;
    DialogMsg dialogMsg;
    String email;
    String fileName = "";
    private FrameLayout fl_address;
    private FrameLayout fl_email;
    public FrameLayout fl_logo;
    private FrameLayout fl_name;
    private FrameLayout fl_phone;
    private FrameLayout fl_website;
    FrameAdapter frameAdapter;
    List<FrameItem> frameItemList;
    String imgUrl;
    boolean isFromUrl;
    boolean isImage = false;
    boolean isVideo = false;

    public ImageView iv_address;

    public ImageView iv_close_address;

    public ImageView iv_close_email;
    public ImageView iv_close_name;
    public ImageView iv_close_phone;
    public ImageView iv_close_website;
    public ImageView iv_email;
    private ImageView iv_frame;
    private ImageView iv_image;
    private ImageView iv_logo;

    private ImageButton ib_prevFrameApply, ib_nextFrameApply;

    private TextView tv_personal_name, tv_personal_address, tv_insta_personal, tv_facebook_personal, tv_persnol_num;
    private TextView tv_name_pol, tv_designation_poli, tv_mob_poli, tv_insta_poli, tv_face_poli;
    private String poliName, poliDesignation, poliMobile, poliInstagram, poliFacebook, profileImg, imgA, imgB, imgC;

    private ImageView iv_PoliProfile, iv_PhotoA, iv_PhotoB, iv_PhotoC, iv_designation_close, iv_insta_close, iv_fb_close;

    public ImageView iv_phone;

    public ImageView iv_website;
    String logo;
    private String name = "";
    String name1;
    RelStickerView.TouchEventListener newtouchlistener = new RelStickerView.TouchEventListener() {
        public void onDelete() {
        }

        public void onEdit(View view, Uri uri) {
        }

        public void onRotateDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onRotateMove(View view) {
            touchMove(view);
        }

        public void onRotateUp(View view) {
            touchUp(view);
        }

        public void onScaleDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onScaleMove(View view) {
            touchMove(view);
        }

        public void onScaleUp(View view) {
            touchUp(view);
        }

        public void onTouchDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onTouchMove(View view) {
            touchMove(view);
        }

        public void onTouchUp(View view) {
            touchUp(view);
        }

        public void onMainClick(View view) {
            Log.e("TOUCH", "MAIN TOUCH");
            setBackImage();
        }
    };
    PersonalFrameAdapter personalFrameAdapter;
    List<FrameItem> personalFrameList;
    LiveData<Resource<List<PersonalItem>>> personalItem;
    PersonalViewModel personalViewModel;
    String phone;
    PoliticalFrameAdapter politicalFrameAdapter;
    List<FrameItem> politicalFrameList;
    int pos;
    List<PostItem> postItemList;
    PrefManager prefManager;
    ProgressDialog progress;
    ProgressDialog progressDD;

    ProgressDialog progressDialog;
    RelStickerView.TouchEventListener rtouchlistener = new RelStickerView.TouchEventListener() {
        public void onDelete() {
        }

        public void onEdit(View view, Uri uri) {
        }

        public void onRotateDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onRotateMove(View view) {
            touchMove(view);
        }

        public void onRotateUp(View view) {
            touchUp(view);
        }

        public void onScaleDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onScaleMove(View view) {
            touchMove(view);
        }

        public void onScaleUp(View view) {
            touchUp(view);
        }

        public void onTouchDown(View view) {
            touchDown(view, "viewboder");
        }

        public void onTouchMove(View view) {
            touchMove(view);
        }

        public void onTouchUp(View view) {
            touchUp(view);
        }

        public void onMainClick(View view) {
            Log.e("TOUCH", "MAIN TOUCH");
            setBackImage();
        }
    };
    float sHeight;
    float sWidth;
    int screenWidth;
    int selectedTextPosition = -1;
    ExoPlayer sharePlayer;

    public TextView tv_address;

    public TextView tv_email;

    public TextView tv_name;

    public TextView tv_phone;

    public TextView tv_website;
    String type = Constant.BUSINESS;
    Uri uri;
    UserItem userItem;
    UserViewModel userViewModel;
    String businessName, businessNumber, businessEmail, businessWebsite, businessAddress;
    String personalName, personalNumber, personalAddress, personalFacebook, personalInstagram;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEditorBinding inflate = ActivityEditorBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        imgUrl = getIntent().getStringExtra("img_url");
        // Adding this line will prevent taking screenshot in your app
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        PrefManager prefManager2 = new PrefManager(this);
        this.prefManager = prefManager2;
        if (prefManager2.getString(Constant.FOLDER_NAME).equals("")) {
            this.FOLDER_NAME = "video_function";
            this.prefManager.setString(Constant.FOLDER_NAME, "video_function");
        } else {
            this.FOLDER_NAME = this.prefManager.getString(Constant.FOLDER_NAME);
        }
        dialogMsg = new DialogMsg(this, false);
        this.progress = new ProgressDialog(this);
        this.progressDD = new ProgressDialog(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sWidth = (float) displayMetrics.widthPixels;
        sHeight = (float) (displayMetrics.heightPixels - ImageUtils.dpToPx(this, 104));
        frameItemList = new ArrayList();
        personalFrameList = new ArrayList();
        politicalFrameList = new ArrayList<>();
        InterstitialAdManager.Interstitial(this, this);
        setImageShow();
        setUpUi();
        setUpViewModel();

        setPostData();


        Log.e("Type>>>>>>>", type);
    }

    private void setUpViewModel() {
        Log.e("PrimaryCategory--->", prefManager.getString(Constant.PRIMARY_CATEGORY));
        if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("")) {
            startActivity(new Intent(EditorActivity.this, AddBusinessActivity.class));
            Toast.makeText(this, "Select your frame type", Toast.LENGTH_SHORT).show();
        }
        businessViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);
        personalViewModel = new ViewModelProvider(this).get(PersonalViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getDbUserData(prefManager.getString(Constant.USER_ID)).observe(this, result -> {
            if (result != null) {
                userItem = result.user;
            }
        });


        if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("0")) {
            type = Constant.BUSINESS;


            ApiClient.getApiService().getBusinessCall("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<BusinessItem>>() {

                public void onResponse(Call<List<BusinessItem>> call, Response<List<BusinessItem>> response) {

                    if (response.body() != null) {

                        businessItem = response.body().get(0);
                        progressDialog.setMessage(response.body().toString());

                        Log.e("BusinessData=====>", ((BusinessItem) response.body().get(0)).logo.toString());
                        businessName = ((BusinessItem) response.body().get(0)).name;
                        businessEmail = ((BusinessItem) response.body().get(0)).email;
                        businessNumber = ((BusinessItem) response.body().get(0)).phone;
                        businessWebsite = ((BusinessItem) response.body().get(0)).website;
                        logo = ((BusinessItem) response.body().get(0)).logo;
                        businessAddress = ((BusinessItem) response.body().get(0)).address;
                        loadFrameData();

                    } else {
                        progressDialog.setMessage(response.message());

                        Log.e("BusinessError=====>", response.message());
                    }
                }

                public void onFailure(Call<List<BusinessItem>> call, Throwable t) {
                    Log.e("BusinessError=====>", t.getMessage());
                    progressDialog.cancel();
                    startActivity(new Intent(EditorActivity.this, AddBusinessActivity.class));
                    finish();
                }
            });

        }

        if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("1")) {
//            Toast.makeText(this, "Call Personal", Toast.LENGTH_SHORT).show();
            type = "personal";
//            progressDialog.show();
            ApiClient.getApiService().getPersonalCall("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<PersonalItem>>() {


                public void onResponse(Call<List<PersonalItem>> call, Response<List<PersonalItem>> response) {

                    if (response.body() != null) {
                        progressDialog.cancel();

                        personalName = ((PersonalItem) response.body().get(0)).name;
                        personalAddress = ((PersonalItem) response.body().get(0)).address;
                        personalNumber = ((PersonalItem) response.body().get(0)).phone;
                        logo = ((PersonalItem) response.body().get(0)).logo;
                        personalFacebook = ((PersonalItem) response.body().get(0)).face_username;
                        personalInstagram = ((PersonalItem) response.body().get(0)).insta_username;
                        loadFrameData();

                    } else {
                        progressDialog.cancel();

                        Log.e("PersonalError=====>", response.message());
                    }
                }

                public void onFailure(Call<List<PersonalItem>> call, Throwable t) {
                    Log.e("PeronalError=====>", t.getMessage());
                    progressDialog.cancel();

                    startActivity(new Intent(EditorActivity.this, AddBusinessActivity.class));

                }
            });
        }

        if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("2")) {
            type = "political";
            ApiClient.getApiService().getPoliticalCall("sdghhgh416546dd5654wst56w4646w46", prefManager.getString(Constant.USER_ID)).enqueue(new Callback<List<PoliticalItem>>() {

                public void onResponse(Call<List<PoliticalItem>> call, Response<List<PoliticalItem>> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    if (response.body() != null) {

                        poliName = ((PoliticalItem) response.body().get(0)).name;
                        poliDesignation = ((PoliticalItem) response.body().get(0)).designation;
                        poliMobile = ((PoliticalItem) response.body().get(0)).phone;
                        poliFacebook = ((PoliticalItem) response.body().get(0)).facebook_username;
                        poliInstagram = ((PoliticalItem) response.body().get(0)).instagram_username;
                        logo = ((PoliticalItem) response.body().get(0)).logo;
                        profileImg = ((PoliticalItem) response.body().get(0)).profile;
                        imgA = ((PoliticalItem) response.body().get(0)).photo1;
                        imgB = ((PoliticalItem) response.body().get(0)).photo2;
                        imgC = ((PoliticalItem) response.body().get(0)).photo3;
//                      progress = ((PoliticalItem) response.body().get(0)).profile;
                        loadFrameData();
                        return;
                    }
                    throw new AssertionError();
                }

                public void onFailure(Call<List<PoliticalItem>> call, Throwable t) {
                    Log.e("Error=====>", t.getMessage());
                    startActivity(new Intent(EditorActivity.this, AddBusinessActivity.class));
                    finish();
                }
            });
        }


    }


    private void loadFrameData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvFrame.setLayoutManager(layoutManager);


        userViewModel.getFrameData(prefManager.getString(Constant.USER_ID)).observe(this, result -> {
            if (result != null) {
                if (result.data != null) {
                    if (type.equals(Constant.BUSINESS)) {

                        frameAdapter = new FrameAdapter(this, position -> {

                            frameAdapter.setSelected(position);
                            currentPosition = position;
                            setFrameData(frameItemList.get(position));
                        }, 3, getResources().getDimension(com.intuit.ssp.R.dimen._2ssp));
                        frameAdapter.setSelected(0);
                        binding.ibNextFrameApply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                currentPosition++;
                                if (currentPosition > frameItemList.size()) {

                                    Log.e("CurrentPosition----->", String.valueOf(currentPosition));
//                    for (int i = 0; i <= frameItemList.size(); i++) {

                                    try {
                                        setFrameData(frameItemList.get(currentPosition));
                                        frameAdapter.setSelected(currentPosition);
                                    } catch (IndexOutOfBoundsException e) {
//                            binding.ibNextFrameApply.setClickable(false);
//                            binding.ibNextFrameApply.setAlpha( 0.5f);
                                        Toast.makeText(EditorActivity.this, "Last Frame" + String.valueOf(currentPosition), Toast.LENGTH_SHORT).show();
                                        currentPosition = frameItemList.size();
                                        e.printStackTrace();
                                    }
                                }
                            }


                        });
                        binding.ibPrevFrameApply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currentPosition--;
                                try {
                                    setFrameData(frameItemList.get(currentPosition));
                                    frameAdapter.setSelected(currentPosition);
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    currentPosition = 0;
                                    Toast.makeText(EditorActivity.this, "Last Frame" + String.valueOf(currentPosition), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        binding.rvFrame.setAdapter(frameAdapter);
                        frameItemList.clear();

                        frameItemList.add(new FrameItem(false, "", Frame6Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.business_frame_preview, false));

                        for (int i = 0; i < result.data.size(); i++) {
                            frameItemList.add(new FrameItem(true, result.data.get(i).imageUrl, null, R.drawable.frame_preview_8, false));
                        }
                        frameAdapter.setFrameItemList(frameItemList);
                        setFrameData(frameItemList.get(0));


                    }
                }
            }
        });

        if (type.equals("personal")) {

            personalFrameAdapter = new PersonalFrameAdapter(this, position -> {
                personalFrameAdapter.setSelected(position);
                currentPosition = position;
                setFrameData(personalFrameList.get(position));
            }, 3, 2);
            personalFrameAdapter.setSelected(0);
            binding.rvFrame.setAdapter(personalFrameAdapter);


            userViewModel.getFrameData(prefManager.getString(Constant.USER_ID)).observe(this, result -> {
                if (result != null) {
                    if (result.data != null) {
                        personalFrameList.clear();


                        Log.e("ResultFrames====>", String.valueOf(result.data.size()));

                        personalFrameList.add(new FrameItem(false, "", PersonalFrame1Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.personal_frame_preview, false));
//                        personalFrameList.add(new FrameItem(false, "", PersonalFrame2Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.personal_frame, false));

                        //        personalFrameList.add(new FrameItem(false, "", Frame2Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_2, false));
//                                personalFrameList.add(new FrameItem(false, "", Frame3Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_3, false));
//                                frameItemList.add(new FrameItem(false, "", Frame4Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_4, false));
//                                frameItemList.add(new FrameItem(false, "", Frame5Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_5, true));
//                                frameItemList.add(new FrameItem(false, "", Frame6Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_6, true));
//                                frameItemList.add(new FrameItem(false, "", Frame7Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_7, true));
//                                frameItemList.add(new FrameItem(false, "", Frame8Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_8, true));
                        for (int i = 0; i < result.data.size(); i++) {
                            personalFrameList.add(new FrameItem(true, result.data.get(i).imageUrl, null, 0, true));
                        }
                        personalFrameAdapter.setFrameItemList(personalFrameList);
                        setFrameData(personalFrameList.get(0));
                    }
                }
            });
        }

        if (type.equals("political")) {
            politicalFrameAdapter = new PoliticalFrameAdapter(this, position -> {
                politicalFrameAdapter.setSelected(position);
                setFrameData(politicalFrameList.get(position));
            }, 3, getResources().getDimension(com.intuit.ssp.R.dimen._2ssp));
            binding.rvFrame.setAdapter(politicalFrameAdapter);
            userViewModel.getFrameData(prefManager.getString(Constant.USER_ID)).observe(this, result -> {
                if (result != null) {
                    if (result.data != null) {
                        politicalFrameList.clear();
//                        politicalFrameList.add(new FrameItem(false, "", PoliticalFrame1Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_1, false));
                        politicalFrameList.add(new FrameItem(false, "", PoliticalFrame2Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.political_frame_preview, false));
//                                frameItemList.add(new FrameItem(false, "", Frame3Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_3, false));
//                                frameItemList.add(new FrameItem(false, "", Frame4Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_4, false));
//                                frameItemList.add(new FrameItem(false, "", Frame5Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_5, true));
//                                frameItemList.add(new FrameItem(false, "", Frame6Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_6, true));
//                                frameItemList.add(new FrameItem(false, "", Frame7Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_7, true));
//                                frameItemList.add(new FrameItem(false, "", Frame8Binding.inflate(getLayoutInflater()).getRoot(), R.drawable.frame_preview_8, true));
                        for (int i = 0; i < result.data.size(); i++) {
                            politicalFrameList.add(new FrameItem(true, result.data.get(i).imageUrl, null, 0, true));
                        }
                        politicalFrameAdapter.setFrameItemList(politicalFrameList);
                        setFrameData(politicalFrameList.get(0));
                    }
                }
            });
        }
    }


    private void setFrameData(FrameItem frame) {
        this.isFromUrl = frame.is_from_url;
        if (frame.is_from_url) {
            binding.ivFrame.setVisibility(View.VISIBLE);
            this.binding.flFrame.removeAllViews();
            return;
        }

        binding.ivFrame.setVisibility(View.GONE);
        binding.ivFrame.setVisibility(View.GONE);
        binding.flFrame.setVisibility(View.VISIBLE);
        binding.flFrame.removeAllViews();
        View frameView = frame.layout;
        binding.flFrame.addView(frameView);
        fl_logo = frameView.findViewById(R.id.fl_logo);
        fl_name = (FrameLayout) frameView.findViewById(R.id.fl_name);
        fl_email = (FrameLayout) frameView.findViewById(R.id.fl_email);
        fl_website = (FrameLayout) frameView.findViewById(R.id.fl_website);
        fl_phone = (FrameLayout) frameView.findViewById(R.id.fl_phone);
        fl_address = (FrameLayout) frameView.findViewById(R.id.fl_address);
        tv_name = (TextView) frameView.findViewById(R.id.tv_name);
        tv_website = (TextView) frameView.findViewById(R.id.tv_website);
        tv_phone = (TextView) frameView.findViewById(R.id.tv_phone);
        tv_email = (TextView) frameView.findViewById(R.id.tv_email);
        tv_address = (TextView) frameView.findViewById(R.id.tv_address);
        iv_logo = (ImageView) frameView.findViewById(R.id.iv_logo);
        iv_phone = (ImageView) frameView.findViewById(R.id.iv_phone);
        iv_email = (ImageView) frameView.findViewById(R.id.ivEmail);
        iv_website = (ImageView) frameView.findViewById(R.id.iv_website);
        iv_address = (ImageView) frameView.findViewById(R.id.iv_address);
        //Common Open Close Views
        iv_close_phone = (ImageView) frameView.findViewById(R.id.iv_phone_close);
        iv_close_name = (ImageView) frameView.findViewById(R.id.iv_name_close);
        iv_close_email = (ImageView) frameView.findViewById(R.id.iv_email_close);
        iv_close_website = (ImageView) frameView.findViewById(R.id.iv_website_close);
        iv_close_address = (ImageView) frameView.findViewById(R.id.iv_address_close);

        //Political Open Close Controller
        iv_designation_close = frameView.findViewById(R.id.iv_designation_close);
        iv_fb_close = frameView.findViewById(R.id.iv_fb_close);
        iv_insta_close = frameView.findViewById(R.id.iv_insta_close);


        //Personal Open Close Controller


        tv_personal_address = frameView.findViewById(R.id.tv_address_personal);
        tv_personal_name = frameView.findViewById(R.id.tv_name_personal);
        tv_insta_personal = frameView.findViewById(R.id.tv_instausername_pers);
        tv_facebook_personal = frameView.findViewById(R.id.tv_facebookusername_pers);
        tv_persnol_num = frameView.findViewById(R.id.tv_phone_personal);
        iv_fb_close = frameView.findViewById(R.id.iv_fb_close);
        iv_insta_close = frameView.findViewById(R.id.iv_insta_close);

        //political frames
        tv_name_pol = frameView.findViewById(R.id.tv_name_poli);
        tv_mob_poli = frameView.findViewById(R.id.tv_mob_poli);
        tv_insta_poli = frameView.findViewById(R.id.tv_instausername_poli);
        tv_face_poli = frameView.findViewById(R.id.tv_facebookusername_poli);
        tv_designation_poli = frameView.findViewById(R.id.tv_designation_poli);

        iv_PoliProfile = frameView.findViewById(R.id.iv_poli_profile);
        iv_PhotoA = frameView.findViewById(R.id.iv_photo_a);
        iv_PhotoB = frameView.findViewById(R.id.iv_photo_b);
        iv_PhotoC = frameView.findViewById(R.id.iv_photo_c);

        //End political frames
        fl_logo.setVisibility(View.VISIBLE);
        binding.cbName.setVisibility(GONE);
        binding.cbEmail.setVisibility(GONE);
        binding.cbPhone.setVisibility(GONE);
        binding.cbWebsite.setVisibility(GONE);
        binding.cbAdress.setVisibility(GONE);
        binding.cbLogo.setVisibility(GONE);
        if (type.equals("personal")) {
            binding.tvBusiness.setVisibility(GONE);
            tv_personal_name.setText(personalName);
            tv_insta_personal.setText(personalInstagram);
            tv_personal_address.setText(personalAddress);
            tv_facebook_personal.setText(personalFacebook);
            tv_persnol_num.setText(personalNumber);
            GlideBinding.bindImage(iv_logo, logo);
        }

        if (this.type.equals("political")) {
            binding.tvBusiness.setVisibility(GONE);
            tv_name_pol.setText(poliName);
            tv_mob_poli.setText(poliMobile);
            tv_designation_poli.setText(poliDesignation);
            tv_face_poli.setText(poliFacebook);
            tv_insta_poli.setText(poliInstagram);

            GlideBinding.bindImage(iv_PoliProfile, profileImg);

            GlideBinding.bindImage(iv_PhotoA, imgA);
            GlideBinding.bindImage(iv_PhotoB, imgB);
            GlideBinding.bindImage(iv_PhotoC, imgC);
            GlideBinding.bindImage(iv_logo, logo);
        }

        if (type.equals("business")) {

            tv_name.setText(businessName);
            tv_email.setText(businessEmail);
            tv_phone.setText(businessNumber);
            tv_website.setText(businessWebsite);
            tv_address.setText(businessAddress);


            GlideBinding.bindImage(iv_logo, logo);
            binding.cbName.setVisibility(VISIBLE);
            binding.cbEmail.setVisibility(VISIBLE);
            binding.cbPhone.setVisibility(VISIBLE);
            binding.cbWebsite.setVisibility(VISIBLE);
            binding.cbAdress.setVisibility(VISIBLE);
            binding.cbLogo.setVisibility(VISIBLE);
            binding.cbName.setChecked(true);
            binding.cbEmail.setChecked(true);
            binding.cbPhone.setChecked(true);
            binding.cbWebsite.setChecked(true);
            binding.cbAdress.setChecked(true);
            binding.cbLogo.setChecked(true);

        }

        progressDialog = new ProgressDialog(EditorActivity.this);

        binding.toolbar.txtEdit.setText(getResources().getString(R.string.save));
        binding.toolbar.llOption.setVisibility(GONE);
        binding.toolbar.txtEdit.setVisibility(VISIBLE);
        binding.toolbar.txtEdit.setOnClickListener(v -> {
            setBackImage();
            removeControl();
            if (type.equals("business")) {
                if (iv_close_name.getVisibility() == GONE && iv_close_phone.getVisibility() == GONE && iv_close_email.getVisibility() == GONE && iv_close_website.getVisibility() == GONE && iv_close_address.getVisibility() == GONE) {
                    fileName = System.currentTimeMillis() + ".jpeg";
                    new LoadSaveImage().execute();
                } else {
                    Toast.makeText(this, "Please unselect your selected items.", Toast.LENGTH_SHORT).show();
                }
            }

            if (type.equals("political")) {
                if (iv_close_name.getVisibility() == GONE && iv_close_phone.getVisibility() == GONE && iv_fb_close.getVisibility() == GONE && iv_insta_close.getVisibility() == GONE) {
                    fileName = System.currentTimeMillis() + ".jpeg";
                    new LoadSaveImage().execute();
                } else {
                    Toast.makeText(this, "Please unselect your selected items.", Toast.LENGTH_SHORT).show();
                }
            }


            if (type.equals("personal")) {
                if (iv_close_name.getVisibility() == GONE && iv_close_phone.getVisibility() == GONE && iv_fb_close.getVisibility() == GONE && iv_insta_close.getVisibility() == GONE && iv_close_address.getVisibility() == GONE) {
                    fileName = System.currentTimeMillis() + ".jpeg";
                    new LoadSaveImage().execute();
                } else {
                    Toast.makeText(this, "Please unselect your selected items.", Toast.LENGTH_SHORT).show();
                }
            }


        });

        setTextClick();

        if (type.equals(Constant.BUSINESS)) {

            try {
                if (businessItem.name != null || !businessItem.name.isEmpty()) {

                    Glide.with(this)
                            .load(businessItem.logo)
                            .circleCrop()
                            .into(iv_logo);
                } else {
                    startActivity(new Intent(EditorActivity.this, AddBusinessActivity.class));
                }


            } catch (NullPointerException e) {
            }

            if (type.equals("political")) {
                tv_name_pol.setOnTouchListener(new OnDragTouchListener(tv_name_pol, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                    public void onDragStart(View view) {
                        tv_name_pol.bringToFront();
                    }

                    public void onDragEnd(View view, Boolean delete) {
                    }
                }));

                tv_mob_poli.setOnTouchListener(new OnDragTouchListener(tv_mob_poli, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                    public void onDragStart(View view) {
                        tv_mob_poli.bringToFront();
                    }

                    public void onDragEnd(View view, Boolean delete) {
                    }
                }));
                tv_designation_poli.setOnTouchListener(new OnDragTouchListener(tv_designation_poli, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                    public void onDragStart(View view) {
                        tv_designation_poli.bringToFront();
                    }

                    public void onDragEnd(View view, Boolean delete) {
                    }
                }));
                fl_logo.setOnTouchListener(new OnDragTouchListener(this.fl_logo, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                    public void onDragStart(View view) {
                        fl_logo.bringToFront();
                    }

                    public void onDragEnd(View view, Boolean delete) {
                    }
                }));

            }

        }

        if (type.equals("personal")) {
            tv_personal_name.setOnTouchListener(new OnDragTouchListener(tv_personal_name, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_personal_name.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_persnol_num.setOnTouchListener(new OnDragTouchListener(tv_persnol_num, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_persnol_num.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));
            tv_personal_address.setOnTouchListener(new OnDragTouchListener(tv_personal_address, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_personal_address.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_facebook_personal.setOnTouchListener(new OnDragTouchListener(tv_facebook_personal, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_facebook_personal.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_insta_personal.setOnTouchListener(new OnDragTouchListener(tv_insta_personal, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_insta_personal.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));


            fl_logo.setOnTouchListener(new OnDragTouchListener(fl_logo, binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    fl_logo.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));
//            setTextClick();
        }
        if (type.equals("business")) {
            tv_email.setOnTouchListener(new OnDragTouchListener(this.tv_email, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_email.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_phone.setOnTouchListener(new OnDragTouchListener(this.tv_phone, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_phone.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));
            this.tv_website.setOnTouchListener(new OnDragTouchListener(this.tv_website, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_website.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));
            this.fl_logo.setOnTouchListener(new OnDragTouchListener(this.fl_logo, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    fl_logo.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

        }


        if (type.equals("political")) {
            tv_name_pol.setOnTouchListener(new OnDragTouchListener(tv_name_pol, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_name_pol.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_mob_poli.setOnTouchListener(new OnDragTouchListener(tv_mob_poli, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_mob_poli.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_designation_poli.setOnTouchListener(new OnDragTouchListener(tv_designation_poli, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_designation_poli.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            tv_insta_poli.setOnTouchListener(new OnDragTouchListener(tv_insta_poli, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_insta_poli.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));


            tv_face_poli.setOnTouchListener(new OnDragTouchListener(tv_face_poli, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    tv_face_poli.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));


            iv_PoliProfile.setOnTouchListener(new OnDragTouchListener(iv_PoliProfile, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    iv_PoliProfile.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            fl_logo.setOnTouchListener(new OnDragTouchListener(fl_logo, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    fl_logo.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            iv_PhotoA.setOnTouchListener(new OnDragTouchListener(iv_PhotoA, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    iv_PhotoA.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            iv_PhotoB.setOnTouchListener(new OnDragTouchListener(iv_PhotoB, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    iv_PhotoB.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

            iv_PhotoC.setOnTouchListener(new OnDragTouchListener(iv_PhotoC, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
                public void onDragStart(View view) {
                    iv_PhotoC.bringToFront();
                }

                public void onDragEnd(View view, Boolean delete) {
                }
            }));

        }
    }


    private void setTextClick() {
        if (type.equals("business")) {
            tv_name.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_name.getVisibility();
                tv_name.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_name.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_address.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_address.getVisibility();
                tv_address.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_address.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_phone.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_phone.getVisibility();
                tv_phone.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_phone.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });
            tv_website.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_website.getVisibility();
                tv_website.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_website.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_email.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_email.getVisibility();
                tv_email.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_email.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });
        }

        if (type.equals("personal")) {
            tv_personal_name.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_name.getVisibility();
                tv_personal_name.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_name.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_personal_address.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_address.getVisibility();
                tv_personal_address.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_address.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_persnol_num.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_phone.getVisibility();
                tv_persnol_num.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_phone.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_insta_personal.setOnClickListener(v -> {
                removeControl();
                int visible = iv_insta_close.getVisibility();
                tv_insta_personal.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_insta_close.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_facebook_personal.setOnClickListener(v -> {
                removeControl();
                int visible = iv_fb_close.getVisibility();
                iv_fb_close.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_fb_close.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });
        }


        if (type.equals("political")) {
            tv_name_pol.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_name.getVisibility();
                tv_name_pol.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_name.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_mob_poli.setOnClickListener(v -> {
                removeControl();
                int visible = iv_close_phone.getVisibility();
                tv_mob_poli.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_close_phone.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_designation_poli.setOnClickListener(v -> {
                removeControl();
                int visible = iv_designation_close.getVisibility();
                tv_designation_poli.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_designation_close.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_insta_poli.setOnClickListener(v -> {
                removeControl();
                int visible = iv_insta_close.getVisibility();
                tv_insta_poli.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_insta_close.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });

            tv_face_poli.setOnClickListener(v -> {
                removeControl();
                int visible = iv_fb_close.getVisibility();
                tv_face_poli.setBackgroundResource(visible == 0 ? 0 : R.drawable.rounded_border);
                iv_fb_close.setVisibility(visible == 0 ? GONE : VISIBLE);
                binding.rlFrame.setVisibility(GONE);
                binding.rlBusiness.setVisibility(GONE);
            });
        }


    }


    private void setUpUi() {
        binding.toolbar.toolbarIvMenu.setBackground(getDrawable(R.drawable.ic_back));
        binding.toolbar.toolbarIvMenu.setOnClickListener(v -> {
            if (absPlayerInternal != null) {
                absPlayerInternal.setPlayWhenReady(false);
                absPlayerInternal.stop();
                absPlayerInternal.seekTo(0);
            }
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
            }
            onBackPressed();
        });
        binding.toolbar.toolName.setText(getString(R.string.create));

        screenWidth = MyApplication.getColumnWidth(1, getResources().getDimension(com.intuit.ssp.R.dimen._10ssp));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.cvBase.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth;

        binding.cvBase.setLayoutParams(params);

        binding.ivFestImage.setOnClickListener(v -> {
            removeControl();
        });

        progressDialog = new ProgressDialog(EditorActivity.this);


        // ****** Bottom Menu Click *******

        binding.tvFrame.setOnClickListener(v -> {
            removeControl();
            int visible = binding.rlFrame.getVisibility();
            binding.rlFrame.setVisibility(visible == 0 ? GONE : VISIBLE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
        });

        binding.tvBusiness.setOnClickListener(v -> {
            removeControl();
            int visible = binding.rlBusiness.getVisibility();
            binding.rlBusiness.setVisibility(visible == 0 ? GONE : VISIBLE);
            binding.rlFrame.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
        });

        binding.tvTextSize.setOnClickListener(v -> {
            removeControl();
            int visible = binding.rlTextSize.getVisibility();
            binding.rlTextSize.setVisibility(visible == 0 ? GONE : VISIBLE);
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
        });

        binding.tvAddText.setOnClickListener(v -> {
            removeControl();
            addText(-1);
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
        });
        binding.tvTextColor.setOnClickListener(v -> {
            removeControl();
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
            ColorPickerDialog pickerDialog = ColorPickerDialog.newBuilder()
                    .setDialogId(1)
                    .setColor(Color.BLACK)
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setDialogTitle(R.string.app_name)
                    .setAllowCustom(true)
                    .setPresets(getResources().getIntArray(R.array.colorlist))
                    .setShowAlphaSlider(false)
                    .setColorShape(0)
                    .create();

            pickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
                @Override
                public void onColorSelected(int dialogId, int color) {
                    if (!isFromUrl) {
//                        if ((iv_close_name == null) ||
//                                (iv_close_email == null) ||
//                                (iv_close_phone == null) ||
//                                (iv_close_website == null) ||
//                                (iv_close_address == null)
//                        ) return;

                        if (type.equals("business")) {
                            if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_close_website.getVisibility() == View.GONE && iv_close_email.getVisibility() == View.GONE &&
                                    iv_close_address.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE) {
                                tv_address.setTextColor(color);
                                tv_email.setTextColor(color);
                                tv_phone.setTextColor(color);
                                tv_website.setTextColor(color);
                                tv_name.setTextColor(color);

                                iv_phone.setImageTintList(ColorStateList.valueOf(color));
                                iv_email.setImageTintList(ColorStateList.valueOf(color));
                                iv_website.setImageTintList(ColorStateList.valueOf(color));
                                iv_address.setImageTintList(ColorStateList.valueOf(color));

                                for (int i = 0; i < addTextList.size(); i++) {
                                    addTextList.get(i).tv_text.setTextColor(color);
                                }
                            } else {
                                if (iv_close_name.getVisibility() == View.VISIBLE) {
                                    tv_name.setTextColor(color);
                                }
                                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                                    tv_phone.setTextColor(color);
                                    iv_phone.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_close_email.getVisibility() == View.VISIBLE) {
                                    tv_email.setTextColor(color);
                                    iv_email.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_close_address.getVisibility() == View.VISIBLE) {
                                    tv_address.setTextColor(color);
                                    iv_address.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_close_website.getVisibility() == View.VISIBLE) {
                                    tv_website.setTextColor(color);
                                    iv_website.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (isTextSelected() && addTextList.size() > 0) {
                                    addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
                                }
                            }
                        }

                        if (type.equals("personal")) {
                            if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE &&
                                    iv_close_address.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE && iv_insta_close.getVisibility() == View.GONE && iv_fb_close.getVisibility() == View.GONE) {
                                tv_personal_address.setTextColor(color);
                                tv_persnol_num.setTextColor(color);
                                tv_personal_name.setTextColor(color);
                                tv_facebook_personal.setTextColor(color);
                                tv_insta_personal.setTextColor(color);

                                for (int i = 0; i < addTextList.size(); i++) {
                                    addTextList.get(i).tv_text.setTextColor(color);
                                }
                            } else {
                                if (iv_close_name.getVisibility() == View.VISIBLE) {
                                    tv_personal_name.setTextColor(color);
                                }
                                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                                    tv_persnol_num.setTextColor(color);
                                }

                                if (iv_close_address.getVisibility() == View.VISIBLE) {
                                    tv_personal_address.setTextColor(color);
                                }

                                if (iv_fb_close.getVisibility() == View.VISIBLE) {
                                    tv_facebook_personal.setTextColor(color);
                                }

                                if (iv_insta_close.getVisibility() == View.VISIBLE) {
                                    tv_insta_personal.setTextColor(color);
                                }

                                if (isTextSelected() && addTextList.size() > 0) {
                                    addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
                                }
                            }
                        }


                        if (type.equals("political")) {
                            Toast.makeText(EditorActivity.this, "Political", Toast.LENGTH_SHORT).show();
                            if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_insta_close.getVisibility() == View.GONE && iv_fb_close.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE && iv_designation_close.getVisibility() == GONE) {
                                tv_designation_poli.setTextColor(color);
                                tv_face_poli.setTextColor(color);
                                tv_mob_poli.setTextColor(color);
                                tv_insta_poli.setTextColor(color);
                                tv_name_pol.setTextColor(color);

//                             iv_phone.setImageTintList(ColorStateList.valueOf(color));
//                             iv_email.setImageTintList(ColorStateList.valueOf(color));
//                             iv_website.setImageTintList(ColorStateList.valueOf(color));
//                             iv_address.setImageTintList(ColorStateList.valueOf(color));

                                for (int i = 0; i < addTextList.size(); i++) {
                                    addTextList.get(i).tv_text.setTextColor(color);
                                }
                            } else {
                                if (iv_close_name.getVisibility() == View.VISIBLE) {
                                    tv_name_pol.setTextColor(color);
                                }
                                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                                    tv_mob_poli.setTextColor(color);
//                                  iv_phone.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_insta_close.getVisibility() == View.VISIBLE) {
                                    tv_insta_poli.setTextColor(color);
//                                 iv_email.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_fb_close.getVisibility() == View.VISIBLE) {
                                    tv_face_poli.setTextColor(color);
//                                iv_address.setImageTintList(ColorStateList.valueOf(color));
                                }
                                if (iv_designation_close.getVisibility() == View.VISIBLE) {
                                    tv_designation_poli.setTextColor(color);
//                                 iv_website.setImageTintList(ColorStateList.valueOf(color));
                                }

                            }
                        }


                    } else {
                        if (isTextSelected() && addTextList.size() > 0) {
                            addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
                        }
                    }

                }

                @Override
                public void onDialogDismissed(int dialogId) {

                }
            });
            pickerDialog.show(Objects.requireNonNull(getSupportFragmentManager()), "color");
        });


//        binding.tvTextColor.setOnClickListener(v -> {
//            removeControl();
//            binding.rlFrame.setVisibility(GONE);
//            binding.rlBusiness.setVisibility(GONE);
//            binding.rlTextSize.setVisibility(GONE);
//            binding.rlTextFont.setVisibility(GONE);
//            binding.rlSticker.setVisibility(GONE);
//            try {
//                ColorPickerDialog pickerDialog = ColorPickerDialog.newBuilder().setDialogId(1).setColor(Color.BLACK).setDialogType(ColorPickerDialog.TYPE_PRESETS).setDialogTitle(R.string.app_name).setAllowCustom(true).setPresets(getResources().getIntArray(R.array.colorlist)).setShowAlphaSlider(false).setColorShape(0).create();
//                pickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
//                    @Override
//                    public void onColorSelected(int dialogId, int color) {
//                        if (!isFromUrl) {
//                            if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_close_website.getVisibility() == View.GONE && iv_close_email.getVisibility() == View.GONE && iv_close_address.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE) {
//                                tv_address.setTextColor(color);
//                                tv_email.setTextColor(color);
//                                tv_phone.setTextColor(color);
//                                tv_website.setTextColor(color);
//                                tv_name.setTextColor(color);
//
//                                iv_phone.setImageTintList(ColorStateList.valueOf(color));
//                                iv_email.setImageTintList(ColorStateList.valueOf(color));
//                                iv_website.setImageTintList(ColorStateList.valueOf(color));
//                                iv_address.setImageTintList(ColorStateList.valueOf(color));
//
//                                for (int i = 0; i < addTextList.size(); i++) {
//                                    addTextList.get(i).tv_text.setTextColor(color);
//                                }
//                            } else {
//                                if (iv_close_name.getVisibility() == View.VISIBLE) {
//                                    tv_name.setTextColor(color);
//                                }
//                                if (iv_close_phone.getVisibility() == View.VISIBLE) {
//                                    tv_phone.setTextColor(color);
//                                    iv_phone.setImageTintList(ColorStateList.valueOf(color));
//                                }
//                                if (iv_close_email.getVisibility() == View.VISIBLE) {
//                                    tv_email.setTextColor(color);
//                                    iv_email.setImageTintList(ColorStateList.valueOf(color));
//                                }
//                                if (iv_close_address.getVisibility() == View.VISIBLE) {
//                                    tv_address.setTextColor(color);
//                                    iv_address.setImageTintList(ColorStateList.valueOf(color));
//                                }
//                                if (iv_close_website.getVisibility() == View.VISIBLE) {
//                                    tv_website.setTextColor(color);
//                                    iv_website.setImageTintList(ColorStateList.valueOf(color));
//                                }
//                                if (isTextSelected() && addTextList.size() > 0) {
//                                    addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
//                                }
//                            }
//                        } else {
//                            if (isTextSelected() && addTextList.size() > 0) {
//                                addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onDialogDismissed(int dialogId) {
//
//                    }
//                });
//                pickerDialog.show(Objects.requireNonNull(getSupportFragmentManager()), "color");
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
//
//        });

        binding.sdSize.addOnChangeListener((slider, value, fromUser) -> {

            changeFontSize(value);
        });

        binding.tvFont.setOnClickListener(v -> {
            removeControl();
            int visible = binding.rlTextFont.getVisibility();
            binding.rlTextFont.setVisibility(visible == 0 ? GONE : VISIBLE);
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
            showFont();
        });

        binding.tvAddImage.setOnClickListener(v -> {
            removeControl();
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            binding.rlSticker.setVisibility(GONE);
            addImage();
        });

        binding.tvAddSticker.setOnClickListener(v -> {
            int visible = binding.rlSticker.getVisibility();
            binding.rlSticker.setVisibility(visible == 0 ? GONE : VISIBLE);
            binding.rlFrame.setVisibility(GONE);
            binding.rlBusiness.setVisibility(GONE);
            binding.rlTextSize.setVisibility(GONE);
            binding.rlTextFont.setVisibility(GONE);
            loadSticker();
        });

        // ********* Check Box Region ******
        try {

            binding.cbName.setOnClickListener(v -> {
                int visible = fl_name.getVisibility();
                fl_name.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_name.getVisibility();
                binding.cbName.setChecked(visible == 0);
            });

            binding.cbAdress.setOnClickListener(v -> {
                int visible = fl_address.getVisibility();
                fl_address.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_address.getVisibility();
                binding.cbAdress.setChecked(visible == 0);
            });

            binding.cbLogo.setOnClickListener(v -> {
                int visible = fl_logo.getVisibility();
                fl_logo.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_logo.getVisibility();
                binding.cbLogo.setChecked(visible == 0);
            });

            binding.cbPhone.setOnClickListener(v -> {
                int visible = fl_phone.getVisibility();
                fl_phone.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_phone.getVisibility();
                binding.cbPhone.setChecked(visible == 0);
            });

            binding.cbWebsite.setOnClickListener(v -> {
                int visible = fl_website.getVisibility();
                fl_website.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_website.getVisibility();
                binding.cbWebsite.setChecked(visible == 0);
            });
            binding.cbEmail.setOnClickListener(v -> {
                int visible = fl_email.getVisibility();
                fl_email.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_email.getVisibility();
                binding.cbEmail.setChecked(visible == 0);
            });
        } catch (NullPointerException e) {
            binding.cbLogo.setOnClickListener(v -> {
                int visible = fl_logo.getVisibility();
                fl_logo.setVisibility(visible == 0 ? GONE : VISIBLE);
                visible = fl_logo.getVisibility();
                binding.cbLogo.setChecked(visible == 0);
            });


        }


    }


    public void setUpUi(View v) {
        removeControl();
        this.binding.rlFrame.setVisibility(VISIBLE);
        this.binding.rlBusiness.setVisibility(View.GONE);
        this.binding.rlTextSize.setVisibility(View.GONE);
        this.binding.rlTextFont.setVisibility(View.GONE);
        this.binding.rlSticker.setVisibility(View.GONE);
        ColorPickerDialog pickerDialog = ColorPickerDialog.newBuilder().setDialogId(1).setColor(-16777216).setDialogType(ColorPickerDialog.TYPE_PRESETS).setDialogTitle(R.string.app_name).setAllowCustom(true).setPresets(getResources().getIntArray(R.array.colorlist)).setShowAlphaSlider(false).setColorShape(0).create();
        pickerDialog.setColorPickerDialogListener(new ColorPickerDialogListener() {
            public void onColorSelected(int dialogId, int color) {
                if (!isFromUrl) {
                    if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_close_website.getVisibility() == View.GONE && iv_close_email.getVisibility() == View.GONE && iv_close_address.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE) {
                        tv_address.setTextColor(color);
                        tv_email.setTextColor(color);
                        tv_phone.setTextColor(color);
                        tv_website.setTextColor(color);
                        tv_name.setTextColor(color);
                        iv_phone.setImageTintList(ColorStateList.valueOf(color));
                        iv_email.setImageTintList(ColorStateList.valueOf(color));
                        iv_website.setImageTintList(ColorStateList.valueOf(color));
                        iv_address.setImageTintList(ColorStateList.valueOf(color));
                        for (int i = 0; i < addTextList.size(); i++) {
                            addTextList.get(i).tv_text.setTextColor(color);
                        }
                        return;
                    }
                    if (iv_close_name.getVisibility() == View.VISIBLE) {
                        tv_name.setTextColor(color);
                    }
                    if (iv_close_phone.getVisibility() == View.VISIBLE) {
                        tv_phone.setTextColor(color);
                        iv_phone.setImageTintList(ColorStateList.valueOf(color));
                    }
                    if (iv_close_email.getVisibility() == View.VISIBLE) {
                        tv_email.setTextColor(color);
                        iv_email.setImageTintList(ColorStateList.valueOf(color));
                    }
                    if (iv_close_address.getVisibility() == View.VISIBLE) {
                        tv_address.setTextColor(color);
                        iv_address.setImageTintList(ColorStateList.valueOf(color));
                    }
                    if (iv_close_website.getVisibility() == View.VISIBLE) {
                        tv_website.setTextColor(color);
                        iv_website.setImageTintList(ColorStateList.valueOf(color));
                    }
                    if (isTextSelected() && addTextList.size() > 0) {
                        addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
                    }
                } else if (isTextSelected() && addTextList.size() > 0) {
                    addTextList.get(selectedTextPosition).tv_text.setTextColor(color);
                }
            }

            public void onDialogDismissed(int dialogId) {
            }
        });
        pickerDialog.show((FragmentManager) Objects.requireNonNull(getSupportFragmentManager()), "color");
    }


    public void onAdFailedToLoad() {
    }

    public void onAdDismissed() {
        showPreviewDialog();
    }

    public void onResume() {
        super.onResume();
        if (!InterstitialAdManager.isLoaded()) {
            InterstitialAdManager.LoadAds();
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
            progress.setMessage("Downloading...");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            try {
                progress.setIndeterminate(true);
            } catch (Exception e) {

            }
            progress.setProgress(0);
            progress.show();

        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER_NAME + "/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdir();
                } else {
                    for (File file : dir.listFiles()) {
                        file.delete();
                    }
                    dir.mkdir();
                }

                File filename = new File(dir, "video.mp4");

                if (filename.exists()) {
                    getContentResolver().delete(Uri.fromFile(filename), null, null);
                }
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }
                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progres) {
            super.onProgressUpdate(progres);
            progress.setIndeterminate(false);
            progress.setMax(100);
            progress.setProgress(progres[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            MediaScannerConnection.scanFile(EditorActivity.this, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER_NAME + "/" + "video.mp4"}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String newpath, Uri newuri) {
                    Util.showLog("ExternalStorage: Scanned " + newpath + ":");
                    Util.showLog("ExternalStorage: -> uri=" + newuri);

                    progress.dismiss();
                    startCreating();
                }
            });

//            CreatingTask creatingTask = new CreatingTask(EditorActivity.this);
//            creatingTask.execute(postItemList.get(pos).image_url);
        }
    }

    private void startCreating() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!userItem.isSubscribed) {
                    binding.ivFrameWatermark.setVisibility(VISIBLE);
                } else {
                    binding.ivFrameWatermark.setVisibility(VISIBLE);
                }
                if (!frameItemList.get(frameAdapter.getSelectedPos()).is_from_url) {
                    removeControl();
                    tv_name.setBackground(null);
                    tv_email.setBackground(null);
                    tv_phone.setBackground(null);
                    tv_website.setBackground(null);
                    tv_address.setBackground(null);

                    iv_close_name.setVisibility(View.GONE);
                    iv_close_email.setVisibility(View.GONE);
                    iv_close_phone.setVisibility(View.GONE);
                    iv_close_website.setVisibility(View.GONE);
                    iv_close_address.setVisibility(View.GONE);

                    if (isTextSelected()) {
                        setTextSelected(addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                    }
                } else {
                    if (isTextSelected()) {
                        setTextSelected(addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                    }
                }

                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + FOLDER_NAME + "BrandPeakVideos.mp4";
                File dir = new File(path);
                if (dir.exists()) {
                    dir.delete();
                }

                binding.flForVideo.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createScaledBitmap(viewToBitmap(binding.flForVideo), 1080, 1080, true);
                String strPath = saveImage(bitmap);

                StringBuilder outputDir = new StringBuilder();
                File file4 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                outputDir.append(file4.getAbsolutePath());
                outputDir.append("/" + FOLDER_NAME + "/");
                outputDir.append("BrandPeakVideos.mp4");

                StringBuilder inputDir = new StringBuilder();
                File file5 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                inputDir.append(file5.getAbsolutePath());
                inputDir.append("/" + FOLDER_NAME + "/");
                inputDir.append("video.mp4");

                MediaInformation info = FFprobe.getMediaInformation(inputDir.toString());
                Util.showLog("" + info.getFilename() + info.getMediaProperties());

                progressDD.setMessage("Creating...");
                progressDD.setCancelable(false);
                progressDD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDD.setIndeterminate(true);
                progressDD.setProgress(0);
                progressDD.show();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(inputDir.toString());
                long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                com.arthenica.mobileffmpeg.Config.enableStatisticsCallback(new StatisticsCallback() {
                    @Override
                    public void apply(Statistics statistics) {
                        float progresss = Float.parseFloat(String.valueOf(statistics.getTime())) / duration;
                        float progressFinal = progresss * 100;
                        try {
                            progressDD.setIndeterminate(false);
                        } catch (Exception e) {

                        }
                        progressDD.setMax(100);
                        progressDD.setProgress((int) progressFinal);
                    }
                });

                long executionId = FFmpeg.executeAsync(new String[]{"-i", info.getFilename(), "-i", strPath, "-filter_complex", /*"overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2"*/
                        "overlay", "-r", "150", "-vb", "20M", "-y", outputDir.toString()}, new ExecuteCallback() {
                    @Override
                    public void apply(long executionId, int returnCode) {
                        Util.showLog("FFM: " + executionId + " " + returnCode);
                        if (returnCode == 1) {
                            FFmpeg.cancel(executionId);
                            progressDD.dismiss();
                            Util.showToast(EditorActivity.this, "Try Again!!");
                            if (FOLDER_NAME.equals("video_function")) {
                                FOLDER_NAME = "video_function_a";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_a")) {
                                FOLDER_NAME = "video_function_b";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_b")) {
                                FOLDER_NAME = "video_function_c";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_c")) {
                                FOLDER_NAME = "video_function_d";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_d")) {
                                FOLDER_NAME = "video_function_e";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_e")) {
                                FOLDER_NAME = "video_function_f";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_f")) {
                                FOLDER_NAME = "video_function_g";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_g")) {
                                FOLDER_NAME = "video_function_h";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            } else if (FOLDER_NAME.equals("video_function_h")) {
                                FOLDER_NAME = "video_function_i";
                                prefManager.setString(Constant.FOLDER_NAME, FOLDER_NAME);
                            }
                        }
                        if (returnCode == 0) {
                            FFmpeg.cancel(executionId);
                            progressDD.dismiss();
                            binding.ivFrameWatermark.setVisibility(GONE);
                            if (InterstitialAdManager.isLoaded() && prefManager.getInt(Constant.CLICK) >= prefManager.getInt(Constant.INTERSTITIAL_AD_CLICK)) {
                                prefManager.setInt(Constant.CLICK, 0);
                                InterstitialAdManager.showAds();
                            } else {
                                prefManager.setInt(Constant.CLICK, prefManager.getInt(Constant.CLICK) + 1);
                                showPreviewDialog();
                            }
                        } else if (returnCode == 255) {
                            Log.e("mobile-ffmpeg", "Command execution cancelled by user.");
                        } else {
                            String str = String.format("Command execution failed with rc=%d and the output below.", Arrays.copyOf(new Object[]{Integer.valueOf(returnCode)}, 1));
                            Log.i("mobile-ffmpeg", str);
                        }
                    }
                });

                binding.flForVideo.setDrawingCacheEnabled(false);

                VideoPath = outputDir.toString();
            }
        });
    }

    public class CreatingTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public CreatingTask(Context context) {
            this.context = context;
            progress.setMessage("Creating...");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!userItem.isSubscribed) {
                binding.ivFrameWatermark.setVisibility(VISIBLE);
            } else {
                binding.ivFrameWatermark.setVisibility(GONE);
            }
            if (!frameItemList.get(frameAdapter.getSelectedPos()).is_from_url) {
                removeControl();
                tv_name.setBackground(null);
                tv_email.setBackground(null);
                tv_phone.setBackground(null);
                tv_website.setBackground(null);
                tv_address.setBackground(null);

                iv_close_name.setVisibility(View.GONE);
                iv_close_email.setVisibility(View.GONE);
                iv_close_phone.setVisibility(View.GONE);
                iv_close_website.setVisibility(View.GONE);
                iv_close_address.setVisibility(View.GONE);

                if (isTextSelected()) {
                    setTextSelected(addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                }
            } else {
                if (isTextSelected()) {
                    setTextSelected(addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int i = Integer.parseInt(s);
            if (i == 0) {
                progress.dismiss();
                binding.ivFrameWatermark.setVisibility(GONE);
                if (InterstitialAdManager.isLoaded() && prefManager.getInt(Constant.CLICK) >= prefManager.getInt(Constant.INTERSTITIAL_AD_CLICK)) {
                    prefManager.setInt(Constant.CLICK, 0);
                    InterstitialAdManager.showAds();
                } else {
                    prefManager.setInt(Constant.CLICK, prefManager.getInt(Constant.CLICK) + 1);
                    showPreviewDialog();
                }
            } else if (i == 255) {
                Log.e("mobile-ffmpeg", "Command execution cancelled by user.");
            } else {
                String str = String.format("Command execution failed with rc=%d and the output below.", Arrays.copyOf(new Object[]{Integer.valueOf(i)}, 1));
                Log.i("mobile-ffmpeg", str);
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "video_function/" + "BrandPeakVideos.mp4";
            File dir = new File(path);
            if (dir.exists()) {
                dir.delete();
            }

            binding.flForVideo.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createScaledBitmap(viewToBitmap(binding.flForVideo), 1080, 1080, true);
            String strPath = saveImage(bitmap);

            StringBuilder outputDir = new StringBuilder();
            File file4 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            outputDir.append(file4.getAbsolutePath());
            outputDir.append("/video_function/");
            outputDir.append("BrandPeakVideos.mp4");

            StringBuilder inputDir = new StringBuilder();
            File file5 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            inputDir.append(file5.getAbsolutePath());
            inputDir.append("/video_function/");
            inputDir.append("video.mp4");

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(inputDir.toString());
            long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            try {
                retriever.release();
            } catch (IOException e) {
                e.printStackTrace();
            }

            com.arthenica.mobileffmpeg.Config.enableStatisticsCallback(new StatisticsCallback() {
                @Override
                public void apply(Statistics statistics) {
                    float progresss = Float.parseFloat(String.valueOf(statistics.getTime())) / duration;
                    float progressFinal = progresss * 100;
                    try {
                        progress.setIndeterminate(false);
                    } catch (Exception e) {

                    }
                    progress.setMax(100);
                    progress.setProgress((int) progressFinal);
                }
            });

            int i = FFmpeg.execute(new String[]{"-i", inputDir.toString(), "-i", strPath, "-filter_complex", /*"overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2"*/
                    "overlay", "-r", "150", "-vb", "20M", "-y", outputDir.toString()});

            binding.flForVideo.setDrawingCacheEnabled(false);

            VideoPath = outputDir.toString();

            return String.valueOf(i);
        }
    }

    private String saveImage(Bitmap paramBitmap) {

        File directory = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + FOLDER_NAME + File.separator);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, "Image-Bitmap.png");
        if (file.exists()) {
            file.delete();
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return file.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

    class LoadSaveImage extends AsyncTask<String, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.e("UserItemIs-->", String.valueOf(userItem.isSubscribed));
            if (!userItem.isSubscribed) {
                binding.ivFrameWatermark.setVisibility(VISIBLE);
            } else {
                binding.ivFrameWatermark.setVisibility(GONE);
            }

            if (type.equals("personal")) {
                try {
                    if (!frameItemList.get(frameAdapter.getSelectedPos()).is_from_url) {
                        removeControl();
                        tv_name.setBackground(null);
                        tv_email.setBackground(null);
                        tv_phone.setBackground(null);
                        tv_website.setBackground(null);
                        tv_address.setBackground(null);

                        iv_close_name.setVisibility(View.GONE);
                        iv_close_email.setVisibility(View.GONE);
                        iv_close_phone.setVisibility(View.GONE);
                        iv_close_website.setVisibility(View.GONE);
                        iv_close_address.setVisibility(View.GONE);

                    }
                } catch (NullPointerException e) {

                }
                if (isTextSelected()) {

                    setTextSelected(addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                }
            }
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
//              binding.videoPlayer.setDrawingCacheEnabled(true);
                binding.ivFestImage.setDrawingCacheEnabled(true);
                binding.ivFrame.setDrawingCacheEnabled(true);
                binding.flBackSticker.setDrawingCacheEnabled(true);
                binding.flSticker.setDrawingCacheEnabled(true);
                binding.flFrame.setDrawingCacheEnabled(true);
                binding.ivFrameWatermark.setDrawingCacheEnabled(true);
                binding.flForVideo.setDrawingCacheEnabled(true);
                binding.cvBase.setDrawingCacheEnabled(true);

                Bitmap bitmap = viewToBitmap(binding.cvBase);
                int multiplier = (int) getResources().getDimension(com.intuit.ssp.R.dimen._1ssp);
                Util.showLog("Multiplier: " + multiplier);
                bitmap = Bitmap.createScaledBitmap(bitmap, 1024 * multiplier, 1024 * multiplier, true);
                Constant.bitmap = bitmap;

                binding.videoPlayer.setDrawingCacheEnabled(false);
                binding.ivFestImage.setDrawingCacheEnabled(false);
                binding.ivFrame.setDrawingCacheEnabled(false);
                binding.flBackSticker.setDrawingCacheEnabled(false);
                binding.flSticker.setDrawingCacheEnabled(false);
                binding.flFrame.setDrawingCacheEnabled(false);
                binding.ivFrameWatermark.setDrawingCacheEnabled(false);
                binding.flForVideo.setDrawingCacheEnabled(false);
                binding.cvBase.setDrawingCacheEnabled(false);
                progressDialog.dismiss();
                return Constant.bitmap != null;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if (s) {
                binding.cvBase.setDrawingCacheEnabled(false);
                binding.ivFrameWatermark.setVisibility(GONE);
                if (InterstitialAdManager.isLoaded() && prefManager.getInt(Constant.CLICK) >= prefManager.getInt(Constant.INTERSTITIAL_AD_CLICK)) {
                    prefManager.setInt(Constant.CLICK, 0);
                    InterstitialAdManager.showAds();
                } else {
                    prefManager.setInt(Constant.CLICK, prefManager.getInt(Constant.CLICK) + 1);
                    showPreviewDialog();
                }
            } else {
                Toast.makeText(EditorActivity.this, getString(R.string.err_creating_image), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    private void showPreviewDialog() {

        int screenWidth;
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.download_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setCancelable(false);
        screenWidth = MyApplication.getColumnWidth(1, getResources().getDimension(com.intuit.ssp.R.dimen._10ssp));


        CardView cv_base = dialog.findViewById(R.id.cv_base);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) cv_base.getLayoutParams();
        params.width = screenWidth;
        params.height = screenWidth;

        cv_base.setLayoutParams(params);

        TextView title = dialog.findViewById(R.id.save_title);
        ImageView iv_cancel = dialog.findViewById(R.id.iv_close);
        ImageView iv_preview = dialog.findViewById(R.id.iv_save_image);
        ImageView iv_download = dialog.findViewById(R.id.iv_download);
        ImageView iv_whatsapp = dialog.findViewById(R.id.ic_whatsapp);
        ImageView iv_facebook = dialog.findViewById(R.id.ic_facebook);
        ImageView iv_instagram = dialog.findViewById(R.id.ic_instagram);
        ImageView iv_twitter = dialog.findViewById(R.id.ic_twitter);
        ImageView iv_share = dialog.findViewById(R.id.ic_share);
        PlayerView videoPlayer = dialog.findViewById(R.id.videoPlayer);
        ImageView ivPlayVideo = dialog.findViewById(R.id.iv_play_video);

        GlideBinding.setTextSize(title, "font_title_size");

        iv_preview.setVisibility(VISIBLE);

        Glide.with(this)
                .load(Constant.bitmap)
                .into(iv_preview);

        if (iv_close_name.getVisibility() != GONE || iv_close_phone.getVisibility() != GONE) {
            iv_close_name.setVisibility(GONE);
            iv_close_phone.setVisibility(GONE);
        }
        assert iv_cancel != null;
        iv_cancel.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            dialog.dismiss();
        });
        iv_download.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("download").execute();
            dialog.dismiss();
        });
        iv_whatsapp.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("whtsapp").execute();
            dialog.dismiss();
        });
        iv_facebook.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("fb").execute();
            dialog.dismiss();
        });
        iv_instagram.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("insta").execute();
            dialog.dismiss();
        });
        iv_twitter.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("twter").execute();
            dialog.dismiss();
        });
        iv_share.setOnClickListener(v -> {
            if (sharePlayer != null) {
                sharePlayer.setPlayWhenReady(false);
                sharePlayer.stop();
                sharePlayer.seekTo(0);
                ivPlayVideo.setVisibility(VISIBLE);
            }
            new LoadDownloadImage("Share Via").execute();
            dialog.dismiss();
        });
        dialog.show();
    }

    class LoadDownloadImage extends AsyncTask<String, Boolean, Boolean> {
        String type = "";
        String filePath;
        boolean checkMemory;

        LoadDownloadImage(String type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (isVideo) {
                fileName = getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".mp4";
                filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name) + File.separator + fileName;

                File sourceLocation = new File(VideoPath);

                boolean success = false;

                if (!new File(filePath).exists()) {
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name));
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                Util.showLog("Can't create directory to save image.");
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.don_t_create), Toast.LENGTH_LONG).show();
                                success = false;
                            }
                        }

                        if (sourceLocation.exists()) {

                            InputStream in = new FileInputStream(sourceLocation);
                            OutputStream out = new FileOutputStream(filePath);

                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;

                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            in.close();
                            out.close();
                            success = true;

                        } else {
                            Util.showLog("Copy file failed. Source file missing.");
                        }

                    } catch (Exception e) {

                    }
                }
                return success;
            } else {
                filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name) + File.separator + fileName;

                boolean success = false;

                if (!new File(filePath).exists()) {
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + getResources().getString(R.string.app_name));
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                Util.showLog("Can't create directory to save image.");
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.don_t_create), Toast.LENGTH_LONG).show();
                                success = false;
                            }
                        }
                        File file2 = new File(file.getAbsolutePath() + "/" + fileName);
                        if (file2.exists()) {
                            file2.delete();
                        }
                        Bitmap bitmap = Constant.bitmap;
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            canvas.drawColor(-1);
                            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                            checkMemory = createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            createBitmap.recycle();
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            MediaScannerConnection.scanFile(EditorActivity.this, new String[]{file2.getAbsolutePath()}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String str, Uri uri) {
                                    Util.showLog("ExternalStorage " + "Scanned " + str + ":");
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("-> uri=");
                                    sb.append(uri);
                                    sb.append("-> FILE=");
                                    sb.append(file2.getAbsolutePath());
                                    Uri muri = Uri.fromFile(file2);
                                }
                            });
                            success = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            success = false;
                        }

                        progressDialog.dismiss();
                    } catch (Exception unused2) {
                    }
                }
                return success;
            }
        }

        @Override
        protected void onPostExecute(Boolean s) {
//                progressDialog.dismiss();
            if (s) {
                if (type.equals("download")) {
                    if (isVideo) {
                        Util.showToast(EditorActivity.this, getString(R.string.video_saved));
                    } else {
                        Toast.makeText(EditorActivity.this, getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    shareFileImageUri(getImageContentUri(new File(filePath)), "", type, isVideo);
                }
            } else {
                Toast.makeText(EditorActivity.this, getString(R.string.err_save_image), Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public void shareFileImageUri(Uri path, String name, String shareTo, boolean isVideo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        switch (shareTo) {
            case "whtsapp":
                shareIntent.setPackage("com.whatsapp");
                break;
            case "fb":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "insta":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "twter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }

        if (isVideo) {
            shareIntent.setDataAndType(path, "video/*");
        } else {
            shareIntent.setDataAndType(path, "image/*");
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        if (!name.equals("")) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, name);
        }
        startActivity(Intent.createChooser(shareIntent, MyApplication.context.getString(R.string.share_via)));
    }


    public Uri getImageContentUri(File imageFile) {
        if (isVideo) {
            return Uri.parse(imageFile.getAbsolutePath());
        } else {
            return Uri.parse(imageFile.getAbsolutePath());
            /*String filePath = imageFile.getAbsolutePath();
            Cursor cursor = this.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                cursor.close();
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
            }*/
        }
//        return null;
    }

    private Bitmap viewToBitmap(View view) {
        Bitmap createBitmap = null;
        try {
            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } catch (Exception e) {
            Util.showErrorLog(e.getMessage(), e);
            return createBitmap;
        } finally {
            view.destroyDrawingCache();
        }
    }

    private void loadSticker() {
        StickerAdapter stickerAdapter = new StickerAdapter(this, Config.stickers, stickerName -> {
            AssetManager assetManager = getAssets();
            InputStream istr;
            Bitmap bitmap = null;
            try {
                istr = assetManager.open("stickers" + "/" + stickerName + ".png");
                bitmap = BitmapFactory.decodeStream(istr);
                addSticker("", "", bitmap, false);
            } catch (IOException e) {
                // handle exception
            }
            binding.rlSticker.setVisibility(GONE);
        });
        binding.rvSticker.setAdapter(stickerAdapter);
    }

    private void addImage() {
//        checkPer();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_image_dialog);
        dialog.setCancelable(false);
        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView fitEditText = (TextView) dialog.findViewById(R.id.auto_fit_edit_text);
        Button btnGallery = (Button) dialog.findViewById(R.id.btnGallery);
        Button btnCamera = (Button) dialog.findViewById(R.id.btnCamera);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        GlideBinding.setTextSize(textView, "font_title_size");
        GlideBinding.setTextSize(fitEditText, "font_body_size");

        btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            Dexter.withContext(this).withPermissions(PERMISSIONS).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        onCameraButtonClick();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(EditorActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        });

        btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            Dexter.withContext(this).withPermissions(PERMISSIONS).withListener(new MultiplePermissionsListener() {
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        onGalleryButtonClick();
                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }

                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).withErrorListener(new PermissionRequestErrorListener() {
                public void onError(DexterError dexterError) {
                    Toast.makeText(EditorActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            }).onSameThread().check();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    public void removeControl() {

        int childCount = this.binding.flSticker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.binding.flSticker.getChildAt(i);
            if (childAt instanceof RelStickerView) {
                ((RelStickerView) childAt).setBorderVisibility(false);
            }
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                String str = null;
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

    public void onGalleryButtonClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image).toString()), SELECT_PICTURE_GALLERY);
    }

    public void onCameraButtonClick() {
        this.uri = FileProvider.getUriForFile(getApplicationContext(), "com.readymadedata.app.provider", createCameraFile());
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.uri);
        startActivityForResult(intent, SELECT_PICTURE_CAMERA);
    }

    private File createCameraFile() {
        try {
            return File.createTempFile("IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = data;
        super.onActivityResult(requestCode, resultCode, data);
        int i3 = requestCode;
        int i4 = resultCode;
        if (i4 != -1) {
            return;
        }
        if (intent != null || i3 == SELECT_PICTURE_CAMERA) {
            if (i3 == SELECT_PICTURE_GALLERY) {
                try {
                    Uri fromFile = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                    Log.e("downaload", "====" + data.getData().getPath());
                    Log.e("downaload", new StringBuilder().append("====").append(fromFile.getPath()).toString());
                    UCrop.Options options2 = new UCrop.Options();
                    options2.setToolbarColor(getResources().getColor(R.color.white));
                    options2.setFreeStyleCropEnabled(true);
                    UCrop.of(data.getData(), fromFile).withOptions(options2).start(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (i3 == SELECT_PICTURE_CAMERA) {
                try {
                    Uri fromFile2 = Uri.fromFile(new File(MyApplication.context.getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                    Log.e("downaload", "====" + fromFile2.getPath());
                    UCrop.Options options3 = new UCrop.Options();
                    options3.setToolbarColor(getResources().getColor(R.color.white));
                    options3.setFreeStyleCropEnabled(true);
                    UCrop.of(this.uri, fromFile2).withOptions(options3).start(this);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (i4 == -1 && i3 == 69) {
                handleCropResult(intent);
            } else if (i4 == 96) {
                UCrop.getError(data);
            }
        }
    }

    private void handleCropResult(Intent data) {
        addSticker("", "", BitmapUtil.decodeBitmapFromUri(this, UCrop.getOutput(data), 456, 456, BitmapUtil.getInSampleSizePower2()), this.isImage);
    }

    public void addSticker(String str, String str2, Bitmap bitmap2, boolean isImage2) {
        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setPOS_X((float) ((this.binding.cvBase.getWidth() / 2) - ImageUtils.dpToPx(MyApplication.context, 70)));
        elementInfo.setPOS_Y((float) ((this.binding.cvBase.getHeight() / 2) - ImageUtils.dpToPx(MyApplication.context, 70)));
        elementInfo.setWIDTH(ImageUtils.dpToPx(MyApplication.context, 140));
        elementInfo.setHEIGHT(ImageUtils.dpToPx(MyApplication.context, 140));
        elementInfo.setROTATION(0.0f);
        elementInfo.setRES_ID(str);
        elementInfo.setBITMAP(bitmap2);
        elementInfo.setCOLORTYPE("colored");
        elementInfo.setTYPE("STICKER");
        elementInfo.setSTC_OPACITY(255);
        elementInfo.setSTC_COLOR(0);
        elementInfo.setSTKR_PATH(str2);
        elementInfo.setSTC_HUE(1);
        elementInfo.setFIELD_TWO("0,0");
        RelStickerView relStickerView = new RelStickerView(MyApplication.context, isImage2);
        relStickerView.optimizeScreen(this.sWidth, this.sHeight);
        relStickerView.setMainLayoutWH((float) this.binding.cvBase.getWidth(), (float) this.binding.cvBase.getHeight());
        relStickerView.setComponentInfo(elementInfo);
        relStickerView.setId(ViewIdGenerator.generateViewId());
        this.binding.flSticker.addView(relStickerView);
        relStickerView.setOnTouchCallbackListener(this.rtouchlistener);
        relStickerView.setBorderVisibility(true);
    }

    public void setBackImage() {
        int childCount = this.binding.flSticker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.binding.flSticker.getChildAt(i);
            if ((childAt instanceof RelStickerView) && ((RelStickerView) childAt).getBorderVisbilty() && ((RelStickerView) childAt).getIsImage()) {
                this.binding.flSticker.removeView(childAt);
                this.binding.flBackSticker.addView(childAt);
                ((RelStickerView) childAt).setOnTouchCallbackListener(this.newtouchlistener);
                ((RelStickerView) childAt).setBorderVisibility(false);
            }
        }
    }

    public void touchDown(View view, String str) {
        if (view instanceof RelStickerView) {
            RelStickerView relStickerView = (RelStickerView) view;
        }
    }

    public void touchMove(View view) {
        if (view instanceof RelStickerView) {
            RelStickerView relStickerView = (RelStickerView) view;
        }
    }

    public void touchUp(View view) {
        if (view instanceof RelStickerView) {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            RelStickerView relStickerView = (RelStickerView) view;
            sb.append(relStickerView.getColorType());
            relStickerView.setBorderVisibility(true);
            if (this.isImage && view.getParent() == this.binding.flBackSticker) {
                this.binding.flBackSticker.removeView(relStickerView);
                this.binding.flSticker.addView(relStickerView);
                relStickerView.setOnTouchCallbackListener(this.rtouchlistener);
            }
        }
    }


    class LoadLogo extends AsyncTask<String, String, String> {
        Bitmap bitmap;
        Drawable drawable;
        private String message = "";
        private String urls;
        private String verifyStatus = Config.ZERO;

        public LoadLogo(String urls2) {
            this.urls = urls2;
        }

        public String doInBackground(String... strings) {
            this.bitmap = null;
            Log.e("URL", this.urls);
            try {
                this.bitmap = BitmapFactory.decodeStream(new URL(this.urls).openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.drawable = new BitmapDrawable(Resources.getSystem(), this.bitmap);
            return Config.ZERO;
        }

        public void onPostExecute(String s) {
            super.onPostExecute(s);
            addSticker("", "", ((BitmapDrawable) this.drawable).getBitmap(), false);
        }

    }

    private void showFont() {
        FontAdapter fontAdapter = new FontAdapter(this, fontList(), fontName -> {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/" + fontName);
            if (!isFromUrl) {
                if (type.equals("business")) {
                    if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_close_website.getVisibility() == View.GONE && iv_close_email.getVisibility() == View.GONE && iv_close_address.getVisibility() == View.GONE && iv_close_phone.getVisibility() == View.GONE) {
                        tv_name.setTypeface(typeface);
                        tv_email.setTypeface(typeface);
                        tv_phone.setTypeface(typeface);
                        tv_website.setTypeface(typeface);
                        tv_address.setTypeface(typeface);

                        for (int i = 0; i < addTextList.size(); i++) {
                            addTextList.get(i).tv_text.setTypeface(typeface);
                        }
                    } else {
                        if (iv_close_name.getVisibility() == View.VISIBLE) {
                            tv_name.setTypeface(typeface);
                        }
                        if (iv_close_phone.getVisibility() == View.VISIBLE) {
                            tv_phone.setTypeface(typeface);
                        }
                        if (iv_close_email.getVisibility() == View.VISIBLE) {
                            tv_email.setTypeface(typeface);
                        }
                        if (iv_close_address.getVisibility() == View.VISIBLE) {
                            tv_address.setTypeface(typeface);
                        }
                        if (iv_close_website.getVisibility() == View.VISIBLE) {
                            tv_website.setTypeface(typeface);
                        }
                        if (iv_close_website.getVisibility() == View.VISIBLE) {
                            tv_website.setTypeface(typeface);
                        }
                        if (isTextSelected() && addTextList.size() > 0) {
                            addTextList.get(selectedTextPosition).tv_text.setTypeface(typeface);
                        }
                    }

                } else {
                    if (isTextSelected() && addTextList.size() > 0) {
                        addTextList.get(selectedTextPosition).tv_text.setTypeface(typeface);
                    }
                }


                if (type.equals("personal")) {
                    if (!isTextSelected() &&
                            iv_close_name.getVisibility() == View.GONE &&
                            iv_close_address.getVisibility() == View.GONE &&
                            iv_fb_close.getVisibility() == View.GONE &&
                            iv_close_phone.getVisibility() == GONE && iv_insta_close.getVisibility() == GONE
                    ) {
                        tv_personal_name.setTypeface(typeface);
                        tv_persnol_num.setTypeface(typeface);
                        tv_insta_personal.setTypeface(typeface);
                        tv_facebook_personal.setTypeface(typeface);
                        tv_personal_address.setTypeface(typeface);

                        for (int i = 0; i < addTextList.size(); i++) {
                            addTextList.get(i).tv_text.setTypeface(typeface);
                        }
                    } else {

                        if (iv_close_name.getVisibility() == View.VISIBLE) {
                            tv_personal_name.setTypeface(typeface);
                        }
                        if (iv_close_phone.getVisibility() == View.VISIBLE) {
                            tv_persnol_num.setTypeface(typeface);
                        }
                        if (iv_close_address.getVisibility() == View.VISIBLE) {
                            tv_personal_address.setTypeface(typeface);
                        }
                        if (iv_fb_close.getVisibility() == View.VISIBLE) {
                            tv_facebook_personal.setTypeface(typeface);
                        }
                        if (iv_insta_close.getVisibility() == View.VISIBLE) {
                            tv_insta_personal.setTypeface(typeface);
                        }
                        if (isTextSelected() && addTextList.size() > 0) {
                            addTextList.get(selectedTextPosition).tv_text.setTypeface(typeface);
                        }
                    }
                }


                if (type.equals("political")) {
                    if (!isTextSelected() && iv_close_name.getVisibility() == View.GONE && iv_designation_close.getVisibility() == View.GONE) {
                        tv_name_pol.setTypeface(typeface);
                        tv_mob_poli.setTypeface(typeface);
                        tv_designation_poli.setTypeface(typeface);
                        tv_insta_poli.setTypeface(typeface);
                        tv_face_poli.setTypeface(typeface);

                        for (int i = 0; i < addTextList.size(); i++) {
                            addTextList.get(i).tv_text.setTypeface(typeface);
                        }
                    } else {
                        if (iv_close_name.getVisibility() == View.VISIBLE) {
                            tv_name_pol.setTypeface(typeface);
                        }
                        if (iv_close_phone.getVisibility() == View.VISIBLE) {
                            tv_mob_poli.setTypeface(typeface);
                        }
                        if (iv_designation_close.getVisibility() == View.VISIBLE) {
                            tv_designation_poli.setTypeface(typeface);
                        }
                        if (iv_fb_close.getVisibility() == VISIBLE) {
                            tv_face_poli.setTypeface(typeface);
                        }

                        if (iv_insta_close.getVisibility() == VISIBLE) {
                            tv_insta_poli.setTypeface(typeface);
                        }


                    }

                } else {
                    if (isTextSelected() && addTextList.size() > 0) {
                        addTextList.get(selectedTextPosition).tv_text.setTypeface(typeface);
                    }
                }


            }


        });
        binding.rvFont.setAdapter(fontAdapter);
    }

    private List<String> fontList() {
        String[] list;
        List<String> fonts_array = new ArrayList<>();
        list = getResources().getStringArray(R.array.fonts);
        if (list != null && list.length > 0) {
            // This is a folder
            fonts_array.clear();
            for (String file : list) {
                if (file.endsWith(".ttf") || file.endsWith("otf")) {
                    fonts_array.add(file);
                }
            }
        }
        return fonts_array;
    }

    public void addText(final int pos2) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_text_dialog);
        dialog.setCancelable(false);
        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
        final EditText fitEditText = (EditText) dialog.findViewById(R.id.auto_fit_edit_text);
        Button button = (Button) dialog.findViewById(R.id.btnCancelDialog);
        Button button2 = (Button) dialog.findViewById(R.id.btnAddTextSDialog);
        GlideBinding.setTextSize(textView, "font_title_size");
        GlideBinding.setTextSize(fitEditText, "edit_text");
        if (pos2 != -1) {
            fitEditText.setText(this.addTextList.get(this.selectedTextPosition).tv_text.getText().toString());
            textView.setText(getString(R.string.edit_text));
            button2.setText(getString(R.string.save));
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!fitEditText.getText().toString().trim().equals("")) {
                    if (pos2 != -1) {
                        addTextList.get(selectedTextPosition).tv_text.setText(fitEditText.getText().toString());
                    } else {
                        addTextView(fitEditText.getText().toString());
                    }
                    dialog.dismiss();
                    return;
                }
                EditorActivity editorActivity = EditorActivity.this;
                Toast.makeText(editorActivity, editorActivity.getString(R.string.add_text), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void addTextView(String text) {
        View view_text = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.add_text, (ViewGroup) null);
        TextView tv_new = (TextView) view_text.findViewById(R.id.tv_add_text);
        ImageView iv_remove = (ImageView) view_text.findViewById(R.id.iv_add_text_remove);
        ImageView iv_edit = (ImageView) view_text.findViewById(R.id.iv_add_text_edit);
        tv_new.setTag(String.valueOf(this.addTextList.size()));
        tv_new.setTextSize(2, 10.0f);
        tv_new.setText(text);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(-2, -2);
        params.setMargins(5, 5, 5, 5);
        view_text.setLayoutParams(params);
        this.binding.flFrame.addView(view_text);
        view_text.bringToFront();
        view_text.setTranslationX((float) (this.binding.flFrame.getWidth() / 2));
        view_text.setTranslationY((float) (this.binding.flFrame.getHeight() / 2));
        final TextView textView = tv_new;
        final View view = view_text;
        final ImageView imageView = iv_remove;
        final ImageView imageView2 = iv_edit;
        view_text.setOnTouchListener(new OnDragTouchListener(view_text, this.binding.flFrame, 50, new OnDragTouchListener.OnDragActionListener() {
            public void onDragStart(View view) {
                if (selectedTextPosition != Integer.parseInt(textView.getTag().toString()) && isTextSelected()) {
                    EditorActivity editorActivity = EditorActivity.this;
                    editorActivity.setTextSelected(editorActivity.addTextList.get(selectedTextPosition).tv_text, addTextList.get(selectedTextPosition).iv_close, addTextList.get(selectedTextPosition).iv_edit, false);
                }
                view.bringToFront();
                setTextSelected(textView, imageView, imageView2, true);
            }

            public void onDragEnd(View view, Boolean delete) {
            }
        }));
        tv_new.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        view_text.bringToFront();
        final TextView textView2 = tv_new;
        final ImageView imageView3 = iv_remove;
        final ImageView imageView4 = iv_edit;
        final View view2 = view_text;
        view_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedTextPosition != Integer.parseInt(textView2.getTag().toString())) {
                    if (isTextSelected()) {
                        addTextList.get(selectedTextPosition).tv_text.setBackground((Drawable) null);
                    }
                    setTextSelected(textView2, imageView3, imageView4, true);
                    view2.bringToFront();
                } else if (isTextSelected()) {
                    setTextSelected(textView2, imageView3, imageView4, false);
                }
            }
        });
        view_text.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                Log.e("aaa", "long click");
                return true;
            }
        });
        iv_remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    addTextList.get(selectedTextPosition).viewLayout.setOnTouchListener((View.OnTouchListener) null);
                    binding.flFrame.removeView(addTextList.get(selectedTextPosition).viewLayout);
                    addTextList.remove(selectedTextPosition);
                    if (selectedTextPosition < addTextList.size()) {
                        for (int i = selectedTextPosition; i < addTextList.size(); i++) {
                            addTextList.get(i).tv_text.setTag(String.valueOf(i));
                        }
                        return;
                    }
                    selectedTextPosition = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    EditorActivity editorActivity = EditorActivity.this;
                    editorActivity.addText(editorActivity.selectedTextPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.addTextList.add(new AddTextItem(view_text, tv_new, iv_remove, iv_edit));
    }

    public void setTextSelected(TextView tv_new, ImageView iv_remove, ImageView iv_edit, boolean isSelected) {
        if (isSelected) {
            selectedTextPosition = Integer.parseInt(tv_new.getTag().toString());
            tv_new.setBackgroundResource(R.drawable.rounded_border);
            iv_remove.setVisibility(View.VISIBLE);
            iv_edit.setVisibility(View.VISIBLE);
            return;
        }
        tv_new.setBackground((Drawable) null);
        iv_remove.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        this.selectedTextPosition = -1;
    }

    public void changeFontSize(float value) {

        if (type.equals("business")) {
            if (!this.isFromUrl) {
                if (iv_close_name.getVisibility() == View.VISIBLE) {
                    tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                    tv_phone.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_website.getVisibility() == View.VISIBLE) {
                    tv_website.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_address.getVisibility() == View.VISIBLE) {
                    tv_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_email.getVisibility() == View.VISIBLE) {
                    tv_email.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (isTextSelected() && this.addTextList.size() > 0) {
                    addTextList.get(this.selectedTextPosition).tv_text.setTextSize(0, value);
                }
            } else if (isTextSelected() && this.addTextList.size() > 0) {
                addTextList.get(this.selectedTextPosition).tv_text.setTextSize(0, value);
            }
        }

        if (type.equals("personal")) {
            if (!this.isFromUrl) {
                if (iv_close_name.getVisibility() == View.VISIBLE) {
                    tv_personal_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }

                if (iv_insta_close.getVisibility() == View.VISIBLE) {
                    tv_insta_personal.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }

                if (iv_fb_close.getVisibility() == View.VISIBLE) {
                    tv_facebook_personal.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                    tv_persnol_num.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }

                if (iv_close_address.getVisibility() == View.VISIBLE) {
                    tv_personal_address.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
            }
            if (isTextSelected() && this.addTextList.size() > 0) {
                addTextList.get(this.selectedTextPosition).tv_text.setTextSize(0, value);
            }
        }

        if (type.equals("political")) {
            if (!this.isFromUrl) {
                if (iv_close_name.getVisibility() == View.VISIBLE) {
                    tv_name_pol.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_close_phone.getVisibility() == View.VISIBLE) {
                    tv_mob_poli.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_designation_close.getVisibility() == View.VISIBLE) {
                    tv_designation_poli.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_fb_close.getVisibility() == View.VISIBLE) {
                    tv_face_poli.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
                if (iv_insta_close.getVisibility() == View.VISIBLE) {
                    tv_insta_poli.setTextSize(TypedValue.COMPLEX_UNIT_SP, value);
                }
            }
            if (isTextSelected() && this.addTextList.size() > 0) {
                addTextList.get(this.selectedTextPosition).tv_text.setTextSize(0, value);
            }
        }
    }

    public boolean isTextSelected() {
        return selectedTextPosition >= 0;
    }

    private void setPostData() {
        if (getIntent() != null) {
            postItemList = (List) getIntent().getSerializableExtra(Constant.INTENT_POST_LIST);
            isImage = true;
            pos = getIntent().getIntExtra(Constant.INTENT_POS, 0);
        }
    }


    private void setImageShow() {
        this.binding.ivFestImage.setVisibility(View.VISIBLE);
        this.binding.videoPlayer.setVisibility(View.GONE);
        GlideBinding.bindImage(binding.ivFestImage, imgUrl);
    }


}
