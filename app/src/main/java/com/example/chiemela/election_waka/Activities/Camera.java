package com.example.chiemela.election_waka.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chiemela.election_waka.Model.PlantingModel;
import com.example.chiemela.election_waka.R;
import com.example.chiemela.election_waka.Utility.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;

public class Camera extends AppCompatActivity {
    private ImageView reportImage;
    private ImageView displayReport;
    private TextView Coordinates;
    private TextView reporterName;
    private static final int CAMERA_REQUEST_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 24;
    private String firstname, lastname;
    private double latitude, longitude;
    private AppCompatActivity activity = Camera.this;
    private Util util = new Util();
    private Button help;
    private Spinner spElectionType,problem;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference addReportRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private ProgressDialog dialog;
    private StorageReference reportImageRef;
    private DatabaseReference db;
    private String helpReports;
    private String elecionType;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mAuth=FirebaseAuth.getInstance();
        addReportRef = FirebaseDatabase.getInstance().getReference().child("Afforestation");
        reportImageRef = FirebaseStorage.getInstance().getReference().child("xvcxvvvcvx");

        db=FirebaseDatabase.getInstance().getReference();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        reportImage = findViewById(R.id.iv_add_report);
        displayReport = findViewById(R.id.iv_Images);
        Coordinates = findViewById(R.id.tv_coordinates);
        reporterName = findViewById(R.id.tv_person_name);
        problem = findViewById(R.id.sphelp);
        help = findViewById(R.id.submit_problem);
        spElectionType = findViewById(R.id.spElectionType);
        dialog= new ProgressDialog(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_PERMISSION_CODE);

        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            firstname = bundle.getString("firstname");
            lastname = bundle.getString("lastname");
            latitude = bundle.getDouble("lat");
            longitude = bundle.getDouble("long");

        }


        reportImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (util.isNetworkAvailable(activity)) {

                    helpReports = problem.getItemAtPosition(problem.getSelectedItemPosition()).toString();
                    elecionType = spElectionType.getItemAtPosition(spElectionType.getSelectedItemPosition()).toString();

                    if (elecionType.equalsIgnoreCase("Select the election type")){
                        MDToast.makeText(getApplication(),"Pls Select a valid election type",
                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                        }

                    }


                } else {
                    util.toastMessage(activity, "Check your Network");
                }



            }
        });


        Coordinates.setText(latitude + ", " + longitude);
        reporterName.setText(lastname + " " + firstname);

     //  final int Trees = Integer.parseInt(noTrees);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (util.isNetworkAvailable(activity)) {
                    if (helpReports.equalsIgnoreCase("Use this field only when in trouble")){
                        MDToast.makeText(getApplication(),"Pls select a problem",
                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    }else {
                        util.toastMessage(activity, "Check your Network");
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Displaying a toast
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

            } else {
                util.toastMessage(activity, "Oops you just denied the permission");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {


            uploadImage();

            Bitmap bitmap= (Bitmap)data.getExtras().get("data");
            displayReport.setImageBitmap(bitmap);

        }

    }

    public void uploadImage(){
        dialog.setMessage("Reporting...");
        dialog.show();
        StorageReference mountainsRef = reportImageRef.child("ReportedImages").child(uid).child("image.jpg");
        if (displayReport!=null) {

            displayReport.setDrawingCacheEnabled(true);
            displayReport.buildDrawingCache();
            Bitmap bitmap = displayReport.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURI = taskSnapshot.getDownloadUrl();
                    id = addReportRef.push().getKey();
                    PlantingModel model = new PlantingModel(uid,lastname + " " + firstname,
                            latitude + ", " + longitude,elecionType,helpReports);
                    addReportRef.child(id).setValue(model);
                    dialog.dismiss();
                    addReportRef.child(id).child("Election Scene").setValue(downloadURI.toString());
                    MDToast.makeText(getApplication(),"Reported Successfully",
                            MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

                    Intent intent = new Intent(Camera.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        } else{
            MDToast.makeText(getApplication()," image Empty",
                    MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
        }

    }

}
