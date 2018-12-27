package com.example.chiemela.election_waka.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chiemela.election_waka.Model.DeforestationModel;
import com.example.chiemela.election_waka.Model.PlantingModel;
import com.example.chiemela.election_waka.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Statistics extends AppCompatActivity {

    private Button btnRetrieve;
    private TextView tvAfforestEconomics;
    private TextView tvAfforestNonEco;

    private TextView tvDeforestEconomics;
    private TextView tvDeforestNonEco;
    private DatabaseReference treeRef;
    private DatabaseReference defRef;
    private String noEcoAffoTree;
    private String noNEcoAffoTree;
    private String noEcoDefTree;
    private String noNonEcoDefTree;
    private String treeType;
    private String treeTypeDef;
    private int treenoEco;
    private int treenoNonEco;
    private int defTreeNoEco;
    private int defTreeNoNonEco;
    private Button displayAfforestation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvAfforestEconomics = findViewById(R.id.tvNoOfTrees);
        tvAfforestNonEco =findViewById(R.id.tvNoOfTreesN);
        displayAfforestation = findViewById(R.id.displayAfforestation);
        tvDeforestEconomics = findViewById(R.id.tvNoOfEcoTreeReported);
        tvDeforestNonEco = findViewById(R.id.tvNoOfNonEcoReported);


        treeRef= FirebaseDatabase.getInstance().getReference().child("Afforestation");
        defRef = FirebaseDatabase.getInstance().getReference().child("Deforestation");

        treeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                treenoEco=0;
                treenoNonEco=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    PlantingModel model = ds.getValue(PlantingModel.class);
                    treeType = model.getElecionType();
                    Log.d("treeType",""+treeType);

                    if (treeType.matches("Primary")){
                        noEcoAffoTree =model.getHelpReports();
                        //Log.d("ecotree",""+noEcoAffoTree);
                        treenoEco=treenoEco+Integer.parseInt(noEcoAffoTree);
                    }else  if (treeType.matches("General")){
                        noNEcoAffoTree =model.getHelpReports();
                        treenoNonEco=treenoNonEco+Integer.parseInt(noNEcoAffoTree);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        defRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DeforestationModel defmodel = ds.getValue(DeforestationModel.class);
                    treeTypeDef = defmodel.getTypeOfTrees();
                    Log.d("treeType",""+treeType);

                    if (treeTypeDef.matches("Primary")){
                        noEcoDefTree =defmodel.getNoOfTrees();
                        Log.d("ecotree",""+noEcoDefTree);
                        defTreeNoEco=defTreeNoEco+Integer.parseInt(noEcoDefTree);
                    }else  if (treeTypeDef.matches("General")){
                        noNonEcoDefTree =defmodel.getNoOfTrees();
                        defTreeNoNonEco=defTreeNoNonEco+Integer.parseInt(noNonEcoDefTree);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        displayAfforestation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Report",""+treenoEco);
                tvAfforestEconomics.setText(""+treenoEco);
                tvAfforestNonEco.setText(""+treenoNonEco);
                tvDeforestEconomics.setText(""+defTreeNoEco);
                tvDeforestNonEco.setText(""+defTreeNoNonEco);

            }
        });
    }

}
