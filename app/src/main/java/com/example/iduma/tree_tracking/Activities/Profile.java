package com.example.iduma.tree_tracking.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iduma.tree_tracking.Model.PlantingModel;
import com.example.iduma.tree_tracking.Model.SignUpModel;
import com.example.iduma.tree_tracking.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private CircleImageView userImage;
    private ImageView addImage;
    private TextView tvuserName;
    private TextView tvuserLocation;
    private TextView tvuserPoints;
    private String username;

    private DatabaseReference userRef;
    private DatabaseReference treesPlanted;
    private StorageReference userPics;
    private static final int GALLERY_REQUEST =78;
    private String uid;
    private FirebaseUser user;
    private String userId;
    private String noOfTree;
    private int totalTreesPlanted;
    private Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.iv_userImage);
        addImage = findViewById(R.id.iv_add_userimage);
        tvuserName = findViewById(R.id.userName);
        tvuserLocation = findViewById(R.id.userLocation);
        tvuserPoints = findViewById(R.id.userPoint);
        goBack = findViewById(R.id.back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userPics= FirebaseStorage.getInstance().getReference();
        uid = user.getUid();
        Log.d("uid",""+uid);

        treesPlanted = FirebaseDatabase.getInstance().getReference().child("Afforestation");

        treesPlanted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalTreesPlanted=0;
                for (DataSnapshot ds :dataSnapshot.getChildren()){
                    PlantingModel model = ds.getValue(PlantingModel.class);
                    userId = model.getUid();
                    Log.d("userid",""+userId);
                    if (userId.matches(uid)){
                        noOfTree=model.getNoOfTrees();
                        totalTreesPlanted=totalTreesPlanted+Integer.parseInt(noOfTree);
                    }

                    Log.d("nooftree",""+noOfTree);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SignUpModel userModel = dataSnapshot.getValue(SignUpModel.class);
                tvuserName.setText(userModel.getFirstName()+" "+userModel.getLastName());
                tvuserLocation.setText(userModel.getCountry());
                tvuserPoints.setText(""+totalTreesPlanted);
                String user_image = dataSnapshot.child("userImage").getValue(String.class);
                Picasso.with(Profile.this).load(user_image).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galIntent, "Choose picture"), GALLERY_REQUEST);
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)

        {
           Uri imageUri = data.getData();

                StorageReference file = userPics.child("ProfileImages").child(imageUri.getLastPathSegment());
                file.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri imagePath = taskSnapshot.getDownloadUrl();
                        userRef.child(uid).child("userImage").setValue(imagePath.toString());
                        refreshImage();
                        MDToast.makeText(getApplication(),"image Added",
                                MDToast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();

                    }
                });

        }

    }
    private void refreshImage(){

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_image = dataSnapshot.child("userImage").getValue(String.class);
                Picasso.with(Profile.this).load(user_image).into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
