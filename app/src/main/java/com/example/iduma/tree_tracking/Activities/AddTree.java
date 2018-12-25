package com.example.iduma.tree_tracking.Activities;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iduma.tree_tracking.Model.PlantingModel;
import com.example.iduma.tree_tracking.R;
import com.example.iduma.tree_tracking.Utility.Util;
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

public class AddTree extends AppCompatActivity {
    private ImageView addTreeImage;
    private ImageView displayTree;
    private TextView treeCoordinates;
    private TextView reporterName;
    private EditText uNoofTrees;
    private static final int CAMERA_REQUEST_CODE = 1;
    private int CAMERA_PERMISSION_CODE = 24;
    private String firstname, lastname;
    private double latitude, longitude;
    private AppCompatActivity activity = AddTree.this;
    private Util util = new Util();
    private Button submitTree;
    private Spinner spTreeType;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference addTreeRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private ProgressDialog dialog;
    private StorageReference treeImageRef;
    private DatabaseReference subtree;
    private String noTrees;
    private String treeType;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree);

        mAuth=FirebaseAuth.getInstance();
        addTreeRef = FirebaseDatabase.getInstance().getReference().child("Afforestation");
        treeImageRef = FirebaseStorage.getInstance().getReference().child("xvcxvvvcvx");

        subtree=FirebaseDatabase.getInstance().getReference();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        addTreeImage = findViewById(R.id.iv_add_tree);
        displayTree = findViewById(R.id.iv_treeImages);
        treeCoordinates = findViewById(R.id.tv_tree_coordinates);
        reporterName = findViewById(R.id.tv_person_name);
        uNoofTrees = findViewById(R.id.etNoTrees);
        submitTree = findViewById(R.id.submit_tree);
        spTreeType = findViewById(R.id.spTreeType);
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


        addTreeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }

            }
        });


        treeCoordinates.setText(latitude + ", " + longitude);
        reporterName.setText(lastname + " " + firstname);

     //  final int Trees = Integer.parseInt(noTrees);

        submitTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (util.isNetworkAvailable(activity)) {

                    noTrees = uNoofTrees.getText().toString().trim();
                    treeType = spTreeType.getItemAtPosition(spTreeType.getSelectedItemPosition()).toString();

                    if (TextUtils.isEmpty(noTrees)){
                        MDToast.makeText(getApplication(),"Pls Add No of trees",
                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else if (treeType.equalsIgnoreCase("Select the tree type")){
                        MDToast.makeText(getApplication(),"Pls Select a valid tree type",
                                MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        uploadImage();

                    }


                } else {
                    util.toastMessage(activity, "Check your Network");
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

            Bitmap bitmap= (Bitmap)data.getExtras().get("data");
            displayTree.setImageBitmap(bitmap);

        }

    }

    public void uploadImage(){
        dialog.setMessage("Reporting Afforestation...");
        dialog.show();
        StorageReference mountainsRef = treeImageRef.child("TreeImages").child(uid).child("image.jpg");
        if (displayTree!=null) {

            displayTree.setDrawingCacheEnabled(true);
            displayTree.buildDrawingCache();
            Bitmap bitmap = displayTree.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadURI = taskSnapshot.getDownloadUrl();
                    id = addTreeRef.push().getKey();
                    PlantingModel model = new PlantingModel(uid,lastname + " " + firstname,
                            latitude + ", " + longitude,treeType,noTrees);
                    addTreeRef.child(id).setValue(model);
                    dialog.dismiss();
                    addTreeRef.child(id).child("treeImage").setValue(downloadURI.toString());
                    MDToast.makeText(getApplication(),"Tree Added Successfully",
                            MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

                    Intent intent = new Intent(AddTree.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        } else{
            MDToast.makeText(getApplication(),"Tree image Empty",
                    MDToast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
        }

    }

}
