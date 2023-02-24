package com.readymadedata.app.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.readymadedata.app.Config;
import com.readymadedata.app.R;
import com.readymadedata.app.api.ApiClient;
import com.readymadedata.app.items.User;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.BusinessViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.farhanfarooqui.pinview.PinView;

public class VerifyOtpActivity extends AppCompatActivity {

    AppCompatButton btnVerifyOtp;
    private String verificationId;
    private FirebaseAuth mAuth;
    private PinView pinView;
    UserViewModel userViewModel;
    BusinessViewModel businessViewModel;
    PrefManager prefManager;
    ProgressDialog prgDialog;
    String phone;
    TextView tvResendOtpBtn;

    TextView tvOtpTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
          prefManager = new PrefManager(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        businessViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);

        pinView = findViewById(R.id.pinView);

        mAuth = FirebaseAuth.getInstance();

        phone = "+91" + getIntent().getStringExtra("mobile_num");
        sendVerificationCode(phone);
        prgDialog = new ProgressDialog(this);

        tvResendOtpBtn = findViewById(R.id.tvResendOtpBtn);
        tvOtpTime = findViewById(R.id.tv_otp_time);
        String str = "+91" + getIntent().getStringExtra("mobile_num");

        prgDialog.setMessage(getResources().getString(R.string.login_loading));
        prgDialog.setCancelable(false);
        btnVerifyOtp = findViewById(R.id.buttonVerify);



        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(pinView.getPin());
            }
        });




        tvResendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startTimer();
                Toast.makeText(VerifyOtpActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            }
        });

        new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOtpBtn.setAlpha(0.4f);
                tvResendOtpBtn.setClickable(false);
                tvOtpTime.setText("Wait "+millisUntilFinished / 1000+" Seconds");
                // logic to set the EditText could go here
            }

            public void onFinish() {
                tvOtpTime.setText("00:00");
                tvResendOtpBtn.setAlpha(1f);
                tvResendOtpBtn.setClickable(true);
            }
        }.start();
    }

    private void startTimer() {
        phone = "+91" + getIntent().getStringExtra("mobile_num");
        sendVerificationCode(phone);


        new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvOtpTime.setText("Wait "+ millisUntilFinished / 1000+" Seconds");

                tvResendOtpBtn.setAlpha(0.4f);
                tvResendOtpBtn.setClickable(false);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                tvOtpTime.setText("00:00");
                tvResendOtpBtn.setAlpha(1f);
                tvResendOtpBtn.setClickable(true);


            }

        }.start();
    }


    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            addUserNumber();
                            loadBusiness();

                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(VerifyOtpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinView.setPin(code);
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(VerifyOtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.

    private void loadBusiness() {

        prgDialog.show();

        businessViewModel.setBusinessObj(prefManager.getString(Constant.USER_ID));

        businessViewModel.getBusiness().observe(this, resource -> {
            if (resource != null) {

                Util.showLog("Got Data" + resource.message + resource.toString());

                switch (resource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server
                        prgDialog.dismiss();
                        gotoMainActivity();
                        break;
                    case ERROR:
                        // Error State
                        gotoMainActivity();
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
    }

    public void verifyCode(String code) {
        signInWithCredential(PhoneAuthProvider.getCredential(this.verificationId, code));
    }

    /* access modifiers changed from: private */
    public void addUserNumber() {
        ApiClient.getApiService().postAddUser(phone).enqueue(new Callback<User>() {



            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                if (response.body() != null) {
                    Log.e("ResponseBody--->", String.valueOf(response.body().data.id));
                    VerifyOtpActivity.this.prefManager.setBoolean(Constant.IS_LOGIN, true);
                    VerifyOtpActivity.this.prefManager.setString(Constant.USER_ID, response.body().data.id.toString());
                    VerifyOtpActivity.this.prefManager.setString(Constant.PRIMARY_CATEGORY, Config.ZERO);
                    VerifyOtpActivity.this.prefManager.setString(Constant.LOGIN_TYPE, Constant.NORMAL);
                    VerifyOtpActivity.this.gotoMainActivity();

                    return;
                }
                throw new AssertionError();
            }

            public void onFailure(Call<User> call, Throwable t) {
                Log.e("ResponseError--->", t.getMessage().toString());
            }
        });
    }

    private void gotoMainActivity() {
        userViewModel.setLoadingState(false);
        prgDialog.cancel();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}