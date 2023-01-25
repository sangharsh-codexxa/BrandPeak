package com.readymadedata.app.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.readymadedata.app.R;

import java.util.Locale;


public class SendOtpActivity extends AppCompatActivity {

    AppCompatButton btnContinue;


    EditText otpEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        btnContinue = findViewById(R.id.btn_continue);
        otpEditText = findViewById(R.id.ed_phoneNumEditext);




        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateMob()) {
                    startActivity(new Intent(SendOtpActivity.this, VerifyOtpActivity.class).putExtra("mobile_num",otpEditText.getText().toString().trim()));
                    finish();
                }
            }
        });

    }

    private boolean validateMob() {

        if(otpEditText.getText().toString().isEmpty()){
            otpEditText.setError("Required");
            return false;
        }else if(otpEditText.getText().toString().length() != 10 ){
            Toast.makeText(this, "10 Digit Mobile Number Required..", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }
}