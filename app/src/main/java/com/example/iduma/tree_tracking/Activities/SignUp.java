package com.example.iduma.tree_tracking.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.iduma.tree_tracking.Model.SignUpModel;
import com.example.iduma.tree_tracking.R;
import com.example.iduma.tree_tracking.Utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.valdesekamdem.library.mdtoast.MDToast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUp extends AppCompatActivity {

    private AppCompatActivity activity = SignUp.this;
    private EditText etFname, etLname, etPassword;
    private EditText etPhone,etEmail;
    private SearchableSpinner spGender, spAccount;
    private Button btnSignup;
    private TextView reg, login;
    private CountryCodePicker spCountry;
    ProgressBar bar;
    ProgressDialog progressDialog;
    SweetAlertDialog pd;
    private KProgressHUD hud;
    private Util util = new Util();
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spCountry = findViewById(R.id.spCountry);
        spGender = findViewById(R.id.spGender);
        spAccount = findViewById(R.id.spAccountType);
        reg = findViewById(R.id.tvReg);
        login = findViewById(R.id.tvLogin);
        btnSignup = findViewById(R.id.btnSignup);

        mAuth = FirebaseAuth.getInstance();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(SignUp.this);
        bar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.setTextColor(getResources().getColor(R.color.white));

                Intent intent = new Intent(SignUp.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String phone = etPhone.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String firstName = etFname.getText().toString().trim();
                final String lastName = etLname.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();
                final String gender = spGender.getItemAtPosition(spGender.getSelectedItemPosition()).toString();
                final String accountType = spAccount.getItemAtPosition(spAccount.getSelectedItemPosition()).toString();
                final String country = spCountry.getSelectedCountryName().toString();

                if (TextUtils.isEmpty(phone)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Phone number cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (TextUtils.isEmpty(firstName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Enter your First name cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Last name cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Email cannot be empty")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Password cannot be less than 5")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (gender.equalsIgnoreCase("Select Gender")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Please select your gender")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (accountType.equalsIgnoreCase("Select Account Type")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Please select your Account type")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                if (country.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setMessage("Please select your Country")
                            .setTitle("Oops!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                else {
                    progressDialog.setMessage("Signing up user...");
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (!task.isSuccessful()){
                                MDToast.makeText(getApplication(),"Sign up not successful",
                                        MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                            } else{
                                MDToast.makeText(getApplication(),"Sign up successful",
                                        MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                uId = user.getUid();

                                SignUpModel signup = new SignUpModel(firstName,lastName,email,password,
                                        gender,accountType,phone,country);
                                usersRef.child(uId).setValue(signup);
                                Intent signin = new Intent(SignUp.this,SignIn.class);
                                signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(signin);

                            }

                        }
                    });

                }
            }



});

    }

}