package com.readymadedata.app.ui.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.readymadedata.app.Config;
import com.readymadedata.app.R;
import com.readymadedata.app.api.ApiStatus;
import com.readymadedata.app.api.common.common.Resource;
import com.readymadedata.app.databinding.ActivityLoginBinding;
import com.readymadedata.app.databinding.VerifyDialogBinding;
import com.readymadedata.app.items.UserItem;
import com.readymadedata.app.ui.dialog.DialogMsg;
import com.readymadedata.app.utils.Connectivity;
import com.readymadedata.app.utils.Constant;
import com.readymadedata.app.utils.PrefManager;
import com.readymadedata.app.utils.Util;
import com.readymadedata.app.viewmodel.BusinessViewModel;
import com.readymadedata.app.viewmodel.UserViewModel;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    UserViewModel userViewModel;
    BusinessViewModel businessViewModel;
    boolean loginScene = true;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DialogMsg dialogMsg;
    String userEmail = "";
    ProgressDialog prgDialog;
    PrefManager prefManager;
    Connectivity connectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefManager = new PrefManager(this);
        dialogMsg = new DialogMsg(this, false);
        prgDialog = new ProgressDialog(this);
        connectivity = new Connectivity(this);
        prgDialog.setMessage(getResources().getString(R.string.login_loading));
        prgDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {

            }
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        businessViewModel = new ViewModelProvider(this).get(BusinessViewModel.class);

        setUpScene(loginScene);
        initUi();
        initData();

    }

    private void initData() {
        userViewModel.getLoadingState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loadingState) {

                if (loadingState != null && loadingState) {
                    prgDialog.show();
                } else {
                    prgDialog.cancel();
                }
                updateRegisterBtnStatus();
                updateLoginBtnStatus();

            }
        });

        userViewModel.getUserLoginStatus().observe(this, listResource -> {

            if (listResource != null) {

                Util.showLog("Got Data: " + listResource.message + listResource.toString());

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
                                Log.e("GOOD", listResource.data.toString());

                                if (listResource.data.user.is_email_verify) {
                                    prefManager.setBoolean(Constant.IS_LOGIN, true);
                                    prefManager.setString(Constant.USER_EMAIL, binding.etEmail.getText().toString().trim());
                                    prefManager.setString(Constant.USER_PASSWORD, binding.etPassword.getText().toString().trim());
                                    prefManager.setString(Constant.USER_ID, listResource.data.user_id);
                                    prefManager.setString(Constant.LOGIN_TYPE, Constant.NORMAL);

                                    loadBusiness();

                                } else {
                                    prgDialog.cancel();
                                    showVerifyDialog(listResource.data.user, true);
                                }

                            } catch (NullPointerException ne) {
                                Util.showErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Util.showErrorLog("Error in getting notification flag data.", e);
                            }

                        }

                        break;
                    case ERROR:
                        // Error State
                        prefManager.setBoolean(Constant.IS_LOGIN, false);
                        dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                        dialogMsg.show();

                        userViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        userViewModel.setLoadingState(false);

                        break;
                }

            } else {

                // Init Object or Empty Data
                Util.showLog("Empty Data");
                prefManager.setBoolean(Constant.IS_LOGIN, false);

            }

        });

        userViewModel.getRegisterUser().observe(this, listResource -> {
            if (listResource != null) {

                Util.showLog("Got Data "
                        + listResource.message +
                        listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        prgDialog.show();

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            userViewModel.setLoadingState(false);
                            prgDialog.cancel();
                            showVerifyDialog(listResource.data, false);
                        }

                        break;
                    case ERROR:
                        // Error State

                        Util.showLog("Error: " + listResource.message);

                        dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                        dialogMsg.show();
                        binding.btnSingUp.setText(getResources().getString(R.string.login_sign_up));

                        userViewModel.setLoadingState(false);
                        prgDialog.cancel();

                        break;
                    default:
                        // Default
                        userViewModel.isLoading = false;
                        prgDialog.cancel();
                        break;
                }

            } else {

                // Init Object or Empty Data
                Util.showLog("Empty Data");

            }
        });

        userViewModel.getGoogleLoginData().observe(this, listResource -> {

            if (listResource != null) {

                Util.showLog("Got Data " + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        prgDialog.show();

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {

                            try {
                                Log.e("GOOD", listResource.data.toString());

                                prefManager.setBoolean(Constant.IS_LOGIN, true);
                                prefManager.setString(Constant.USER_EMAIL, listResource.data.user.email);
                                prefManager.setString(Constant.USER_NAME, listResource.data.user.userName);
                                prefManager.setString(Constant.USER_IMAGE, listResource.data.user.userImage);
                                prefManager.setString(Constant.USER_ID, listResource.data.user_id);
                                prefManager.setString(Constant.LOGIN_TYPE, Constant.GOOGLE);
                                loadBusiness();

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

                        userViewModel.isLoading = false;
                        prgDialog.cancel();

                        dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                        dialogMsg.show();

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

        //For resent code
        userViewModel.getResentVerifyCodeData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:

                        //add offer text
                        Util.showToast(this, "Successfully Send Code");

                        break;

                    case ERROR:
                        Util.showToast(this, "Fail resent code again");
                        break;
                }
            }
        });

    }

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

    private void showVerifyDialog(UserItem userItem, boolean isLogin) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        VerifyDialogBinding binding = VerifyDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);
        binding.emailTextView.setText(userItem.email);

        binding.submitButton.setOnClickListener(v -> {
            if (!binding.enterCodeEditText.getText().toString().equals("")) {
                binding.submitButton.setEnabled(false);
                binding.submitButton.setText(getResources().getString(R.string.login_loading));
                userViewModel.setEmailVerificationUser(userItem.userId, binding.enterCodeEditText.getText().toString());
            } else {
                Util.showToast(this, getString(R.string.verify_email__enter_code));
            }
        });

        if (isLogin) {
            userViewModel.setResentVerifyCodeObj(userItem.userId);
        }

        binding.resentCodeButton.setOnClickListener(v -> userViewModel.setResentVerifyCodeObj(userItem.userId));

        binding.changeEmailButton.setOnClickListener(v -> {
            userViewModel.setLoadingState(false);
            dialog.dismiss();
        });

        dialog.show();

        LiveData<Resource<ApiStatus>> itemList = userViewModel.getEmailVerificationUser();

        if (itemList != null) {

            itemList.observe(this, listResource -> {
                if (listResource != null) {

                    binding.submitButton.setEnabled(true);
                    binding.submitButton.setText(getResources().getString(R.string.verify_email__submit));
                    switch (listResource.status) {
                        case LOADING:

                            break;

                        case SUCCESS:

                            if (listResource.data != null) {

                                try {
                                    if (isLogin) {

                                        dialogMsg.showSuccessDialog(getString(R.string.success_verify), getString(R.string.ok));
                                        dialogMsg.show();
                                        dialog.dismiss();
                                        dialogMsg.okBtn.setOnClickListener(v -> {
                                            prefManager.setBoolean(Constant.IS_LOGIN, true);
                                            prefManager.setString(Constant.USER_EMAIL, userItem.email);
                                            prefManager.setString(Constant.USER_PASSWORD, this.binding.etPassword.getText().toString().trim());
                                            prefManager.setString(Constant.USER_ID, userItem.userId);
                                            prefManager.setString(Constant.LOGIN_TYPE, Constant.NORMAL);
                                            gotoMainActivity();
                                        });

                                    } else {
                                        dialogMsg.showSuccessDialog(getString(R.string.success_register), getString(R.string.ok));
                                        dialogMsg.show();
                                        dialog.dismiss();
                                        dialogMsg.okBtn.setOnClickListener(v -> {
                                            prefManager.setBoolean(Constant.IS_LOGIN, true);
                                            prefManager.setString(Constant.USER_EMAIL, this.binding.etEmailSi.getText().toString().trim());
                                            prefManager.setString(Constant.USER_PASSWORD, this.binding.etPasswordSi.getText().toString().trim());
                                            prefManager.setString(Constant.USER_ID, userItem.userId);
                                            prefManager.setString(Constant.LOGIN_TYPE, Constant.NORMAL);

                                            dialogMsg.cancel();

                                            loadBusiness();
                                            userEmail = "";
                                            setEmptyText();
                                            setUpScene(true);
                                        });
                                    }

                                } catch (NullPointerException ne) {
                                    Util.showErrorLog("Null Pointer Exception.", ne);
                                } catch (Exception e) {
                                    Util.showErrorLog("Error in getting notification flag data.", e);
                                }

                            }

                            break;

                        case ERROR:
                            // Error State
                            dialogMsg.showErrorDialog(listResource.message, getString(R.string.ok));
                            dialogMsg.show();
//                            dialog.dismiss();

                            break;
                        default:
                            // Default

                            break;
                    }

                }

            });
        }
    }

    private void gotoMainActivity() {
        userViewModel.setLoadingState(false);
        prgDialog.cancel();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void initUi() {
        binding.txtTabLogin.setOnClickListener(v -> {
            if (!loginScene) {
                setUpScene(true);
            }
        });

        binding.txtTabSignUp.setOnClickListener(v -> {
            if (loginScene) {
                setUpScene(false);
            }
        });

        binding.txtSingBottom.setOnClickListener(v -> {
            if (loginScene) {
                setUpScene(false);
            }
        });

        binding.txtLoginBottom.setOnClickListener(v -> {
            if (!loginScene) {
                setUpScene(true);
            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (validate()) {
                if (!connectivity.isConnected()) {
                    Util.showToast(this, getString(R.string.error_message__no_internet));
                    return;
                }
                if (!userViewModel.isLoading) {

                    prgDialog.show();

                    userEmail = binding.etEmail.getText().toString().trim();

                    userViewModel.isLoading = false;
                    updateLoginBtnStatus();
                    Util.showLog("Sign in with email and password");
                    signInWithEmailAndPassword(userEmail, userEmail + "sdg");

                }
            }
        });

        binding.ivGoogle.setOnClickListener(v -> {
            if (!connectivity.isConnected()) {
                Util.showToast(this, getString(R.string.error_message__no_internet));
                return;
            }
            signIn();
        });

        binding.btnSingUp.setOnClickListener(v -> {
            if (registerValid()) {
                if (!connectivity.isConnected()) {
                    Util.showToast(this, getString(R.string.error_message__no_internet));
                    return;
                }
                userEmail = binding.etEmailSi.getText().toString().trim();
                userViewModel.isLoading = true;
                prgDialog.show();
                updateRegisterBtnStatus();

                // userEmail and userEmail is correct
                // This is needed for firebase login
                // no need to change to password
                createRegisterUserWithEmailAndPassword(userEmail, userEmail);
            }
        });
        binding.btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.txtForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPassActivity.class));
        });
    }

    private boolean registerValid() {
        if (binding.etEmailSi.getText().toString().trim().isEmpty()) {
            binding.etEmailSi.setError(getResources().getString(R.string.hint_business_name));
            binding.etEmailSi.requestFocus();
            return false;
        } else if (binding.etPasswordSi.getText().toString().trim().isEmpty()) {
            binding.etPasswordSi.setError(getResources().getString(R.string.hint_business_email));
            binding.etPasswordSi.requestFocus();
            return false;
        } else if (!isEmailValid(binding.etEmailSi.getText().toString())) {
            binding.etEmailSi.setError(getString(R.string.invalid_email));
            binding.etEmailSi.requestFocus();
            return false;
        } else if (binding.etPasswordSi.getText().toString().endsWith(" ")) {
            binding.etPasswordSi.setError(getResources().getString(R.string.space_not_allowed));
            binding.etPasswordSi.requestFocus();
            return false;
        } else if (binding.etConfPasswordSi.getText().toString().isEmpty()) {
            binding.etConfPasswordSi.setError(getResources().getString(R.string.enter_confirm_password));
            binding.etConfPasswordSi.requestFocus();
            return false;
        } else if (!binding.etPasswordSi.getText().toString().equals(binding.etConfPasswordSi.getText().toString())) {
            binding.etPasswordSi.setError(getResources().getString(R.string.pass_not_match));
            binding.etPasswordSi.requestFocus();
            return false;
        } else if (binding.etNumberSi.getText().toString().trim().isEmpty()) {
            binding.etNumberSi.setError(getResources().getString(R.string.hint_phone_number));
            binding.etNumberSi.requestFocus();
            return false;
        }
        return true;
    }

    private FirebaseUser createRegisterUserWithEmailAndPassword(String email, String password) {
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Util.showLog("createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                registerUser();

                            } else {
                                registerWithEmailAndPassword(email, password);
                            }
                        }
                    });
        } catch (Exception exception) {
            Util.showLog("***** Error Exception: " + exception);

            dialogMsg.showErrorDialog(getString(R.string.login__exception_error), getString(R.string.ok));
            dialogMsg.show();

        }
        return user;
    }

    private FirebaseUser registerWithEmailAndPassword(String email, String password) {
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Util.showLog("signInWithEmail:success");
                        user = mAuth.getCurrentUser();
                        registerUser();
                    } else {
                        Util.showLog("Fail");

                        if (!email.equals(Constant.DEFAULTEMAIL)) {

                            createRegisterUserWithEmailAndPassword(Constant.DEFAULTEMAIL, Constant.DEFAULTPASSWORD);
                        } else {
                            // Error
                            handleRegisterFirebaseAuthError(binding.etEmailSi.getText().toString().trim());
                        }

                    }

                }
            });
        } catch (Exception e) {
            Util.showLog("signInWithEmail:failure");
            dialogMsg.showErrorDialog(getString(R.string.login__failure_error), getString(R.string.ok));
            dialogMsg.show();

        }
//        FirebaseUser firebaseUser = user;
        return user;
    }

    private void registerUser() {
        userViewModel.isLoading = true;
        updateRegisterBtnStatus();
        userViewModel.setRegisterUserData(new UserItem("",
                binding.etNameSi.getText().toString().trim(),
                "",
                binding.etEmailSi.getText().toString().trim(),
                binding.etPasswordSi.getText().toString().trim(),
                binding.etNumberSi.getText().toString().trim(),
                "",
                false,
                "",
                "",
                "",
                "",
                false,
                1));
    }

    private void setEmptyText() {
        binding.etNameSi.setText(Config.EMPTY_STRING);
        binding.etPasswordSi.setText(Config.EMPTY_STRING);
        binding.etConfPasswordSi.setText(Config.EMPTY_STRING);
        binding.etNumberSi.setText(Config.EMPTY_STRING);
        binding.etEmailSi.setText(Config.EMPTY_STRING);

        binding.etEmail.setText(Config.EMPTY_STRING);
        binding.etPassword.setText(Config.EMPTY_STRING);
    }

    private void handleRegisterFirebaseAuthError(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethod = result.getSignInMethods();

                    Util.showLog("SignInMethod  =" + signInMethod);
                    if (signInMethod.contains(Constant.EMAILAUTH)) {
                        dialogMsg.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_email), getString(R.string.ok));
                        dialogMsg.show();
                    } else if (signInMethod.contains(Constant.GOOGLEAUTH)) {
                        dialogMsg.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_google), getString(R.string.ok));
                        dialogMsg.show();
                    }
                }
            }
        });
    }

    private void updateRegisterBtnStatus() {
        if (userViewModel.isLoading) {
            binding.btnSingUp.setEnabled(false);
            binding.btnSingUp.setText(getResources().getString(R.string.login_loading));
        } else {
            binding.btnSingUp.setEnabled(true);
            binding.btnSingUp.setText(getResources().getString(R.string.login_sign_up));
        }
    }

    private FirebaseUser signInWithEmailAndPassword(String email, String password) {
        Util.showLog(email + " " + password);
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        try {
                            //Success
                            Util.showLog("signInWithEmail:success" + email);
                            user = mAuth.getCurrentUser();
                            Util.showLog("doSubmit Sign");
                            doSubmit(userEmail, binding.etPassword.getText().toString());
                        } catch (Exception e) {
                            Util.showLog("" + e);
                        }
                    } else {
                        // Fail
                        Util.showLog("Fail");
                        if (!email.equals(Constant.DEFAULTEMAIL)) {
                            createUserWithEmailAndPassword(email, email);
                        } else {
                            userViewModel.isLoading = false;
                            updateLoginBtnStatus();
                            // Error Handling
                            Util.showLog("handleFirebaseAuthError");
                            handleFirebaseAuthError(binding.etEmail.getText().toString().trim());
                        }

                    }

                }

            });
        } catch (Exception e) {
            Util.showLog("signInWithEmail:failure");
            dialogMsg.showErrorDialog(getString(R.string.login__failure_error), getString(R.string.ok));
            dialogMsg.show();
        }

        return user;
    }

    public FirebaseUser createUserWithEmailAndPassword(String email, String password) {
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Util.showLog("createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                Util.showLog("doSubmit Create");
                                doSubmit(email, binding.etPassword.getText().toString());
                            } else {
                                //fail
                                if (!email.equals(Constant.DEFAULTEMAIL)) {
                                    createUserWithEmailAndPassword(Constant.DEFAULTEMAIL, Constant.DEFAULTPASSWORD);
                                } else {
                                    //fail
                                    signInWithEmailAndPassword(Constant.DEFAULTEMAIL, Constant.DEFAULTPASSWORD);
                                }
                            }
                        }

                    });
        } catch (Exception exception) {

            //If sign in fails, display a message to the user.
            Util.showLog("createUserWithEmail:failure");

            dialogMsg.showErrorDialog(getString(R.string.login__exception_error), getString(R.string.ok));
            dialogMsg.show();

        }

        return user;
    }

    private void doSubmit(String email, String password) {
        userViewModel.setUserLogin(new UserItem(
                "",
                "",
                "",
                email,
                password,
                "",
                "",
                false,
                "",
                "",
                "",
                "",
                true,
                1
        ));
        userViewModel.isLoading = true;
    }

    private boolean validate() {
        if (binding.etEmail.getText().toString().trim().isEmpty()) {
            binding.etEmail.setError(getResources().getString(R.string.hint_business_name));
            binding.etEmail.requestFocus();
            return false;
        } else if (binding.etPassword.getText().toString().trim().isEmpty()) {
            binding.etPassword.setError(getResources().getString(R.string.hint_business_email));
            binding.etPassword.requestFocus();
            return false;
        } else if (!isEmailValid(binding.etEmail.getText().toString())) {
            binding.etEmail.setError(getString(R.string.invalid_email));
            binding.etEmail.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && !email.contains(" ");
    }

    private void updateLoginBtnStatus() {
        if (userViewModel.isLoading) {
            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setText(getResources().getString(R.string.login_loading));
        } else {
            binding.btnLogin.setEnabled(true);
            binding.btnLogin.setText(getResources().getString(R.string.login_login));
        }
    }

    private void setUpScene(boolean loginScene) {
        this.loginScene = loginScene;
        if (loginScene) {
            binding.txtTabLogin.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.active_color)));
            binding.viewTabLogin.setBackgroundColor(getResources().getColor(R.color.active_color));
            binding.txtTabSignUp.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.blue_grey_200)));
            binding.viewTabSingUp.setBackgroundColor(getResources().getColor(R.color.transparent_color));
            binding.llSingUp.setVisibility(View.GONE);
            binding.llLogin.setVisibility(View.VISIBLE);
            clearSignUp();
        } else {
            binding.txtTabLogin.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.blue_grey_200)));
            binding.viewTabLogin.setBackgroundColor(getResources().getColor(R.color.transparent_color));
            binding.txtTabSignUp.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.active_color)));
            binding.viewTabSingUp.setBackgroundColor(getResources().getColor(R.color.active_color));
            binding.llSingUp.setVisibility(View.VISIBLE);
            binding.llLogin.setVisibility(View.GONE);
            clearLogin();
        }

    }

    private void clearLogin() {
        binding.etEmail.setText(Config.EMPTY_STRING);
        binding.etPassword.setText(Config.EMPTY_STRING);
    }

    private void clearSignUp() {
        binding.etNameSi.setText(Config.EMPTY_STRING);
        binding.etEmailSi.setText(Config.EMPTY_STRING);
        binding.etPasswordSi.setText(Config.EMPTY_STRING);
        binding.etConfPasswordSi.setText(Config.EMPTY_STRING);
        binding.etNumberSi.setText(Config.EMPTY_STRING);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        if (result.getData() != null) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                if (account != null) {
                                    prgDialog.show();
                                    firebaseAuthWithGoogle(account);
                                    Util.showLog("Google sign in success ");
                                }
                            } catch (ApiException e) {
                                // Google Sign In failed, update UI appropriately
                                Util.showLog("Google sign in failed: " + e);
                                // ...
                            }
                        }
                    }
                }
            });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Util.showLog("firebaseAuthWithGoogle: " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (user != null) {
                            List<? extends UserInfo> userInfoList = user.getProviderData();

                            String email = "";
                            String uid = "";
                            String displayName = "";
                            String photoUrl = "";
                            for (int i = 0; i < userInfoList.size(); i++) {

                                email = userInfoList.get(i).getEmail();

                                if (email != null && !email.equals("")) {
                                    uid = userInfoList.get(i).getUid();
                                    displayName = userInfoList.get(i).getDisplayName();
                                    photoUrl = String.valueOf(userInfoList.get(i).getPhotoUrl());

                                    break;
                                }

                            }

                            userViewModel.setRegisterGoogleUser(displayName, email, photoUrl);

                        } else {
                            // Error Message
                            Toast.makeText(this, getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, getString(R.string.login__fail), Toast.LENGTH_LONG).show();
                        String email = user.getEmail();
                        handleFirebaseAuthError(email);
                    }
                });
    }

    private void handleFirebaseAuthError(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethod = result.getSignInMethods();

                    Util.showLog("SignInMethod  =" + signInMethod);
                    if (signInMethod.contains(Constant.EMAILAUTH)) {
                        dialogMsg.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_email), getString(R.string.ok));
                        dialogMsg.show();
                    } else if (signInMethod.contains(Constant.GOOGLEAUTH)) {
                        dialogMsg.showErrorDialog("[" + email + "]" + getString(R.string.login__auth_google), getString(R.string.ok));
                        dialogMsg.show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}