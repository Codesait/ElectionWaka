package com.example.iduma.tree_tracking.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.iduma.tree_tracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.valdesekamdem.library.mdtoast.MDToast;

public class ResetPassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        mDialog = new ProgressDialog(ResetPassword.this);


        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPassword.this, SignIn.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    MDToast.makeText(getApplicationContext(),"Enter your registered email id",
                            MDToast.LENGTH_SHORT,MDToast.TYPE_ERROR).show();
                    return;
                }
                mDialog.setMessage("Resetting password...");
                mDialog.show();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDialog.dismiss();
                                if (task.isSuccessful()) {
                                    MDToast.makeText(getApplication(),"Check your email to reset your password",
                                            MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                                    startActivity(new Intent(ResetPassword.this, SignIn.class));
                                    finish();
                                } else {
                                    MDToast.makeText(getApplication(),"Failed to send reset Email",
                                            MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();                                }

                            }
                        });
            }
        });
    }
}
