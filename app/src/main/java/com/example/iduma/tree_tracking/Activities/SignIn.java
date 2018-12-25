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
import android.widget.TextView;

import com.example.iduma.tree_tracking.R;
import com.example.iduma.tree_tracking.Utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;


public class SignIn extends AppCompatActivity {

    private AppCompatActivity activity = SignIn.this;
    private TextView tvReg, tvLogin, tvReset;
    private EditText etEmail1, etPassword1;
    private Button btnLogin1;
    private KProgressHUD hud;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    private Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            startActivity(new Intent(SignIn.this, Home.class));
            finish();
        }



        tvReg = findViewById(R.id.tvReg);
        tvLogin = findViewById(R.id.tvLogin);
        etPassword1 = findViewById(R.id.etPassword1);
        etEmail1 = findViewById(R.id.etemail1);
        btnLogin1 = findViewById(R.id.btnLogin1);
        tvReset = (TextView)findViewById(R.id.tvReset);


        progressDialog = new ProgressDialog(SignIn.this);

        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(SignIn.this, ResetPassword.class);
                reg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(reg);
                finish();
            }
        });
        btnLogin1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = etEmail1.getText().toString().trim();
                final String password = etPassword1.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    MDToast.makeText(getApplication(),"email is empty",
                            MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                } else if (TextUtils.isEmpty(password)){
                    MDToast.makeText(getApplication(),"password is empty",
                            MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                } else {
                    progressDialog.setMessage("Signing user in...");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()){
                                        MDToast.makeText(getApplication(),"incorrect email/password",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                                    } else
                                    {
                                        MDToast.makeText(getApplication(),"SignIn Successful",
                                                MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                                        Intent intent = new Intent(SignIn.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                }
                            });


                }


            }
        });

        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(SignIn.this, SignUp.class);
                reg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(reg);
            }
        });
    }



}

