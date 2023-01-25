package com.readymadedata.app.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.arthenica.mobileffmpeg.Config;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.readymadedata.app.R;
import com.readymadedata.app.binding.GlideBinding;
import com.readymadedata.app.ui.activities.AddBusinessActivity;
import com.readymadedata.app.ui.activities.SplashyActivity;
import com.readymadedata.app.ui.activities.SubsPlanActivity;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;

public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    ImageButton btnEdit;
    MaterialTextView btnLogOut;
    MaterialTextView btnMyPlan;
    MaterialTextView btnMyPost;
    MaterialTextView btnPrivacyPolicy;
    MaterialTextView btnRateUs;
    MaterialTextView btnShareUs;
    MaterialTextView btnTermsConditions;
    MaterialButton btnUpgrade;
    EditText edFirstName;
    ImageView ivProfileView;
    EditText edProfileType;
    PrefManager prefManager;
    View f1v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        f1v = inflater.inflate(R.layout.fragment_profile, container, false);

        prefManager = new PrefManager(getContext());
        btnMyPost = f1v.findViewById(R.id.btn_myPost);
        ivProfileView = f1v.findViewById(R.id.user_profile);
        btnMyPlan = (MaterialTextView) f1v.findViewById(R.id.btn_myPlan);
        btnShareUs = (MaterialTextView) f1v.findViewById(R.id.btn_shareApp);
        btnRateUs = (MaterialTextView) f1v.findViewById(R.id.btn_rateApp);
        btnUpgrade = f1v.findViewById(R.id.btn_upgrade);
        btnPrivacyPolicy = (MaterialTextView) f1v.findViewById(R.id.btn_privacyPolicy);
        btnTermsConditions = (MaterialTextView) f1v.findViewById(R.id.btn_termsConditions);
        btnLogOut = (MaterialTextView) f1v.findViewById(R.id.btn_logOut);
        btnEdit = (ImageButton) f1v.findViewById(R.id.btnEdit);

        edFirstName = (EditText) f1v.findViewById(R.id.ed_first_name);
        edProfileType = f1v.findViewById(R.id.ed_profile_type);
        edFirstName.setText(prefManager.getString(Constant.USER_NAME));

        View view = getActivity().findViewById(R.id.toolbar);
        view.findViewById(R.id.ll_option).setVisibility(View.GONE);



        try {
            GlideBinding.bindImage(ivProfileView, prefManager.getString("p_url"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        btnMyPlan.setOnClickListener(e-> {
//              startActivity(new Intent(this.getActivity(),SubsPlanActivity.class));
                setupFragment(new PlanInfoFragment());
        });

        try {
            if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("0")) {
                edProfileType.setText("Business");
            }

            if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("1")) {
                edProfileType.setText("Personal");
            }

            if (prefManager.getString(Constant.PRIMARY_CATEGORY).equals("2")) {
                edProfileType.setText("Political");
            }
            if (prefManager.getString("p_name").equals("")) {
                edFirstName.setText("Editkro");

            } else {
                edFirstName.setText(prefManager.getString("p_name"));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
//      btnEdit.setOnClickListener(this::);
//        new MaterialTapTargetPrompt.Builder(getActivity())
//                .setTarget(btnUpgrade)
//                .setPrimaryText("Hii Codeplayon")
//                .setSecondaryText("Tap the envelope to start composing your first email")
//                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
//                {
//                    @Override
//                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
//                    {
//                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
//                        {
//                            new MaterialTapTargetPrompt.Builder(getActivity())
//                                    .setTarget(btnEdit)
//                                    .setPrimaryText("Hii Codeplayon")
//                                    .setSecondaryText("Tap the envelope to start composing your first email")
//                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
//                                    {
//                                        @Override
//                                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
//                                        {
//                                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
//                                            {
//                                                // User has pressed the prompt target
//                                            }
//                                        }
//                                    })
//                                    .show();
//                            // User has pressed the prompt target
//                        }
//                    }
//                })
//                .show();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddBusinessActivity.class));
            }
        });

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(f1v.getContext(), SubsPlanActivity.class));
            }
        });


//        btnShareUs.setOnClickListener(ProfileFragment$$ExternalSyntheticLambda1.INSTANCE);
        btnRateUs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProfileFragment.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + Config.getPackageName())));
            }
        });
        btnTermsConditions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(ProfileFragment.this.getActivity(), PrivacyActivity.class);
//                intent.putExtra("type", Constant.PRIVACY_POLICY);
//                ProfileFragment.this.startActivity(intent);
            }
        });
        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(ProfileFragment.this.getActivity(), PrivacyActivity.class);
//                intent.putExtra("type", Constant.PRIVACY_POLICY);
//                ProfileFragment.this.startActivity(intent);
            }
        });
        btnMyPost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setupFragment(new DownloadFragment());
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefManager.setBoolean(Constant.IS_LOGIN, false);
                prefManager.setString(Constant.USER_EMAIL, "");
                prefManager.setString("password", "");
                prefManager.setString(Constant.USER_ID, "");
                startActivity(new Intent(v.getContext(), SplashyActivity.class));
            }
        });
        return f1v;
    }

    public void setupFragment(Fragment fragment) {
        try {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            Util.showLog("Error! Can't replace fragment.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}